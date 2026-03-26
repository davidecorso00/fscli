package ch.supsi.fscli.business.command_execution;

import ch.supsi.fscli.data_access.CommandResult;

public interface ICommandExecutionLogic {
    CommandResult executeCommand(String command);
}
