package ch.supsi.fscli.service;

import java.nio.file.Path;

public interface IFSPersistence {
    void save();
    void saveAs(Path filePath);
    boolean open(Path filePath);
    Path getCurrentFilePath();
    void setCurrentFilePath(Path filePath);
}
