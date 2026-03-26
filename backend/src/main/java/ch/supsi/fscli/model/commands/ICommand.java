package ch.supsi.fscli.model.commands;

import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.model.command_management.ParsedCommand;

import java.text.ParseException;
import java.util.List;

public interface ICommand {
    ParsedCommand parse(List<String> args) throws ParseException;
    CommandResult execute(ParsedCommand cmd);
}
