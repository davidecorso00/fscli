package ch.supsi.fscli.controller;

import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.model.FSModel;
import ch.supsi.fscli.model.HelpModel;
import ch.supsi.fscli.model.TranslationsModel;
import ch.supsi.fscli.utils.FileChooserProvider;
import ch.supsi.fscli.utils.NativeFileChooser;
import ch.supsi.fscli.view.AboutView;
import ch.supsi.fscli.view.DataView;
import ch.supsi.fscli.view.HelpView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FSController implements FSEventHandler {

    private static FSController mySelf;
    private Stage primaryStage;
    private final FSModel fsConfiguration = FSModel.getInstance();
    private TranslationsModel translationsModel = TranslationsModel.getInstance();
    private final List<DataView> views = new ArrayList<>();
    private Path lastSavedPath = null;

    private FileChooserProvider fileChooserProvider = new NativeFileChooser();

    // Questo serve SOLO per i test
    public void setFileChooserProvider(FileChooserProvider provider) {
        this.fileChooserProvider = provider;
    }

    private FSController() {}

    public static FSController getInstance() {
        if (mySelf == null) {
            mySelf = new FSController();
        }
        return mySelf;
    }

    public void initialize(List<?> viewList) {
        if (viewList == null) return;
        for (Object view : viewList) {
            if (view instanceof DataView) {
                registerView((DataView) view);
            }
        }
    }

    public void registerView(DataView view) {
        if (view != null) {
            this.views.add(view);
        }
    }

    @Override
    public void newFileSystem() {
        try {
            if (fsConfiguration.isRootNull()) {
                fsConfiguration.newFileSystem();
            } else {
                ButtonType yes = new ButtonType(translationsModel.translate("label.yes"), ButtonBar.ButtonData.YES);
                ButtonType no = new ButtonType(translationsModel.translate("label.no"), ButtonBar.ButtonData.NO);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, null, yes, no);
                alert.setTitle(translationsModel.translate("label.newFSCreatedAlertTitle"));
                alert.setHeaderText(translationsModel.translate("label.newFSCreatedAlertHeader"));
                alert.setContentText(translationsModel.translate("label.newFSCreatedAlertContent"));
                if (this.primaryStage != null) {
                    alert.initOwner(this.primaryStage);
                }
                alert.showAndWait().ifPresent(response -> {
                    if (response == yes) {
                        try {
                            this.lastSavedPath = null;
                            fsConfiguration.newFileSystem();
                        } catch (Exception e) {
                            System.err.println("Error creating new file system: " + e.getMessage());
                        }
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("Error creating new file system: " + e.getMessage());

        }
    }

    @Override
    public void saveFileSystem() {
        try {
            if (this.lastSavedPath == null) {
                SaveFileSystemAs(this.primaryStage);
            } else {
                fsConfiguration.save();
            }
        } catch (Exception e) {
            System.err.println("FSController.saveFileSystem: errore -> " + e.getMessage());
        }
    }

    @Override
    public void SaveFileSystemAs(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(translationsModel.translate("label.fileChooserTitle"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File JSON", "*.json"));
        fileChooser.setInitialFileName("filesystem.json");
        File file = fileChooserProvider.showSaveDialog(stage);

        if (file != null) {
            try {
                fsConfiguration.saveAs("", file.toPath());
                this.lastSavedPath = file.toPath();
            } catch (Exception e) {
                System.err.println("FSController.SaveFileSystemAs: errore -> " + e.getMessage());
            }
        }
    }

    @Override
    public void openFileSystem(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(translationsModel.translate("label.fileChooserOpenTitle"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File JSON", "*.json"));
        fileChooser.setInitialFileName("filesystem.json");
        File file = fileChooserProvider.showOpenDialog(stage);

        if (file != null) {
            try {
                System.out.println("🎮 [FSController] Apertura file: " + file.toPath());
                boolean opened = fsConfiguration.open(file.toPath(), file.getName());
                System.out.println("🎮 [FSController] Risultato apertura: " + opened);

                if (opened) {
                    this.lastSavedPath = file.toPath();
                }
            } catch (Exception e) {
                System.err.println("FSController.openFileSystem: errore -> " + e.getMessage());
                e.printStackTrace();

            }
        }
    }

    @Override
    public boolean checkForQuit() {
        return fsConfiguration.checkForQuit(this.lastSavedPath);
    }

    @Override
    public void quitApplication() {
        if(checkForQuit()) {
            this.primaryStage.close();
            return;
        }
        // Open Alert for confirmation
        ButtonType yes = new ButtonType(translationsModel.translate("label.yes"), ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType(translationsModel.translate("label.no"), ButtonBar.ButtonData.NO);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, null, yes, no);
        alert.setTitle(translationsModel.translate("label.quitAlertTitle"));
        alert.setHeaderText(translationsModel.translate("label.quitAlertHeader"));
        alert.setContentText(translationsModel.translate("label.quitAlertContent"));
        alert.showAndWait();
        if (alert.getResult() == yes) {
            this.primaryStage.close();
        } else {
            alert.close();
        }

    }

    @Override
    public void editPreferences(Stage stage) {}

    @Override
    public String getHelpText(Stage stage) {
        return HelpModel.getInstance().getHelp();
    }

    @Override
    public void about(Stage stage) {
        for (DataView view : views) {
            if (view instanceof AboutView) {
                try {
                    ((AboutView) view).show(stage);
                } catch (Exception e) {
                    System.err.println("FSController.about: errore mostrando AboutView -> " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void help(Stage stage) {
        for (DataView view : views) {
            if (view instanceof HelpView) {
                try {
                    ((HelpView) view).show(stage, getHelpText(stage));
                } catch (Exception e) {
                    System.err.println("FSController.help: errore mostrando HelpView -> " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    @Override
    public CommandResult executeCommand(String commandLine) {
        return fsConfiguration.executeCommand(commandLine);
    }
}