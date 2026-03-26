package ch.supsi.fscli.view;

import ch.supsi.fscli.model.AbstractModel;
import ch.supsi.fscli.model.TranslationsModel;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HelpView implements UncontrolledFxView{

    private static HelpView myself;
    private Stage dialogStage;
    private VBox root;
    private TranslationsModel translationsModel;

    private Label titleLabel;
    private Label introductionLabel;

    private HelpView() {
    }

    public static HelpView getInstance() {
        if (myself == null) {
            myself = new HelpView();
        }
        return myself;
    }

    public void show(Stage owner, String helpText){
        dialogStage = new Stage();
        this.titleLabel = new Label();
        this.introductionLabel = new Label();
        if(this.translationsModel != null){
            dialogStage.setTitle(this.translationsModel.translate("label.help"));
            this.titleLabel.setText(this.translationsModel.translate("label.help"));
            this.introductionLabel.setText(this.translationsModel.translate("label.helpIntroduction"));
        } else{
            dialogStage.setTitle("Help");
        }
        dialogStage.initOwner(owner);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setResizable(true);


        StringBuilder sb = new StringBuilder();
        String intro = this.introductionLabel.getText();
        if (intro != null && !intro.isBlank()) {
            sb.append(intro).append("\n\n");
        }
        if (helpText != null && !helpText.isBlank()) {
            sb.append(helpText).append("\n\n");
        }


        TextArea textArea = new TextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);


        textArea.prefWidthProperty().bind(dialogStage.widthProperty().subtract(40));
        textArea.prefHeightProperty().bind(dialogStage.heightProperty().subtract(40));

        root = new VBox(textArea);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 600, 450);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
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
