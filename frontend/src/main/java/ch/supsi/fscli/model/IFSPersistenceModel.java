package ch.supsi.fscli.model;

import java.nio.file.Path;

public interface IFSPersistenceModel {
    void save();
    void saveAs(String fileName, Path filePath);
    boolean open(Path path, String fileName);


}
