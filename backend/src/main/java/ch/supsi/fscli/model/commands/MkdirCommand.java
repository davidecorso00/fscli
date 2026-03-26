package ch.supsi.fscli.model.commands;

import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.data_access.CommandResultStatus;
import ch.supsi.fscli.model.command_management.CommandInfo;
import ch.supsi.fscli.model.command_management.ParsedCommand;
import ch.supsi.fscli.model.command_management.PathResolver;
import ch.supsi.fscli.model.inode.DirectoryINode;
import ch.supsi.fscli.model.inode.FileSystemComponent;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        name = "mkdir",
        signatureKey = "command.mkdir.signature",
        descriptionKey = "command.mkdir.description"
)
public class MkdirCommand implements ICommand {

    // Classe interna per memorizzare gli errori di creazione
    private static class CreationError {
        String messageKey;
        Object[] args;

        CreationError(String messageKey, Object... args) {
            this.messageKey = messageKey;
            this.args = args;
        }
    }

    @Override
    public ParsedCommand parse(List<String> args) throws ParseException {
        if (args.isEmpty()) {
            throw new ParseException("label.mkdir.usage", 0);
        }

        return new ParsedCommand("mkdir", args);
    }

    @Override
    public CommandResult execute(ParsedCommand cmd) {

        List<CreationError> errors = new ArrayList<>();

        // 1. Itera su tutti i path da creare
        for (String pathArgument : cmd.getArguments()) {

            try {
                // 2. Risoluzione: trova la directory genitore e il nome della nuova directory
                DirectoryINode parentDir = PathResolver.resolve(pathArgument);
                String newDirName = PathResolver.getFileName(pathArgument);

                // 3. Validazione del nome (non può essere . o ..)
                if (newDirName.equals(".") || newDirName.equals("..")) {
                    errors.add(new CreationError("label.mkdir.invalidName", pathArgument));
                    continue;
                }

                // 4. Controllo esistenza
                FileSystemComponent existing = parentDir.findEntry(newDirName);
                if (existing != null) {
                    errors.add(new CreationError("label.mkdir.fileExists", pathArgument));
                    continue;
                }

                // 5. Creazione e aggiunta
                DirectoryINode newDir = new DirectoryINode(parentDir);
                boolean added = parentDir.addEntry(newDirName, newDir);

                if (!added) {
                    errors.add(new CreationError("label.mkdir.cannotCreate", pathArgument));
                }

            } catch (IllegalArgumentException e) {
                String msg = e.getMessage();
                if (msg != null && msg.startsWith("label.")) {
                    errors.add(new CreationError(msg, pathArgument));
                } else {
                    errors.add(new CreationError("label.mkdir.pathError", pathArgument, msg));
                }
            }
        }

        if (errors.isEmpty()) {
            return new CommandResult("", CommandResultStatus.SUCCESS);
        } else {
            CreationError firstError = errors.get(0);
            return new CommandResult(firstError.messageKey, CommandResultStatus.ERROR, firstError.args);
        }
    }
}