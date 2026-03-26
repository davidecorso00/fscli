package ch.supsi.fscli.model.commands;

import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.data_access.CommandResultStatus;
import ch.supsi.fscli.model.command_management.CommandInfo;
import ch.supsi.fscli.model.command_management.ParsedCommand;
import ch.supsi.fscli.model.command_management.PathResolver;
import ch.supsi.fscli.model.inode.DirectoryINode;
import ch.supsi.fscli.model.inode.FileINode;
import ch.supsi.fscli.model.inode.FileSystemComponent;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        name = "touch",
        signatureKey = "command.touch.signature",
        descriptionKey = "command.touch.description"
)
public class TouchCommand implements ICommand {

    // Classe interna per memorizzare gli errori di creazione
    private static class TouchError {
        String messageKey;
        Object[] args;

        TouchError(String messageKey, Object... args) {
            this.messageKey = messageKey;
            this.args = args;
        }
    }

    @Override
    public ParsedCommand parse(List<String> args) throws ParseException {
        // 1. Parsing: Richiede almeno un argomento
        if (args.isEmpty()) {
            throw new ParseException("label.touch.usage", 0);
        }

        // 2. Espansione delle wildcard
        List<String> expandedArgs = PathResolver.expandWildcards(args);

        if (expandedArgs.isEmpty()) {
            throw new ParseException("label.touch.missingFileOperand", 0);
        }

        return new ParsedCommand("touch", expandedArgs);
    }

    @Override
    public CommandResult execute(ParsedCommand cmd) {
        if (cmd.getArguments() == null || cmd.getArguments().isEmpty()) {
            return new CommandResult("label.touch.missingFileOperand", CommandResultStatus.ERROR);
        }

        List<TouchError> errors = new ArrayList<>();

        // 3. Itera su tutti i path
        for (String path : cmd.getArguments()) {
            if (path == null || path.trim().isEmpty()) {
                errors.add(new TouchError("label.touch.emptyPath"));
                continue;
            }

            // 4. Controllo sulla slash finale (non si possono creare file che finiscono con '/')
            if (path.endsWith("/")) {
                errors.add(new TouchError("label.touch.invalidTrailingSlash", path));
                continue;
            }

            try {
                // 5. Risoluzione: trova il padre e il nome del file
                DirectoryINode parent = PathResolver.resolve(path);
                String fileName = PathResolver.getFileName(path);

                // 6. Controlli: impedisce touch sulla root o su . e ..
                if (fileName.isEmpty()) {
                    errors.add(new TouchError("label.touch.cannotTouchRoot"));
                    continue;
                }

                if (fileName.equals(".") || fileName.equals("..")) {
                    errors.add(new TouchError("label.touch.invalidName", fileName));
                    continue;
                }

                // 7. Controllo esistenza
                FileSystemComponent existing = parent.findEntry(fileName);
                if (existing != null) {
                    // File già esiste, operazione riuscita (simula l'aggiornamento del timestamp)
                    continue;
                }

                // 8. Creazione del nuovo file e aggiunta alla directory
                FileINode newFile = new FileINode();
                boolean added = parent.addEntry(fileName, newFile);

                if (!added) {
                    errors.add(new TouchError("label.touch.cannotCreate", path));
                }

            } catch (IllegalArgumentException e) {
                // 9. Gestione errori di PathResolver
                String msg = e.getMessage();
                if (msg != null && msg.startsWith("label.")) {
                    errors.add(new TouchError(msg, path));
                } else {
                    errors.add(new TouchError("label.touch.pathError", path, msg));
                }
            }
        }

        // 10. Risultato finale
        if (errors.isEmpty()) {
            return new CommandResult("", CommandResultStatus.SUCCESS);
        } else {
            TouchError firstError = errors.get(0);
            return new CommandResult(firstError.messageKey, CommandResultStatus.ERROR, firstError.args);
        }
    }
}