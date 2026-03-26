package ch.supsi.fscli.model.commands;

import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.data_access.CommandResultStatus;
import ch.supsi.fscli.model.command_management.CommandInfo;
import ch.supsi.fscli.model.command_management.ParsedCommand;
import ch.supsi.fscli.model.inode.FileSystem;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;


@CommandInfo(
        name = "pwd",
        signatureKey = "command.pwd.signature",
        descriptionKey = "command.pwd.description"
)
public class PwdCommand implements ICommand {
    @Override
    public ParsedCommand parse(List<String> args) throws ParseException {
        // 1. Parsing: Assicura che non ci siano argomenti
        if (!args.isEmpty()) {
            throw new ParseException("label.pwd.usage", 0);
        }
        return new ParsedCommand("pwd", Collections.emptyList());
    }

    @Override
    public CommandResult execute(ParsedCommand cmd) {
        // 2. Esecuzione: Recupera il path assoluto della directory corrente
        String path = FileSystem.getInstance()
                .getCurrentWorkingDirectory()
                .getAbsolutePath();
        return new CommandResult(path, CommandResultStatus.SUCCESS);
    }
}