package ch.supsi.fscli.business.save_fs;

import java.io.IOException;
import java.nio.file.Path;

public interface ISaveFS {
    void save(Path path) throws IOException;
    void saveAs(Path path) throws IOException;
    Path getLastSavedPath();
    void setLastSavedPath(Path path);
}
