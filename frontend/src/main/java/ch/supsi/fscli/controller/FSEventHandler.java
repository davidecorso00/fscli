package ch.supsi.fscli.controller;

import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.model.inode.FileSystem;
import javafx.stage.Stage;

import java.nio.file.Path;

public interface FSEventHandler extends EventHandler {
    void newFileSystem();
    void saveFileSystem();
    void SaveFileSystemAs(Stage stage);
    void openFileSystem(Stage stage);
    boolean checkForQuit();
    void quitApplication();

    void editPreferences(Stage stage);

    String getHelpText(Stage stage);
    void about(Stage stage);
    void help(Stage stage);
    CommandResult executeCommand(String command);

    void setPrimaryStage(Stage stage);
}
