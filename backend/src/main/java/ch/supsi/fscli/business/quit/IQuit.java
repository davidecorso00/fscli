package ch.supsi.fscli.business.quit;

import java.nio.file.Path;

public interface IQuit {
    boolean checkForQuit(Path lastSavedPath);
}
