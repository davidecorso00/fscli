package ch.supsi.fscli.view;

import ch.supsi.fscli.controller.PreferencesController;
import ch.supsi.fscli.model.AbstractModel;
import ch.supsi.fscli.model.TranslationsModel;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class PreferencesView implements UncontrolledFxView {
    private static PreferencesView myself;
    private Stage dialogStage;
    private VBox root;
    private TranslationsModel translationsModel;

    private Spinner<Integer> columnsSpinner;
    private Spinner<Integer> visibleLinesCLSpinner;
    private Spinner<Integer> visibleLinesOutputSpinner;
    private ComboBox<String> languageComboBox;

    private ComboBox<String> commandLineFontCombo;
    private ComboBox<String> outputAreaFontCombo;
    private ComboBox<String> logAreaFontCombo;

    private Label titleLabel;
    private Label columnsLabel;
    private Label visibleLinesCLLabel;
    private Label visibleLinesOutputLabel;
    private Label languageLabel;
    private Label cmdFontLabel;
    private Label outFontLabel;
    private Label logFontLabel;
    private Button saveButton;

    private final PreferencesController preferencesController;
    List<String> fontFamilies = Font.getFamilies();

    PreferencesView() {
        preferencesController = PreferencesController.getInstance();
    }

    public static PreferencesView getInstance() {
        if (myself == null) {
            myself = new PreferencesView();
        }
        return myself;
    }

    private void translateAll() {
        if (this.translationsModel == null) return;
        if (titleLabel != null) titleLabel.setText(this.translationsModel.translate("label.preferences"));
        if (columnsLabel != null) columnsLabel.setText(this.translationsModel.translate("label.numOfColumns"));
        if (visibleLinesCLLabel != null) visibleLinesCLLabel.setText(this.translationsModel.translate("label.numOfLinesLogArea"));
        if (visibleLinesOutputLabel != null) visibleLinesOutputLabel.setText(this.translationsModel.translate("label.numOfLinesOutputArea"));
        if (languageLabel != null) languageLabel.setText(this.translationsModel.translate("label.language"));
        if (cmdFontLabel != null) cmdFontLabel.setText(this.translationsModel.translate("label.fontCommandLine"));
        if (outFontLabel != null) outFontLabel.setText(this.translationsModel.translate("label.fontOutputArea"));
        if (logFontLabel != null) logFontLabel.setText(this.translationsModel.translate("label.fontLogArea"));
        if (saveButton != null) saveButton.setText(this.translationsModel.translate("label.save"));
    }

    public void show(Stage owner) {
        if (this.translationsModel == null) {
            return;
        }

        dialogStage = new Stage();
        dialogStage.initOwner(owner);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setResizable(false);

        titleLabel = new Label();
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        columnsLabel = new Label();
        columnsSpinner = new Spinner<>();
        columnsSpinner.setId("columnsSpinner");
        columnsSpinner.setEditable(true);
        columnsSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        3, 20, preferencesController.getNumCommandLineColumns()
                )
        );

        visibleLinesCLLabel = new Label();
        visibleLinesCLSpinner = new Spinner<>();
        visibleLinesCLSpinner.setId("visibleLinesCLSpinner");
        visibleLinesCLSpinner.setEditable(true);
        visibleLinesCLSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        3, 20, preferencesController.getNumVisibleLineCL()
                )
        );

        visibleLinesOutputLabel = new Label();
        visibleLinesOutputSpinner = new Spinner<>();
        visibleLinesOutputSpinner.setId("visibleLinesOutputSpinner");
        visibleLinesOutputSpinner.setEditable(true);
        visibleLinesOutputSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        3, 20, preferencesController.getNumVisibleLineOutputArea()
                )
        );

        languageLabel = new Label();
        languageComboBox = new ComboBox<>();
        languageComboBox.setId("languageComboBox");
        languageComboBox.getItems().addAll("it-IT", "en-US");
        languageComboBox.setValue(preferencesController.getLanguage());

        cmdFontLabel = new Label();
        commandLineFontCombo = new ComboBox<>();
        commandLineFontCombo.setId("commandLineFontCombo");
        commandLineFontCombo.getItems().addAll(fontFamilies);
        commandLineFontCombo.setValue(preferencesController.getCommandLineFont());
        commandLineFontCombo.setPrefWidth(200);

        outFontLabel = new Label();
        outputAreaFontCombo = new ComboBox<>();
        outputAreaFontCombo.setId("outputAreaFontCombo");
        outputAreaFontCombo.getItems().addAll(fontFamilies);
        outputAreaFontCombo.setValue(preferencesController.getOutputAreaFont());
        outputAreaFontCombo.setPrefWidth(200);

        logFontLabel = new Label();
        logAreaFontCombo = new ComboBox<>();
        logAreaFontCombo.setId("logAreaFontCombo");
        logAreaFontCombo.getItems().addAll(fontFamilies);
        logAreaFontCombo.setValue(preferencesController.getLogAreaFont());
        logAreaFontCombo.setPrefWidth(200);

        saveButton = new Button();
        saveButton.setId("saveButton");
        saveButton.setOnAction(event -> saveSettings());

        translateAll();

        dialogStage.setTitle(this.translationsModel.translate("label.preferences"));

        root = new VBox(10,
                titleLabel,
                new Separator(),
                new Label(this.translationsModel.translate("label.dimensions")),
                new HBox(10, columnsLabel, columnsSpinner),
                new HBox(10, visibleLinesCLLabel, visibleLinesCLSpinner),
                new HBox(10, visibleLinesOutputLabel, visibleLinesOutputSpinner),
                new Separator(),
                new Label(this.translationsModel.translate("label.localization")),
                new HBox(10, languageLabel, languageComboBox),
                new Separator(),
                new Label(this.translationsModel.translate("label.font")),
                new HBox(10, cmdFontLabel, commandLineFontCombo),
                new HBox(10, outFontLabel, outputAreaFontCombo),
                new HBox(10, logFontLabel, logAreaFontCombo),
                new Separator(),
                saveButton
        );
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 520, 550);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void saveSettings() {
        int columns = columnsSpinner.getValue();
        int visibleLinesCL = visibleLinesCLSpinner.getValue();
        int visibleLinesOutput = visibleLinesOutputSpinner.getValue();
        String language = languageComboBox.getValue();

        String cmdFont = commandLineFontCombo.getValue();
        String outFont = outputAreaFontCombo.getValue();
        String logFont = logAreaFontCombo.getValue();

        preferencesController.setNumCommandLineColumns(columns);
        preferencesController.setNumVisibleLineCL(visibleLinesCL);
        preferencesController.setNumVisibleLineOutputArea(visibleLinesOutput);
        preferencesController.setLanguage(language);

        preferencesController.setCommandLineFont(cmdFont);
        preferencesController.setOutputAreaFont(outFont);
        preferencesController.setLogAreaFont(logFont);

        preferencesController.save();

        Alert alert = new Alert(
                Alert.AlertType.INFORMATION,
                this.translationsModel.translate("label.preferencesSaved")
        );
        alert.showAndWait();
        dialogStage.close();
    }

    @Override
    public Node getNode() {
        return root;
    }

    @Override
    public void initialize(AbstractModel model, AbstractModel translationsModel) {
        this.translationsModel = (TranslationsModel) translationsModel;
    }

}
