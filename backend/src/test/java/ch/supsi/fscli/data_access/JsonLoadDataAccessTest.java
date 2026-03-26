package ch.supsi.fscli.data_access;

import ch.supsi.fscli.model.inode.FileSystem;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JsonLoadDataAccessTest {

    @Test
    void loadFromFile_validJson_returnsFileSystem() throws Exception {
        FileSystem fs = FileSystem.getInstance();
        Path tempFile = Files.createTempFile("fscli-load-ok-", ".json");
        tempFile.toFile().deleteOnExit();

        JsonSaveDataAccess save = new JsonSaveDataAccess();
        save.saveToFile(tempFile, fs);

        JsonLoadDataAccess load = new JsonLoadDataAccess();

        FileSystem loaded = load.loadFromFile(tempFile, FileSystem.class);

        assertNotNull(loaded);
        assertNotNull(loaded.getRoot());
    }

    @Test
    void loadFromFile_invalidJson_throwsIOException() throws Exception {
        Path tempFile = Files.createTempFile("fscli-load-invalid-", ".json");
        tempFile.toFile().deleteOnExit();
        Files.writeString(tempFile, "this is not valid json at all");

        JsonLoadDataAccess load = new JsonLoadDataAccess();

        assertThrows(IOException.class,
                () -> load.loadFromFile(tempFile, FileSystem.class));
    }

    @Test
    void loadFromFile_unknownProperties_areIgnored() throws Exception {
        Path tempFile = Files.createTempFile("fscli-load-unknown-prop-", ".json");
        tempFile.toFile().deleteOnExit();

        String json = """
                ["ch.supsi.fscli.model.inode.FileSystem",
                 {"root":["ch.supsi.fscli.model.inode.DirectoryINode",
                          {"id":0,"linkCount":1,"entries":["java.util.HashMap",{}],
                           "someUnknownField":"ignored"}]}]
                """;
        Files.writeString(tempFile, json);

        JsonLoadDataAccess load = new JsonLoadDataAccess();

        FileSystem loaded = load.loadFromFile(tempFile, FileSystem.class);

        assertNotNull(loaded);
        assertNotNull(loaded.getRoot());
    }
}
