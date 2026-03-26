package ch.supsi.fscli.view;

import ch.supsi.fscli.controller.PreferencesController;
import ch.supsi.fscli.model.AbstractModel;
import ch.supsi.fscli.model.TranslationsModel;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;

public class OutputViewFxml implements UncontrolledFxView {
    private static OutputViewFxml myself;
    private TranslationsModel translationsModel;

    private TextArea textArea;
    private ScrollPane scrollPane;

    private OutputViewFxml() { buildDefault(); }

    public static OutputViewFxml getInstance() {
        if (myself == null) myself = new OutputViewFxml();
        return myself;
    }

    private void buildDefault() {
        if (this.textArea != null) return;
        this.textArea = new TextArea();
        this.textArea.setId("outputView");
        this.textArea.setWrapText(true);
        this.textArea.setEditable(false);
        this.scrollPane = new ScrollPane(this.textArea);
        this.scrollPane.setFitToWidth(true);
        this.scrollPane.setFitToHeight(false);
    }

    @Override
    public Node getNode() { return this.scrollPane; }

    @Override
    public void initialize(AbstractModel model, AbstractModel translationsModel) {
        this.translationsModel = (TranslationsModel) translationsModel;
        PreferencesController prefs = PreferencesController.getInstance();
        Font f = Font.font(prefs.getOutputAreaFont(), 12);
        this.textArea.setFont(f);
        this.textArea.setPrefHeight(prefs.getNumVisibleLineOutputArea() * 25.0);
        this.textArea.setEditable(false);

        if (this.translationsModel != null)
            this.textArea.clear();
    }

    public void clear(){
        this.textArea.clear();
    }


    public void appendText(String text) { if (this.textArea != null && text != null) this.textArea.appendText(text); }
    public void setFont(Font font) { if (this.textArea != null && font != null) this.textArea.setFont(font); }
    public void setPrefHeight(double height) { if (this.textArea != null) this.textArea.setPrefHeight(height); }
    public void setEditable(boolean editable) { if (this.textArea != null) this.textArea.setEditable(editable); }
}