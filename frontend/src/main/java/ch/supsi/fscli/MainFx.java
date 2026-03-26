package ch.supsi.fscli;

import ch.supsi.fscli.controller.*;
import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.data_access.CommandResultStatus;
import ch.supsi.fscli.model.AbstractModel;
import ch.supsi.fscli.model.FSModel;
import ch.supsi.fscli.model.PreferencesModel;
import ch.supsi.fscli.model.TranslationsModel;
import ch.supsi.fscli.view.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class MainFx extends Application {
    private static final int PREF_INSETS_SIZE = 7;
    private static final int PREF_COMMAND_SPACER_WIDTH = 11;
    public static final String APP_TITLE = "filesystem command interpreter simulator";

    // Models
    private final AbstractModel preferencesModel;
    private final AbstractModel translationsModel;
    private final AbstractModel filesystemModel;

    // Views
    private final ControlledTranslatableFxView menuBarView;
    private final PreferencesView preferencesView;
    private final UncontrolledFxView commandLineView;
    private final HelpView helpView;
    private final AboutView aboutView;
    private final OutputViewFxml outputView;
    private final LogViewFxml logView;

    // Controllers / Handlers
    private final FSEventHandler fsEventHandler;
    private final PreferencesController preferencesController;
    private final TranslationsController translationsController;

    public MainFx() {
        this.preferencesModel = PreferencesModel.getInstance();
        this.translationsModel = TranslationsModel.getInstance();
        this.filesystemModel = FSModel.getInstance();

        this.menuBarView = MenuBarViewFxml.getInstance();
        this.helpView = HelpView.getInstance();
        this.aboutView = AboutView.getInstance();
        this.preferencesView = PreferencesView.getInstance();
        this.commandLineView = CommandLineViewFxml.getInstance();
        this.outputView = OutputViewFxml.getInstance();
        this.logView = LogViewFxml.getInstance();

        this.fsEventHandler = FSController.getInstance();
        this.preferencesController = PreferencesController.getInstance();
        this.translationsController = TranslationsController.getInstance();

        try {
            this.translationsController.initFromPreferences();
        } catch (Exception e) {
            System.err.println("MainFx: errore initFromPreferences -> " + e.getMessage());
        }

        this.menuBarView.initialize(this.fsEventHandler, this.preferencesModel, this.translationsModel);
        this.commandLineView.initialize(this.preferencesModel, this.translationsModel);
        this.preferencesView.initialize(this.preferencesModel, this.translationsModel);
        this.helpView.initialize(this.preferencesModel, this.translationsModel);
        this.aboutView.initialize(this.preferencesModel, this.translationsModel);
        this.outputView.initialize(this.preferencesModel, this.translationsModel);
        this.logView.initialize(this.preferencesModel, this.translationsModel);

        try {
            FSController.getInstance().initialize(java.util.List.of(
                    this.menuBarView, this.helpView, this.aboutView, this.preferencesView,
                    this.logView, this.outputView, this.commandLineView));
        } catch (Exception ignored) {
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Node commandLineNode = commandLineView.getNode();
        Node menuNode = menuBarView.getNode();

        if (menuNode == null) menuNode = new Region();
        if (commandLineNode == null) commandLineNode = new Region();

        HBox commandLinePane = new HBox();
        commandLinePane.setAlignment(Pos.BASELINE_LEFT);
        commandLinePane.setPadding(new Insets(PREF_INSETS_SIZE));
        Region spacer1 = new Region(); spacer1.setPrefWidth(PREF_COMMAND_SPACER_WIDTH);
        Region spacer2 = new Region(); spacer2.setPrefWidth(PREF_COMMAND_SPACER_WIDTH);
        commandLinePane.getChildren().addAll(commandLineNode, spacer1, spacer2);

        VBox top = new VBox(menuNode, commandLinePane);

        BorderPane rootPane = new BorderPane();
        rootPane.setTop(top);
        rootPane.setCenter(this.outputView.getNode());
        rootPane.setBottom(this.logView.getNode());

        Scene mainScene = new Scene(rootPane);

        primaryStage.setTitle(APP_TITLE);
        primaryStage.setResizable(true);
        primaryStage.setScene(mainScene);

        FSController controller = FSController.getInstance();
        controller.registerView(this.helpView);
        controller.registerView(this.aboutView);
        controller.registerView(this.preferencesView);
        controller.registerView(this.commandLineView);
        controller.registerView(this.outputView);
        controller.registerView(this.logView);
        controller.setPrimaryStage(primaryStage);

        primaryStage.setOnCloseRequest(e -> primaryStage.close());
        primaryStage.show();

        wireCommandLineBehavior();
    }

    private void wireCommandLineBehavior() {
        if (commandLineView instanceof CommandLineViewFxml clv) {
            if (clv.getEnterButton() != null && clv.getCommandLine() != null) {

                clv.getEnterButton().setOnAction(ev -> executeCommand(clv));

                clv.getCommandLine().setOnKeyPressed(event -> {
                    if (Objects.requireNonNull(event.getCode()) == KeyCode.ENTER) {
                        executeCommand(clv);
                    }
                });
            }
        }
    }

    private void executeCommand(CommandLineViewFxml clv) {
        String cmd = clv.getCommandLine().getText();
        if (cmd != null && !cmd.isEmpty()) {

            CommandResult commandResult = fsEventHandler.executeCommand(cmd);

            if (commandResult.getStatus().equals(CommandResultStatus.CLEAR_OUTPUT)) {
                outputView.clear();
            }
            else if (commandResult.getMessage() != null && !commandResult.getMessage().isEmpty()) {
                outputView.appendText(commandResult.getMessage() + "\n");
            }

            clv.getCommandLine().clear();
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}