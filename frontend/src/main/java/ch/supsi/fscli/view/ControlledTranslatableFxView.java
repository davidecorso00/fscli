package ch.supsi.fscli.view;

import ch.supsi.fscli.controller.EventHandler;
import ch.supsi.fscli.model.AbstractModel;
import javafx.scene.Node;

public interface ControlledTranslatableFxView extends ControlledView {
    Node getNode();

    // Passiamo anche il controller per le traduzioni nella view
    void initialize(EventHandler eventHandler, AbstractModel model, AbstractModel translationsController);
}