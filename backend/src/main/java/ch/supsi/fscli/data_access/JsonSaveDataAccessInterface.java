package ch.supsi.fscli.data_access;

import java.io.IOException;
import java.nio.file.Path;

public interface JsonSaveDataAccessInterface {

    void saveToFile(Path path, Object objectToSave) throws IOException;

    void ensureDirectoryExists(Path dir) throws IOException;

}
