package ch.supsi.fscli.business.open_fs;

import java.nio.file.Path;

public interface IOpenFS {
    boolean open(Path path);
}
