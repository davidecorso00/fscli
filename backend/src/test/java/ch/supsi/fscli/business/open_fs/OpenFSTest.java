package ch.supsi.fscli.business.open_fs;

import ch.supsi.fscli.model.inode.FileSystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class OpenFSTest {

    @BeforeEach
    @AfterEach
    void resetSingletons() throws Exception {
        Field openFsField = OpenFS.class.getDeclaredField("myself");
        openFsField.setAccessible(true);
        openFsField.set(null, null);

        FileSystem.resetInstance();
    }

    @Test
    void openNullPath() {
        OpenFS openFS = OpenFS.getInstance();
        boolean result = openFS.open(null);
        assertFalse(result);
    }

    @Test
    void openInvalidFile() throws Exception {
        OpenFS openFS = OpenFS.getInstance();
        Path file = Files.createTempFile("fscli-open-invalid-", ".json");
        file.toFile().deleteOnExit();
        Files.writeString(file, "not a valid json");

        boolean result = openFS.open(file);

        assertFalse(result);
    }

    @Test
    void openValidFile() throws Exception {
        Path file = Files.createTempFile("fscli-open-valid-", ".json");
        file.toFile().deleteOnExit();

        FileSystem fs = FileSystem.getInstance();
        new ch.supsi.fscli.data_access.JsonSaveDataAccess().saveToFile(file, fs);

        OpenFS openFS = OpenFS.getInstance();
        boolean result = openFS.open(file);

        assertTrue(result);
        assertNotNull(FileSystem.getInstance().getRoot());
    }
}
