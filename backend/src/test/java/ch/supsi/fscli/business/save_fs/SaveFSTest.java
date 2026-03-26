package ch.supsi.fscli.business.save_fs;

import ch.supsi.fscli.model.inode.FileSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SaveFSTest {

    @BeforeEach
    void reset() throws Exception {
        Field instance = SaveFS.class.getDeclaredField("myself");
        instance.setAccessible(true);
        instance.set(null, null);

        FileSystem.resetInstance();
    }

    @Test
    void saveNullPath() {
        SaveFS saveFS = SaveFS.getInstance();
        assertThrows(IllegalArgumentException.class, () -> saveFS.save(null));
    }

    @Test
    void saveFile() throws Exception {
        SaveFS saveFS = SaveFS.getInstance();
        FileSystem fs = FileSystem.getInstance();

        Path file = Files.createTempFile("fscli-", ".json");
        file.toFile().deleteOnExit();

        saveFS.save(file);

        assertTrue(Files.exists(file));
        assertEquals(file, saveFS.getLastSavedPath());
    }

    @Test
    void saveAsFile() throws Exception {
        SaveFS saveFS = SaveFS.getInstance();
        FileSystem fs = FileSystem.getInstance();

        Path file = Files.createTempFile("fscli-as-", ".json");
        file.toFile().deleteOnExit();

        saveFS.saveAs(file);

        assertTrue(Files.exists(file));
        assertEquals(file, SaveFS.getInstance().getLastSavedPath());
    }

    @Test
    void getAndSetLastSavedPath() {
        SaveFS saveFS = SaveFS.getInstance();
        Path file = Path.of("test.json");

        saveFS.setLastSavedPath(file);

        assertEquals(file, saveFS.getLastSavedPath());
    }
}
