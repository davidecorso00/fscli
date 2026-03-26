package ch.supsi.fscli.business.command_execution;


import ch.supsi.fscli.business.translations.TranslationsLogic;
import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.model.command_management.CommandExecutor;

public class CommandExecutionLogic implements ICommandExecutionLogic {
    private static CommandExecutionLogic myself;

    public static CommandExecutionLogic getInstance() {
        if (myself == null) {
            myself = new CommandExecutionLogic();
        }
        return myself;
    }

    @Override
    public CommandResult executeCommand(String command) {
        CommandExecutor commandExecutor = CommandExecutor.getInstance();

        // 1. Esecuzione logica (Model)
        // Il risultato contiene la chiave di traduzione (es. "label.error"), non il testo finale
        CommandResult rawResult = commandExecutor.executeCommand(command);
        TranslationsLogic translator = TranslationsLogic.getInstance();

        // 2. Traduzione del messaggio
        String translatedMessage = translator.translate(
                rawResult.getMessage(),
                rawResult.getArgs()
        );

        // 3. Restituzione risultato elaborato
        return new CommandResult(translatedMessage, rawResult.getStatus());
    }
}