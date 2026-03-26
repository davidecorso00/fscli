package ch.supsi.fscli.model;

import ch.supsi.fscli.data_access.CommandResult;
import java.nio.file.Path;

public interface IFSConfigurationModel {
    void newFileSystem();
    boolean checkForQuit(Path lastSavedPath);
    boolean isRootNull();
    CommandResult executeCommand(String commandLine);
}
