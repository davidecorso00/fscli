package ch.supsi.fscli.utils; // Adatta il package

import java.io.File;
import javafx.stage.Stage;

public interface FileChooserProvider {
    File showOpenDialog(Stage stage);
    File showSaveDialog(Stage stage);
}