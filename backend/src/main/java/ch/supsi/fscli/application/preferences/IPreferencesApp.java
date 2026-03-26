package ch.supsi.fscli.application.preferences;

public interface IPreferencesApp {

     void setNumCommandLineColumns(int columns);
     void setNumVisibleLineCL(int numVisibleLineCL);
     void setNumVisibleLineOutputArea(int numVisibleLineOutputArea);
     void setLanguage(String language);

     int getNumVisibleLineOutputArea();
     int getNumCommandLineColumns();
     int getNumVisibleLineCL();
     String getLanguage();

    void setCommandLineFont(String font);
    void setOutputAreaFont(String font);
    void setLogAreaFont(String font);

    String getCommandLineFont();
    String getOutputAreaFont();
    String getLogAreaFont();

    void save();


}
