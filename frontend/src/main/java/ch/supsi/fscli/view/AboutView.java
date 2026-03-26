package ch.supsi.fscli.view;

import ch.supsi.fscli.model.AbstractModel;
import ch.supsi.fscli.model.TranslationsModel;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.stream.Collectors;

public class AboutView extends AbstractModel implements UncontrolledFxView {
    private static AboutView myself;
    private Stage dialogStage;
    private VBox root;
    private ManifestInfoReader manifestInfo;
    private TranslationsModel translationsModel;

    private Label titleLabel;

    private AboutView() {
        manifestInfo = new ManifestInfoReader();
    }

    public static AboutView getInstance() {
        if (myself == null) {
            myself = new AboutView();
        }
        return myself;
    }

    public void show(Stage owner) {
        dialogStage = new Stage();
        this.titleLabel = new Label();
        if (this.translationsModel != null) {
            dialogStage.setTitle(this.translationsModel.translate("label.about"));
            this.titleLabel.setText(this.translationsModel.translate("label.about"));
        } else {
            dialogStage.setTitle("About");
        }
        dialogStage.initOwner(owner);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setResizable(false);

        // Formatta la lista dei developer in elenco puntato
        String developersList = manifestInfo.getDevelopers()
                .stream()
                .map(dev -> "- " + dev)
                .collect(Collectors.joining("\n"));

        String formatted = String.format(
                "Version: %s\nDate: %s\nDevelopers:\n%s",
                manifestInfo.getVersion() != null ? manifestInfo.getVersion() : "N/A",
                manifestInfo.getDate() != null ? manifestInfo.getDate() : "N/A",
                developersList.isEmpty() ? "- N/A" : developersList
        );

        Label textLabel = new Label(formatted);
        textLabel.setWrapText(true);

        ScrollPane scrollPane = new ScrollPane(textLabel);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10));

        root = new VBox(scrollPane);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 450, 400);
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