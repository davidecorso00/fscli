package ch.supsi.fscli.model.commands;

import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.data_access.CommandResultStatus;
import ch.supsi.fscli.model.CommandsList;
import ch.supsi.fscli.model.command_management.CommandInfo;
import ch.supsi.fscli.model.command_management.ParsedCommand;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;


@CommandInfo(
        name = "help",
        signatureKey = "command.help.signature",
        descriptionKey = "command.help.description"
)
public class HelpCommand implements ICommand {
    @Override
    public ParsedCommand parse(List<String> args) throws ParseException {
        // 1. Parsing: Assicura che non ci siano argomenti
        if (!args.isEmpty()) {
            throw new ParseException("label.help.usage", 0);
        }
        return new ParsedCommand("help", Collections.emptyList());
    }

    @Override
    public CommandResult execute(ParsedCommand cmd) {
        // 2. Esecuzione: Richiede al Model (CommandsList) la lista formattata di tutti i comandi
        return new CommandResult(CommandsList.getInstance().getAllCommands(), CommandResultStatus.SUCCESS);
    }
}