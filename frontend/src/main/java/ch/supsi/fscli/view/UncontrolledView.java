package ch.supsi.fscli.view;

import ch.supsi.fscli.model.AbstractModel;

public interface UncontrolledView extends DataView {
    void initialize(AbstractModel model, AbstractModel translationsModel);
}