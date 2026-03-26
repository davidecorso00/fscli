package ch.supsi.fscli.model.commands;

import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.data_access.CommandResultStatus;
import ch.supsi.fscli.model.command_management.CommandInfo;
import ch.supsi.fscli.model.command_management.LocalizedParseException;
import ch.supsi.fscli.model.command_management.ParsedCommand;
import ch.supsi.fscli.model.command_management.PathResolver;
import ch.supsi.fscli.model.inode.DirectoryINode;
import ch.supsi.fscli.model.inode.FileSystemComponent;

import java.text.ParseException;
import java.util.*;

@CommandInfo(
        name = "ls",
        signatureKey = "command.ls.signature",
        descriptionKey = "command.ls.description"
)
public class LsCommand implements ICommand {

    private static final Set<String> VALID_FLAGS = Set.of("-i", "-a");

    @Override
    public ParsedCommand parse(List<String> args) throws ParseException {
        List<String> paths = new ArrayList<>();
        Map<String, Boolean> foundFlags = new HashMap<>();

        // 1. Parsing: Estrazione flag e path
        for (String arg : args) {
            if (arg.startsWith("-")) {
                // Gestisci flag composti come -ia
                String flagStr = arg.substring(1);
                for (char c : flagStr.toCharArray()) {
                    String flag = "-" + c;
                    if (VALID_FLAGS.contains(flag)) {
                        foundFlags.put(String.valueOf(c), true);
                    } else {
                        throw new LocalizedParseException("label.ls.invalidOption", String.valueOf(c));
                    }
                }
            } else {
                paths.add(arg);
            }
        }

        // 2. Default: Se non viene fornito nessun path, usa "."
        if (paths.isEmpty()) {
            paths.add(".");
        }

        // 3. Espansione delle wildcard nei path
        List<String> expandedPaths = PathResolver.expandWildcards(paths);

        // 4. Creazione del comando parsato
        ParsedCommand cmd = new ParsedCommand("ls", expandedPaths);
        foundFlags.forEach(cmd::setFlag);

        return cmd;
    }

    @Override
    public CommandResult execute(ParsedCommand cmd) {
        List<String> paths = cmd.getArguments();
        boolean showInode = cmd.getFlag("i");
        boolean showHidden = cmd.getFlag("a");

        StringBuilder output = new StringBuilder();
        List<String> errors = new ArrayList<>();

        // 1. Itera su tutti i path risolti (inclusi quelli da wildcard)
        for (int index = 0; index < paths.size(); index++) {
            String path = paths.get(index);

            try {
                // 2. Trovo il padre del path e l'ultimo nome
                DirectoryINode parent = PathResolver.resolve(path);
                String last = PathResolver.getFileName(path);

                // FIX 3: Gestisci anche .. (se path è ".", "..", o termina con "/")
                boolean listParent = (last.isEmpty() || last.equals(".") || last.equals(".."));

                DirectoryINode targetDir;
                if (listParent) {
                    targetDir = parent;
                } else {
                    FileSystemComponent component = parent.findEntry(last);

                    if (component == null) {
                        errors.add("ls: cannot access '" + path + "': No such file or directory");
                        continue;
                    }

                    if (component instanceof DirectoryINode) {
                        targetDir = (DirectoryINode) component;
                    } else {
                        // LS su singolo file
                        output.append(formatEntry(component, last, showInode)).append("\n");
                        continue;
                    }
                }

                // Header per multipli path
                if (paths.size() > 1) {
                    output.append(path).append(":\n");
                }

                // 3. Contenuto directory
                List<String> names = new ArrayList<>(targetDir.getAllEntryNames());
                names.sort(String::compareTo); // Ordina alfabeticamente

                for (String name : names) {
                    // 4. Gestione flag -a (mostra . e .. e file nascosti)
                    if (!showHidden) {
                        if (name.equals("..") || name.startsWith(".")) {
                            continue;
                        }
                    }

                    FileSystemComponent entry = targetDir.findEntry(name);
                    output.append(formatEntry(entry, name, showInode)).append("\n");
                }

                // Aggiunge un accapo tra le liste se ci sono più path
                if (index < paths.size() - 1) {
                    output.append("\n");
                }

            } catch (IllegalArgumentException e) {
                String errorMsg = e.getMessage();
                if (errorMsg != null && errorMsg.startsWith("label.")) {
                    errors.add("ls: cannot access '" + path + "': Invalid path");
                } else {
                    errors.add("ls: cannot access '" + path + "': " + errorMsg);
                }
            }
        }

        // 5. Stampa errori alla fine (comportamento Unix)
        if (!errors.isEmpty()) {
            for (String error : errors) {
                output.append(error).append("\n");
            }
        }
        return new CommandResult(output.toString().stripTrailing(), CommandResultStatus.SUCCESS);
    }

    // 6. Formatta l'output (con o senza INode ID)
    private String formatEntry(FileSystemComponent comp, String name, boolean showInode) {
        if (!showInode) return name;
        return comp.getId() + " " + name;
    }
}