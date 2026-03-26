package ch.supsi.fscli.model.commands;

import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.data_access.CommandResultStatus;
import ch.supsi.fscli.model.command_management.CommandInfo;
import ch.supsi.fscli.model.command_management.ParsedCommand;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

@CommandInfo(
        name = "clear",
        signatureKey = "command.clear.signature",
        descriptionKey = "command.clear.description"
)
public class ClearCommand implements ICommand {
    @Override
    public ParsedCommand parse(List<String> args) throws ParseException {
        // 1. Parsing: Assicura che non ci siano argomenti
        if (!args.isEmpty()) {
            throw new ParseException("label.clear.usage", 0);
        }
        return new ParsedCommand("clear", Collections.emptyList(), null);
    }

    @Override
    public CommandResult execute(ParsedCommand cmd) {
        // 2. Esecuzione: Ritorna un risultato con lo stato speciale CLEAR_OUTPUT
        return new CommandResult("", CommandResultStatus.CLEAR_OUTPUT);
    }
}