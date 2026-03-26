package ch.supsi.fscli.view;

import ch.supsi.fscli.controller.EventHandler;
import ch.supsi.fscli.controller.FSEventHandler;
import ch.supsi.fscli.model.AbstractModel;
import ch.supsi.fscli.model.PreferencesModel;
import ch.supsi.fscli.model.TranslationsModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MenuBarViewFxml implements ControlledTranslatableFxView {
    private static MenuBarViewFxml myself;
    private FSEventHandler eventhandler;
    private TranslationsModel translationsModel;
    private PreferencesModel preferencesModel = PreferencesModel.getInstance();

    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu fileMenu;
    @FXML
    private Menu editMenu;
    @FXML
    private Menu helpMenu;
    @FXML
    private MenuItem newMenuItem;
    @FXML
    private MenuItem openMenuItem;
    @FXML
    private MenuItem saveMenuItem;
    @FXML
    private MenuItem saveAsMenuItem;
    @FXML
    private MenuItem quitMenuItem;
    @FXML
    private MenuItem preferencesMenuItem;
    @FXML
    private MenuItem aboutMenuItem;
    @FXML
    private MenuItem helpMenuItem;

    private MenuBarViewFxml() {
        // Costruttore vuoto, il caricamento avviene in getInstance
    }

    public static ControlledTranslatableFxView getInstance() {
        if (myself == null) {
            myself = new MenuBarViewFxml();
            try {
                // Carichiamo l'FXML
                URL fxmlUrl = MenuBarViewFxml.class.getResource("/menubar.fxml");
                if (fxmlUrl == null) {
                    throw new IOException("File menubar.fxml non trovato in resources!");
                }
                FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
                fxmlLoader.setController(myself); // Impostiamo il controller manualmente (Singleton)
                fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Impossibile caricare la GUI da FXML", e);
            }
        }
        return myself;
    }

    private void createBehaviour() {
        if (this.preferencesMenuItem != null) {
            this.preferencesMenuItem.setOnAction(event -> {
                try {
                    Stage stage = (Stage) menuBar.getScene().getWindow();
                    PreferencesView.getInstance().show(stage);
                    preferencesModel.notifyObservers();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        if (this.helpMenuItem != null) {
            this.helpMenuItem.setOnAction(event -> {
                try {
                    Stage stage = (Stage) menuBar.getScene().getWindow();
                    this.eventhandler.help(stage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        if (this.aboutMenuItem != null) {
            this.aboutMenuItem.setOnAction(event -> {
                try {
                    Stage stage = (Stage) menuBar.getScene().getWindow();
                    this.eventhandler.about(stage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        if (this.newMenuItem != null) {
            this.newMenuItem.setOnAction(event -> {
                this.saveMenuItem.setDisable(false);
                this.saveAsMenuItem.setDisable(false);
                CommandLineViewFxml.getInstance().getCommandLine().setDisable(false);
                this.eventhandler.newFileSystem();
            });
        }

        if (this.saveMenuItem != null) {
            this.saveMenuItem.setOnAction(event -> this.eventhandler.saveFileSystem());
        }

        if (this.saveAsMenuItem != null) {
            this.saveAsMenuItem.setOnAction(event -> {
                Stage stage = (Stage) menuBar.getScene().getWindow();
                this.eventhandler.SaveFileSystemAs(stage);
            });
        }

        if (this.openMenuItem != null) {
            this.openMenuItem.setOnAction(event -> {
                Stage stage = (Stage) menuBar.getScene().getWindow();
                this.saveMenuItem.setDisable(false);
                this.saveAsMenuItem.setDisable(false);
                CommandLineViewFxml.getInstance().getCommandLine().setDisable(false);
                this.eventhandler.openFileSystem(stage);
            });
        }

        if (this.quitMenuItem != null) {
            this.quitMenuItem.setOnAction(event -> this.eventhandler.quitApplication());
        }
    }

    public void translateText() {
        if (this.translationsModel == null) return;

        if (this.fileMenu != null) this.fileMenu.setText(this.translationsModel.translate("label.file"));
        if (this.editMenu != null) this.editMenu.setText(this.translationsModel.translate("label.edit"));
        if (this.helpMenu != null) this.helpMenu.setText(this.translationsModel.translate("label.help"));

        if (this.newMenuItem != null) this.newMenuItem.setText(this.translationsModel.translate("label.new"));
        if (this.openMenuItem != null) this.openMenuItem.setText(this.translationsModel.translate("label.open"));
        if (this.saveMenuItem != null) this.saveMenuItem.setText(this.translationsModel.translate("label.save"));
        if (this.saveAsMenuItem != null) this.saveAsMenuItem.setText(this.translationsModel.translate("label.saveAs"));
        if (this.quitMenuItem != null) this.quitMenuItem.setText(this.translationsModel.translate("label.quit"));
        if (this.preferencesMenuItem != null) this.preferencesMenuItem.setText(this.translationsModel.translate("label.preferences"));
        if (this.aboutMenuItem != null) this.aboutMenuItem.setText(this.translationsModel.translate("label.about"));
        if (this.helpMenuItem != null) this.helpMenuItem.setText(this.translationsModel.translate("label.help"));
    }

    @Override
    public void initialize(EventHandler eventHandler, AbstractModel model, AbstractModel translationsModel) {
        this.eventhandler = (FSEventHandler) eventHandler;
        this.translationsModel = (TranslationsModel) translationsModel;

        if (this.menuBar == null) {
            throw new IllegalStateException("MenuBar non inizializzata! Controlla menubar.fxml e gli fx:id.");
        }


        this.createBehaviour();
        this.translateText();
    }

    @Override
    public Node getNode() {
        return this.menuBar;
    }
}