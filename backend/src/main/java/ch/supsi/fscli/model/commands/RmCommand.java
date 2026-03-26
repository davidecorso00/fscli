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
        name = "rm",
        signatureKey = "command.rm.signature",
        descriptionKey = "command.rm.description"
)
public class RmCommand implements ICommand {

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
            throw new ParseException("label.rm.usage", 0);
        }

        // 2. Espansione delle wildcard
        List<String> expandedArgs = PathResolver.expandWildcards(args);

        if (expandedArgs.isEmpty()) {
            throw new ParseException("label.rm.missingOperand", 0);
        }

        return new ParsedCommand("rm", expandedArgs);
    }

    @Override
    public CommandResult execute(ParsedCommand cmd) {
        List<String> arguments = cmd.getArguments();

        if (arguments == null || arguments.isEmpty()) {
            return new CommandResult("label.rm.missingOperand", CommandResultStatus.ERROR);
        }

        List<RemovalError> errors = new ArrayList<>();

        // 3. Itera su tutti i file da rimuovere
        for (String pathArgument : arguments) {
            if (pathArgument == null || pathArgument.trim().isEmpty()) {
                errors.add(new RemovalError("label.rm.emptyArg"));
                continue;
            }

            try {
                // 4. Risoluzione: trova il padre e il nome del file
                DirectoryINode parentDir = PathResolver.resolve(pathArgument);
                String filename = PathResolver.getFileName(pathArgument);

                // 5. Controlli: impedisce la rimozione della root o di "." e ".."
                if (filename.isEmpty()) {
                    errors.add(new RemovalError("label.rm.cannotRemoveRoot"));
                    continue;
                }

                if (filename.equals(".") || filename.equals("..")) {
                    errors.add(new RemovalError("label.rm.refuseDot", filename));
                    continue;
                }

                // 6. Verifica esistenza e tipo
                FileSystemComponent component = parentDir.findEntry(filename);

                if (component == null) {
                    errors.add(new RemovalError("label.rm.noSuchFile", pathArgument));
                    continue;
                }

                // rm non può rimuovere directory (salvo implementazione di -r)
                if (component instanceof DirectoryINode) {
                    errors.add(new RemovalError("label.rm.isDirectory", pathArgument));
                    continue;
                }

                // 7. Rimozione dell'entry dalla directory
                FileSystemComponent removed = parentDir.removeEntry(filename);

                if (removed == null) {
                    errors.add(new RemovalError("label.rm.removeFailed", pathArgument));
                }

                // NOTA: La rimozione dell'entry in DirectoryINode decrementa il link count
                // La riga sotto appare ridondante e potrebbe causare un decremento doppio:
                /*
                if (removed instanceof INode) {
                    ((INode) removed).decrementLinkCount();
                }
                */

            } catch (IllegalArgumentException e) {
                // 8. Gestione errori di PathResolver
                String errorMsg = e.getMessage();
                if (errorMsg != null && errorMsg.startsWith("label.")) {
                    errors.add(new RemovalError(errorMsg, pathArgument));
                } else {
                    errors.add(new RemovalError("label.rm.pathError", pathArgument, errorMsg));
                }
            }
        }

        // 9. Risultato finale
        if (errors.isEmpty()) {
            return new CommandResult("", CommandResultStatus.SUCCESS);
        } else {
            RemovalError firstError = errors.get(0);
            return new CommandResult(firstError.messageKey, CommandResultStatus.ERROR, firstError.args);
        }
    }
}