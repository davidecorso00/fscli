package ch.supsi.fscli.application;

import ch.supsi.fscli.data_access.CommandResult;

import java.nio.file.Path;

public interface IFSApplication {
    void newFileSystem();
    boolean checkForQuit(Path lastSavedPath);
    boolean isRootNull();
    CommandResult executeCommand(String commandLine);
}
