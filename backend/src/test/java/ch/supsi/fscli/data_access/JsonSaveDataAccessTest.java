package ch.supsi.fscli.data_access;

import ch.supsi.fscli.model.inode.FileSystem;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JsonSaveDataAccessTest {

    @Test
    void saveToFile_createsFileAndWritesJson() throws Exception {
        FileSystem fs = FileSystem.getInstance();
        Path tempFile = Files.createTempFile("fscli-save-ok-", ".json");
        tempFile.toFile().deleteOnExit();

        JsonSaveDataAccess save = new JsonSaveDataAccess();

        save.saveToFile(tempFile, fs);

        assertTrue(Files.exists(tempFile));
        String content = Files.readString(tempFile);
        assertFalse(content.isBlank());
    }

    @Test
    void saveToFile_invalidPath_throwsIOException() {
        Path invalidPath = Path.of("/oinwd2/invalid/path/fs.json");
        JsonSaveDataAccess save = new JsonSaveDataAccess();

        assertThrows(IOException.class,
                () -> save.saveToFile(invalidPath, FileSystem.getInstance()));
    }
}
