package ch.supsi.fscli.model.commands;

import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.data_access.CommandResultStatus;
import ch.supsi.fscli.model.command_management.CommandInfo;
import ch.supsi.fscli.model.command_management.LocalizedParseException;
import ch.supsi.fscli.model.command_management.ParsedCommand;
import ch.supsi.fscli.model.command_management.PathResolver;
import ch.supsi.fscli.model.inode.DirectoryINode;
import ch.supsi.fscli.model.inode.FileSystemComponent;
import ch.supsi.fscli.model.inode.INode;
import ch.supsi.fscli.model.inode.SymlinkINode;

import java.text.ParseException;
import java.util.*;

@CommandInfo(
        name = "ln",
        signatureKey = "command.ln.signature",
        descriptionKey = "command.ln.description"
)
public class LnCommand implements ICommand {
    private static final Set<String> VALID_FLAGS = Set.of("-s");

    @Override
    public ParsedCommand parse(List<String> args) throws ParseException {
        boolean symbolic = false;
        List<String> arguments = new ArrayList<>();

        // 1. Parsing: Estrazione flag e argomenti
        for (String arg : args) {
            if (VALID_FLAGS.contains(arg)) {
                symbolic = true;
            } else if (arg.startsWith("-")) {
                throw new LocalizedParseException("label.ln.invalidOption", arg.substring(1));
            } else {
                arguments.add(arg);
            }
        }

        // 2. Validazione sintassi: Richiede esattamente 2 argomenti (target e link)
        if (arguments.size() != 2) {
            throw new ParseException("label.ln.usage", 0);
        }

        // 3. Creazione del comando parsato
        Map<String, Boolean> flags = new HashMap<>();
        if (symbolic) {
            flags.put("s", true);
        }

        return new ParsedCommand("ln", arguments, flags);
    }

    @Override
    public CommandResult execute(ParsedCommand cmd) {
        List<String> args = cmd.getArguments();
        boolean isSymbolic = cmd.getFlag("s");

        String targetPath = args.get(0);
        String linkPath = args.get(1);

        // 4. Validazione path vuoti
        if (targetPath == null || targetPath.trim().isEmpty()) {
            return new CommandResult("label.ln.emptyTarget", CommandResultStatus.ERROR);
        }
        if (linkPath == null || linkPath.trim().isEmpty()) {
            return new CommandResult("label.ln.emptyLink", CommandResultStatus.ERROR);
        }

        try {
            // 5. Delega all'implementazione specifica (Hard link o Symbolic link)
            if (isSymbolic) {
                return createSymbolicLink(targetPath, linkPath);
            } else {
                return createHardLink(targetPath, linkPath);
            }
        } catch (IllegalArgumentException e) {
            // 6. Gestisce errori di PathResolver
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.startsWith("label.")) {
                return new CommandResult(errorMsg, CommandResultStatus.ERROR);
            }
            return new CommandResult("label.ln.pathError", CommandResultStatus.ERROR, errorMsg);
        }
    }

    private CommandResult createHardLink(String targetPath, String linkPath) {
        // --- 1. RISOLUZIONE TARGET ---
        DirectoryINode targetParent = PathResolver.resolve(targetPath);
        String targetName = PathResolver.getFileName(targetPath);

        if (targetName.isEmpty() || targetName.equals(".") || targetName.equals("..")) {
            return new CommandResult("label.ln.invalidTarget", CommandResultStatus.ERROR, targetPath);
        }

        FileSystemComponent targetComp = targetParent.findEntry(targetName);
        if (targetComp == null) {
            return new CommandResult("label.ln.targetNotFound", CommandResultStatus.ERROR, targetPath);
        }

        // Hard link non consentiti sulle directory
        if (targetComp instanceof DirectoryINode) {
            return new CommandResult("label.ln.directoryNotAllowed", CommandResultStatus.ERROR, targetPath);
        }

        // --- 2. RISOLUZIONE LINK ---
        DirectoryINode linkParent = PathResolver.resolve(linkPath);
        String linkName = PathResolver.getFileName(linkPath);

        if (linkName.isEmpty() || linkName.equals(".") || linkName.equals("..")) {
            return new CommandResult("label.ln.invalidLinkName", CommandResultStatus.ERROR, linkPath);
        }

        // Controllo se esiste già un file/link con lo stesso nome
        FileSystemComponent existing = linkParent.findEntry(linkName);
        if (existing != null) {
            return new CommandResult("label.ln.fileExists", CommandResultStatus.ERROR, linkPath);
        }

        // --- 3. CREAZIONE LINK ---
        // Aggiunge l'entry nella directory del link puntando allo stesso INode del target
        if (!linkParent.addEntry(linkName, targetComp)) {
            return new CommandResult("label.ln.addFailed", CommandResultStatus.ERROR, linkPath);
        }

        // 4. Incrementa il conteggio dei link sull'INode target
        if (targetComp instanceof INode) {
            targetComp.incrementLinkCount();
        }

        return new CommandResult("", CommandResultStatus.SUCCESS);
    }

    private CommandResult createSymbolicLink(String targetPath, String linkPath) {
        // --- 1. RISOLUZIONE LINK ---
        // I Symbolic Link non richiedono l'esistenza del target
        DirectoryINode linkParent = PathResolver.resolve(linkPath);
        String linkName = PathResolver.getFileName(linkPath);

        if (linkName.isEmpty() || linkName.equals(".") || linkName.equals("..")) {
            return new CommandResult("label.ln.invalidLinkName", CommandResultStatus.ERROR, linkPath);
        }

        FileSystemComponent existing = linkParent.findEntry(linkName);
        if (existing != null) {
            return new CommandResult("label.ln.fileExists", CommandResultStatus.ERROR, linkPath);
        }

        // --- 2. CREAZIONE SYMLINK ---
        // Crea un nuovo INode di tipo Symlink che memorizza solo il path target come stringa
        SymlinkINode symlink = new SymlinkINode(targetPath);

        // Aggiunge l'entry nella directory
        if (!linkParent.addEntry(linkName, symlink)) {
            return new CommandResult("label.ln.addFailed", CommandResultStatus.ERROR, linkPath);
        }

        return new CommandResult("", CommandResultStatus.SUCCESS);
    }
}