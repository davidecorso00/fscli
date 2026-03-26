package ch.supsi.fscli.model.commands;

import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.data_access.CommandResultStatus;
import ch.supsi.fscli.model.command_management.CommandInfo;
import ch.supsi.fscli.model.command_management.ParsedCommand;
import ch.supsi.fscli.model.command_management.PathResolver;
import ch.supsi.fscli.model.inode.DirectoryINode;
import ch.supsi.fscli.model.inode.FileSystem;
import ch.supsi.fscli.model.inode.FileSystemComponent;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        name = "rmdir",
        signatureKey = "command.rmdir.signature",
        descriptionKey = "command.rmdir.description"
)
public class RmDirCommand implements ICommand {

    // Classe interna per memorizzare gli errori di rimozione
    private static class RemovalError {
        String messageKey;
        Object[] args;

        RemovalError(String messageKey, Object... args) {
            this.messageKey = messageKey;
            this.args = args;
        }
    }

    @Override
    public ParsedCommand parse(List<String> args) throws ParseException {
        // 1. Parsing: Richiede almeno un argomento
        if (args.isEmpty()) {
            throw new ParseException("label.rmdir.usage", 0);
        }

        // 2. Espansione delle wildcard
        List<String> expandedArgs = PathResolver.expandWildcards(args);

        if (expandedArgs.isEmpty()) {
            throw new ParseException("label.rmdir.missingOperand", 0);
        }

        return new ParsedCommand("rmdir", expandedArgs);
    }

    @Override
    public CommandResult execute(ParsedCommand cmd) {
        FileSystem fs = FileSystem.getInstance();

        if (cmd.getArguments() == null || cmd.getArguments().isEmpty()) {
            return new CommandResult("label.rmdir.missingOperand", CommandResultStatus.ERROR);
        }

        List<RemovalError> errors = new ArrayList<>();

        // 3. Itera su tutti i path da rimuovere
        for (String pathArgument : cmd.getArguments()) {
            if (pathArgument == null || pathArgument.trim().isEmpty()) {
                errors.add(new RemovalError("label.rmdir.emptyPath"));
                continue;
            }

            try {
                // 4. Risoluzione: trova il padre e il nome della directory
                DirectoryINode parentDir = PathResolver.resolve(pathArgument);
                String dirname = PathResolver.getFileName(pathArgument);

                // 5. Controlli: impedisce la rimozione della root, di . e ..
                if (dirname.isEmpty()) {
                    errors.add(new RemovalError("label.rmdir.cannotRemoveRoot"));
                    continue;
                }

                if (dirname.equals(".") || dirname.equals("..")) {
                    errors.add(new RemovalError("label.rmdir.invalidName", dirname));
                    continue;
                }

                // 6. Verifica esistenza e tipo
                FileSystemComponent component = parentDir.findEntry(dirname);

                if (component == null) {
                    errors.add(new RemovalError("label.rmdir.noSuchFile", pathArgument));
                    continue;
                }

                if (!(component instanceof DirectoryINode directory)) {
                    errors.add(new RemovalError("label.rmdir.notDirectory", pathArgument));
                    continue;
                }

                // 7. Controllo se è la directory di lavoro corrente (non si può rimuovere)
                if (directory == fs.getCurrentWorkingDirectory()) {
                    errors.add(new RemovalError("label.rmdir.currentDir", pathArgument));
                    continue;
                }

                // 8. Controllo se la directory è vuota
                if (!directory.isEmpty()) {
                    errors.add(new RemovalError("label.rmdir.notEmpty", pathArgument));
                    continue;
                }

                // 9. Rimozione dell'entry dalla directory genitore
                FileSystemComponent removed = parentDir.removeEntry(dirname);

                if (removed == null) {
                    errors.add(new RemovalError("label.rmdir.removeFailed", pathArgument));
                }

            } catch (IllegalArgumentException e) {
                // 10. Gestione errori di PathResolver
                String errorMsg = e.getMessage();
                if (errorMsg != null && errorMsg.startsWith("label.")) {
                    errors.add(new RemovalError(errorMsg, pathArgument));
                } else {
                    errors.add(new RemovalError("label.rmdir.pathError", pathArgument, errorMsg));
                }
            }
        }

        // 11. Risultato finale
        if (errors.isEmpty()) {
            return new CommandResult("", CommandResultStatus.SUCCESS);
        } else {
            RemovalError firstError = errors.get(0);
            return new CommandResult(firstError.messageKey, CommandResultStatus.ERROR, firstError.args);
        }
    }
}