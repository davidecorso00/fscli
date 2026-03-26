package ch.supsi.fscli.data_access;

import java.io.IOException;
import java.nio.file.Path;

public interface JsonLoadDataAccessInterface {
    <T> T loadFromFile(Path path, Class<T> clazz) throws IOException;
}
