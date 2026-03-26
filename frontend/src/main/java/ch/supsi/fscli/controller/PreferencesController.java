package ch.supsi.fscli.controller;

import ch.supsi.fscli.model.PreferencesModel;

public class PreferencesController implements PreferencesHandler {
    private final PreferencesModel model = PreferencesModel.getInstance();

    private static PreferencesController myself;

    public static PreferencesController getInstance() {
        if (myself == null) {
            myself = new PreferencesController();
        }
        return myself;
    }



    @Override
    public void setNumCommandLineColumns(int columns) {
        model.setNumCommandLineColumns(columns);
    }

    @Override
    public void setNumVisibleLineCL(int numVisibleLineCL) {
        model.setNumVisibleLineCL(numVisibleLineCL);
    }

    @Override
    public void setNumVisibleLineOutputArea(int numVisibleLineOutputArea) {
        model.setNumVisibleLineOutputArea(numVisibleLineOutputArea);
    }

    @Override
    public void setLanguage(String language) {
        model.setLanguage(language);
    }

    @Override
    public int getNumCommandLineColumns() {
        return model.getNumCommandLineColumns();
    }

    @Override
    public int getNumVisibleLineOutputArea() {
        return model.getNumVisibleLineOutputArea();
    }

    @Override
    public int getNumVisibleLineCL() {
        return model.getNumVisibleLineCL();
    }

    @Override
    public String getLanguage() {
        return model.getLanguage();
    }

    @Override
    public void setCommandLineFont(String font) {
        model.setCommandLineFont(font);
    }

    @Override
    public void setOutputAreaFont(String font) {
        model.setOutputAreaFont(font);
    }

    @Override
    public void setLogAreaFont(String font) {
        model.setLogAreaFont(font);
    }

    @Override
    public String getCommandLineFont() {
        return model.getCommandLineFont();
    }

    @Override
    public String getOutputAreaFont() {
        return model.getOutputAreaFont();
    }

    @Override
    public String getLogAreaFont() {
        return model.getLogAreaFont();
    }

    @Override
    public void save() {
        model.save();
    }

}
