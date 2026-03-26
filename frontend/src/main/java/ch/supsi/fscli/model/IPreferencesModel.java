package ch.supsi.fscli.model;

public interface IPreferencesModel {

    void setNumCommandLineColumns(int columns);
    void setNumVisibleLineCL(int numVisibleLineCL);
    void setNumVisibleLineOutputArea(int numVisibleLineOutputArea);
    void setLanguage(String language);

    int getNumCommandLineColumns();
    int getNumVisibleLineCL();
    int getNumVisibleLineOutputArea();
    String getLanguage();

    void setCommandLineFont(String font);
    void setOutputAreaFont(String font);
    void setLogAreaFont(String font);

    String getCommandLineFont();
    String getOutputAreaFont();
    String getLogAreaFont();

    void save();
}
