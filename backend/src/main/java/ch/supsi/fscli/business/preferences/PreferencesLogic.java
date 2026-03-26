package ch.supsi.fscli.business.preferences;

import ch.supsi.fscli.data_access.PreferencesData;

public class PreferencesLogic implements IPreferencesLogic {
    private static PreferencesLogic myself;

    private PreferencesLogic(){}

    public static PreferencesLogic getInstance(){
        if (myself == null){
            myself = new PreferencesLogic();
        }
        return myself;
    }

    public void setNumCommandLineColumns(int columns){
        PreferencesData preferencesData = PreferencesData.getInstance();
        preferencesData.setNumCommandLineColumns(columns);
    }

    public void setNumVisibleLineCL(int numVisibleLineCL){
        PreferencesData preferencesData = PreferencesData.getInstance();
        preferencesData.setNumVisibleLineCL(numVisibleLineCL);
    }

    public void setNumVisibleLineOutputArea(int numVisibleLineOutputArea){
        PreferencesData preferencesData = PreferencesData.getInstance();
        preferencesData.setNumVisibleLineOutputArea(numVisibleLineOutputArea);
    }

    public void setLanguage(String language){
        PreferencesData preferencesData = PreferencesData.getInstance();
        preferencesData.setLanguage(language);
    }

    @Override
    public void setCommandLineFont(String font) {
        PreferencesData.getInstance().setCommandLineFont(font);
    }

    @Override
    public void setOutputAreaFont(String font) {
        PreferencesData.getInstance().setOutputAreaFont(font);
    }

    @Override
    public void setLogAreaFont(String font) {
        PreferencesData.getInstance().setLogAreaFont(font);
    }

    @Override
    public int getNumVisibleLineCL() {
        return PreferencesData.getInstance().getNumVisibleLineCL();
    }

    @Override
    public int getNumVisibleLineOutputArea() {
        return PreferencesData.getInstance().getNumVisibleLineOutputArea();
    }

    @Override
    public int getNumCommandLineColumns() {
        return PreferencesData.getInstance().getNumCommandLineColumns();
    }

    @Override
    public String getLanguage() {
        return PreferencesData.getInstance().getLanguage();
    }

    @Override
    public String getCommandLineFont() {
        return PreferencesData.getInstance().getCommandLineFont();
    }

    @Override
    public String getOutputAreaFont() {
        return PreferencesData.getInstance().getOutputAreaFont();
    }

    @Override
    public String getLogAreaFont() {
        return PreferencesData.getInstance().getLogAreaFont();
    }

    @Override
    public void save() {
        PreferencesData.getInstance().save();
    }
}