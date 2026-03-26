package ch.supsi.fscli.model;

import ch.supsi.fscli.application.preferences.PreferencesApp;
import ch.supsi.fscli.model.inode.PreferencesChanges;

import java.util.ArrayList;
import java.util.List;

public class PreferencesModel extends AbstractModel implements IPreferencesModel{
    private static PreferencesModel myself;
    private List<PreferencesChanges> preferencesChanges = new ArrayList<>();
    private final List<PreferencesObserver> observers = new ArrayList<>();
    private final PreferencesApp app = PreferencesApp.getInstance();

    private PreferencesModel() {}

    public static PreferencesModel getInstance() {
        if (myself == null) {
            myself = new PreferencesModel();
        }
        return myself;
    }

    public List<PreferencesChanges> getPreferencesChanges() {
        return preferencesChanges;
    }

    public void addObserver(PreferencesObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers() {
        for (PreferencesObserver observer : observers) {
            observer.updatePreferences();
        }
    }
    public void setNumCommandLineColumns(int columns){
        int currentColumns = app.getNumCommandLineColumns();
        if(currentColumns != columns && !preferencesChanges.contains(PreferencesChanges.NUM_COLUMNS_COMMAND_LINE)){
            this.preferencesChanges.add(PreferencesChanges.NUM_COLUMNS_COMMAND_LINE);
        }
        app.setNumCommandLineColumns(columns);
    }

    public void setNumVisibleLineCL(int numVisibleLineCL){
        int currentLines = app.getNumVisibleLineCL();
        if(currentLines != numVisibleLineCL && !preferencesChanges.contains(PreferencesChanges.NUM_LINES_LOG_AREA)){
            this.preferencesChanges.add(PreferencesChanges.NUM_LINES_LOG_AREA);
        }
        app.setNumVisibleLineCL(numVisibleLineCL);
    }

    public void setNumVisibleLineOutputArea(int numVisibleLineOutputArea){
        int currentLines = app.getNumVisibleLineOutputArea();
        if(currentLines != numVisibleLineOutputArea && !preferencesChanges.contains(PreferencesChanges.NUM_LINES_OUTPUT_AREA)){
            this.preferencesChanges.add(PreferencesChanges.NUM_LINES_OUTPUT_AREA);
        }
        app.setNumVisibleLineOutputArea(numVisibleLineOutputArea);
    }

    public void setLanguage(String language){
        String currentLanguage = app.getLanguage();
        if(!currentLanguage.equals(language) && !preferencesChanges.contains(PreferencesChanges.LANGUAGE)) {
            this.preferencesChanges.add(PreferencesChanges.LANGUAGE);
        }
        app.setLanguage(language);
    }

    public int getNumCommandLineColumns(){
        return app.getNumCommandLineColumns();
    }

    @Override
    public int getNumVisibleLineCL() {
        return app.getNumVisibleLineCL();
    }

    @Override
    public int getNumVisibleLineOutputArea() {
        return app.getNumVisibleLineOutputArea();
    }

    @Override
    public String getLanguage() {
        return app.getLanguage();
    }

    @Override
    public void setCommandLineFont(String font) {
        String currentFont = app.getCommandLineFont();
        if(!currentFont.equals(font)  && !preferencesChanges.contains(PreferencesChanges.FONT_COMMAND_LINE)){
            this.preferencesChanges.add(PreferencesChanges.FONT_COMMAND_LINE);
        }
        app.setCommandLineFont(font);
    }

    @Override
    public void setOutputAreaFont(String font) {
        String currentFont = app.getOutputAreaFont();
        if(!currentFont.equals(font) && !preferencesChanges.contains(PreferencesChanges.FONT_OUTPUT_AREA)){
            this.preferencesChanges.add(PreferencesChanges.FONT_OUTPUT_AREA);
        }
        app.setOutputAreaFont(font);
    }

    @Override
    public void setLogAreaFont(String font) {
        String currentFont = app.getLogAreaFont();
        if(!currentFont.equals(font) && !preferencesChanges.contains(PreferencesChanges.FONT_LOG_AREA)){
            this.preferencesChanges.add(PreferencesChanges.FONT_LOG_AREA);
        }
        app.setLogAreaFont(font);
    }

    @Override
    public String getCommandLineFont() {
        return app.getCommandLineFont();
    }

    @Override
    public String getOutputAreaFont() {
        return app.getOutputAreaFont();
    }

    @Override
    public String getLogAreaFont() {
        return app.getLogAreaFont();
    }

    @Override
    public void save() {
        app.save();
    }
}