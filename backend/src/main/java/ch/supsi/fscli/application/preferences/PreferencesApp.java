package ch.supsi.fscli.application.preferences;

import ch.supsi.fscli.business.preferences.PreferencesLogic;

public class PreferencesApp implements IPreferencesApp {
    private static PreferencesApp myself;


    private PreferencesApp(){}

    public static PreferencesApp getInstance(){
        if(myself == null){
            myself = new PreferencesApp();
        }
        return myself;
    }

    public void setNumCommandLineColumns(int columns){
        PreferencesLogic.getInstance().setNumCommandLineColumns(columns);
    }

    public void setNumVisibleLineCL(int numVisibleLineCL){
        PreferencesLogic.getInstance().setNumVisibleLineCL(numVisibleLineCL);
    }

    public void setNumVisibleLineOutputArea(int numVisibleLineOutputArea){
        PreferencesLogic.getInstance().setNumVisibleLineOutputArea(numVisibleLineOutputArea);
    }

    public void setLanguage(String language){
        PreferencesLogic.getInstance().setLanguage(language);
    }

    @Override
    public void setCommandLineFont(String font) {
        PreferencesLogic.getInstance().setCommandLineFont(font);
    }

    @Override
    public void setOutputAreaFont(String font) {
        PreferencesLogic.getInstance().setOutputAreaFont(font);
    }

    @Override
    public void setLogAreaFont(String font) {
        PreferencesLogic.getInstance().setLogAreaFont(font);
    }

    @Override
    public int getNumVisibleLineOutputArea() {
        return PreferencesLogic.getInstance().getNumVisibleLineOutputArea();
    }

    @Override
    public int getNumCommandLineColumns() {
        return PreferencesLogic.getInstance().getNumCommandLineColumns();
    }

    @Override
    public int getNumVisibleLineCL() {
        return PreferencesLogic.getInstance().getNumVisibleLineCL();
    }

    @Override
    public String getLanguage() {
        return PreferencesLogic.getInstance().getLanguage();
    }

    @Override
    public String getCommandLineFont() {
        return PreferencesLogic.getInstance().getCommandLineFont();
    }

    @Override
    public String getOutputAreaFont() {
        return PreferencesLogic.getInstance().getOutputAreaFont();
    }

    @Override
    public String getLogAreaFont() {
        return PreferencesLogic.getInstance().getLogAreaFont();
    }

    @Override
    public void save() {
        PreferencesLogic.getInstance().save();
    }
}