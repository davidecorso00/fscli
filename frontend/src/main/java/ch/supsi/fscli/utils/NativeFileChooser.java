package ch.supsi.fscli.utils;

import java.io.File;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class NativeFileChooser implements FileChooserProvider {
    @Override
    public File showOpenDialog(Stage stage) {
        return new FileChooser().showOpenDialog(stage);
    }

    @Override
    public File showSaveDialog(Stage stage) {
        return new FileChooser().showSaveDialog(stage);
    }
}