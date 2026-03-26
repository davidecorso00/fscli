package ch.supsi.fscli.business.preferences;

public interface IPreferencesLogic{

    void setNumCommandLineColumns(int columns);
    void setNumVisibleLineCL(int numVisibleLineCL);
    void setNumVisibleLineOutputArea(int numVisibleLineOutputArea);
    void setLanguage(String language);

    int getNumVisibleLineCL();
    int getNumVisibleLineOutputArea();
    int getNumCommandLineColumns();
    String getLanguage();

    void setCommandLineFont(String font);
    void setOutputAreaFont(String font);
    void setLogAreaFont(String font);

    String getCommandLineFont();
    String getOutputAreaFont();
    String getLogAreaFont();

    void save();

}
