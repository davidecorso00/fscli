package ch.supsi.fscli.view;

import ch.supsi.fscli.controller.PreferencesController;
import ch.supsi.fscli.model.AbstractModel;
import ch.supsi.fscli.model.TranslationsModel;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;

public class CommandLineViewFxml implements UncontrolledFxView {
    private static CommandLineViewFxml myself;
    private TranslationsModel translationsModel;

    private HBox root;
    private Label commandLineLabel;
    private TextField commandLine;
    private Button enter;

    private static final double SPACER_WIDTH = 11.0;
    private static final Insets PADDING = new Insets(7);

    private CommandLineViewFxml() { buildDefault(); }

    public static CommandLineViewFxml getInstance() {
        if (myself == null) {
            myself = new CommandLineViewFxml();
        }
        return myself;
    }

    private void buildDefault() {
        if (this.root != null) return;

        this.commandLineLabel = new Label();
        this.commandLineLabel.setId("command");

        this.commandLine = new TextField();
        this.commandLine.setId("commandLine");
        this.commandLine.setDisable(true);

        Region spacer1 = new Region(); spacer1.setPrefWidth(SPACER_WIDTH);
        Region spacer2 = new Region(); spacer2.setPrefWidth(SPACER_WIDTH);

        this.enter = new Button();
        this.enter.setId("enter");

        this.commandLineLabel.setText("command");
        this.enter.setText("enter");

        this.root = new HBox();
        this.root.setSpacing(0);
        this.root.setPadding(PADDING);
        this.root.getChildren().addAll(commandLineLabel, spacer1, commandLine, spacer2, enter);
    }

    private void translateAll() {
        if (this.translationsModel == null) return;
        if (this.commandLineLabel != null) this.commandLineLabel.setText(this.translationsModel.translate("label.command"));
        if (this.enter != null) this.enter.setText(this.translationsModel.translate("label.enter"));
    }

    @Override
    public Node getNode() { return this.root; }

    @Override
    public void initialize(AbstractModel model, AbstractModel translationsModel) {
        this.translationsModel = (TranslationsModel) translationsModel;
        PreferencesController prefs = PreferencesController.getInstance();
        Font f = Font.font(prefs.getCommandLineFont(), 12);
        if (commandLine != null) commandLine.setFont(f);
        if (enter != null) enter.setFont(f);
        if (commandLine != null) commandLine.setPrefColumnCount(prefs.getNumCommandLineColumns());


        translateAll();
    }



    public TextField getCommandLine() { return this.commandLine; }
    public Button getEnterButton() { return this.enter; }
}