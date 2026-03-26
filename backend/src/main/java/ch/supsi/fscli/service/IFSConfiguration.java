package ch.supsi.fscli.service;

import java.nio.file.Path;

public interface IFSConfiguration {
    void newFileSystem();
    boolean checkForQuit(Path lastSavedPath);
}
