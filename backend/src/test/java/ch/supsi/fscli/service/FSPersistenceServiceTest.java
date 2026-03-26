package ch.supsi.fscli.service;

import ch.supsi.fscli.business.open_fs.OpenFS;
import ch.supsi.fscli.business.save_fs.SaveFS;
import ch.supsi.fscli.model.inode.FileSystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FSPersistenceServiceTest {

    private FSPersistenceService service;

    @BeforeEach
    void setup() throws Exception {
        // 1. Reset Singleton SaveFS
        resetSingleton(SaveFS.class, "myself");

        // 2. Reset Singleton OpenFS
        resetSingleton(OpenFS.class, "myself");

        // 3. Reset Singleton FileSystem
        FileSystem.resetInstance();

        // 4. Inizializza il servizio (che ora prenderà le nuove istanze pulite)
        service = new FSPersistenceService();
    }

    @AfterEach
    void cleanup() throws Exception {
        // Pulizia finale per non sporcare i test successivi
        resetSingleton(SaveFS.class, "myself");
        resetSingleton(OpenFS.class, "myself");
        FileSystem.resetInstance();
    }

    // Metodo helper per evitare duplicazione codice
    private void resetSingleton(Class<?> clazz, String fieldName) throws Exception {
        Field instance = clazz.getDeclaredField(fieldName);
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void saveNoCurrentFile() {
        service.setCurrentFilePath(null);
        service.save();
        assertNull(SaveFS.getInstance().getLastSavedPath());
    }

    @Test
    void saveWithCurrentFile() throws Exception {
        Path file = Files.createTempFile("fscli-service-save-", ".json");
        file.toFile().deleteOnExit();

        service.setCurrentFilePath(file);
        service.save();

        assertEquals(file, SaveFS.getInstance().getLastSavedPath());
    }

    @Test
    void saveAsSetsCurrentFilePathAndSaves() throws Exception {
        Path file = Files.createTempFile("fscli-service-saveas-", ".json");
        file.toFile().deleteOnExit();

        service.saveAs(file);

        assertEquals(file, service.getCurrentFilePath());
        assertEquals(file, SaveFS.getInstance().getLastSavedPath());
    }

    @Test
    void openNullPath() {
        boolean result = service.open(null);
        assertFalse(result);
        assertNull(service.getCurrentFilePath());
    }

    @Test
    void openValidFileUpdatesCurrentFilePathOnSuccess() throws Exception {
        Path file = Files.createTempFile("fscli-service-open-", ".json");
        file.toFile().deleteOnExit();

        new ch.supsi.fscli.data_access.JsonSaveDataAccess()
                .saveToFile(file, ch.supsi.fscli.model.inode.FileSystem.getInstance());

        boolean result = service.open(file);

        assertTrue(result);
        assertEquals(file, service.getCurrentFilePath());
    }
}
