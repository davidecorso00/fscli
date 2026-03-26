package ch.supsi.fscli.application.persistence;

import java.nio.file.Path;

public interface IFSPersistenceApplication {
    void save();
    void saveAs(String fileName, Path path);
    boolean open(Path path, String fileName);
}
