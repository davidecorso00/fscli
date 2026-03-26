package ch.supsi.fscli.view;

import ch.supsi.fscli.controller.EventHandler;
import ch.supsi.fscli.model.AbstractModel;
import javafx.scene.Node;


public interface ControlledFxView extends ControlledView {
    Node getNode();
    void initialize(EventHandler eventHandler, AbstractModel model);
}