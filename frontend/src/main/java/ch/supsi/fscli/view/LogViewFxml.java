package ch.supsi.fscli.view;

import ch.supsi.fscli.controller.PreferencesController;
import ch.supsi.fscli.model.*;
import ch.supsi.fscli.model.inode.FSStatus;
import ch.supsi.fscli.model.inode.PreferencesChanges;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;

public class LogViewFxml implements UncontrolledFxView, FSModelObserver, PreferencesObserver {

    private static LogViewFxml myself;
    private TranslationsModel translationsModel;
    private PreferencesModel preferencesModel;
    private FSModel fsModel;
    private TextArea textArea;
    private ScrollPane scrollPane;

    private LogViewFxml() {
        buildDefault();
    }

    public static LogViewFxml getInstance() {
        if (myself == null) myself = new LogViewFxml();
        return myself;
    }

    private void buildDefault() {
        if (this.textArea != null) return;
        this.textArea = new TextArea();
        this.textArea.setId("logView");
        this.textArea.setWrapText(true);
        this.textArea.setEditable(false);
        this.scrollPane = new ScrollPane(this.textArea);
        this.scrollPane.setFitToWidth(true);
        this.scrollPane.setFitToHeight(false);
    }

    @Override
    public Node getNode() {
        return this.scrollPane;
    }

    @Override
    public void initialize(AbstractModel preferencesModel, AbstractModel translationsModel) {
        this.translationsModel = (TranslationsModel) translationsModel;
        this.fsModel = FSModel.getInstance();
        this.preferencesModel = (PreferencesModel) preferencesModel;
        PreferencesController prefs = PreferencesController.getInstance();
        Font f = Font.font(prefs.getLogAreaFont(), 12);
        this.textArea.setFont(f);
        this.textArea.setPrefHeight(prefs.getNumVisibleLineCL() * 25.0);
        this.textArea.setEditable(false);
        if (this.translationsModel != null) {
            this.textArea.clear();
            this.textArea.appendText("\n");
        }
        this.fsModel.addObserver(this);
        this.preferencesModel.addObserver(this);
        this.updateStatus(fsModel.getStatus());
    }


    @Override
    public void updateStatus(FSStatus status) {
        this.textArea.clear();
        if (this.fsModel == null) return;

        String logMessage = switch (status) {
            case WELCOME -> translationsModel.translate("label.welcomeLogView");
            case NEW_FILESYSTEM -> translationsModel.translate("label.newFSCreatedLogView");
            case SAVE -> translationsModel.translate("label.fileFSSavedLogView");
            case SAVE_ERROR -> translationsModel.translate("label.fileFSSNotSavedLogView");
            case OPEN -> translationsModel.translate("label.fileFSLoadedLogView");
            case OPEN_ERROR -> translationsModel.translate("label.fileFSNotLoadedLogView");
            case NOT_OPEN -> translationsModel.translate("label.notOpenedFSLogView");
            default -> "Status: " + status;
        };


        this.textArea.appendText(logMessage);
        this.textArea.appendText("\n");
    }

    @Override
    public void updatePreferences() {
        this.textArea.clear();
        StringBuilder builder = new StringBuilder();
        builder.append(translationsModel.translate("label.preferencesChangeIntroduction")).append("\n");
        System.out.println("DIMENSIONE CAMBIAMENTI: "+preferencesModel.getPreferencesChanges().size());
        for (PreferencesChanges preference : preferencesModel.getPreferencesChanges()){
            switch (preference) {
                case LANGUAGE:
                    builder.append("    -").append(translationsModel.translate("label.languageChanged")).append("\n");
                    break;
                case NUM_COLUMNS_COMMAND_LINE:
                    builder.append("    - ").append(translationsModel.translate("label.numColumnsCommandLineChanged")).append("\n");
                    break;
                case NUM_LINES_LOG_AREA:
                    builder.append("    - ").append(translationsModel.translate("label.numLinesLogAreaChanged")).append("\n");
                    break;
                case NUM_LINES_OUTPUT_AREA:
                    builder.append("    - ").append(translationsModel.translate("label.numLinesOutputAreaChanged")).append("\n");
                    break;
                case FONT_COMMAND_LINE:
                    builder.append("    - ").append(translationsModel.translate("label.fontCommandLineChanged")).append("\n");
                    break;
                case FONT_OUTPUT_AREA:
                    builder.append("    - ").append(translationsModel.translate("label.fontOutputAreaChanged")).append("\n");
                    break;
                case FONT_LOG_AREA:
                    builder.append("    - ").append(translationsModel.translate("label.fontLogAreaChanged")).append("\n");
                    break;
                default:
                    break;
            }
        }
        if(!preferencesModel.getPreferencesChanges().isEmpty())
            this.textArea.appendText(builder.toString());
        else
            this.textArea.appendText(translationsModel.translate("label.preferencesChangedNoDetails") + "\n");

    }
}
