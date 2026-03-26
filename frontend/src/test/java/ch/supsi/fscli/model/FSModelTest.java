package ch.supsi.fscli.model;

import ch.supsi.fscli.application.IFSApplication;
import ch.supsi.fscli.application.persistence.IFSPersistenceApplication;
import ch.supsi.fscli.model.inode.FSStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FSModelTest {

    @Mock
    private IFSApplication mockFsConfig;

    @Mock
    private IFSPersistenceApplication mockFsPersistence;

    // Salviamo i valori originali per ripristinarli alla fine (pulizia)
    private Object originalConfig;
    private Object originalPersistence;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // 1. REFLECTION su 'fsConfiguration' (campo statico)
        Field configField = FSModel.class.getDeclaredField("fsConfiguration");
        configField.setAccessible(true);
        originalConfig = configField.get(null); // Salviamo l'originale
        configField.set(null, mockFsConfig);    // Iniettiamo il mock (null perché è statico)

        // 2. REFLECTION su 'fsPersistence' (campo statico)
        Field persistField = FSModel.class.getDeclaredField("fsPersistence");
        persistField.setAccessible(true);
        originalPersistence = persistField.get(null); // Salviamo l'originale
        persistField.set(null, mockFsPersistence);    // Iniettiamo il mock
    }

    @AfterEach
    void tearDown() throws Exception {
        // Ripristiniamo i valori originali per non rompere altri test
        Field configField = FSModel.class.getDeclaredField("fsConfiguration");
        configField.setAccessible(true);
        configField.set(null, originalConfig);

        Field persistField = FSModel.class.getDeclaredField("fsPersistence");
        persistField.setAccessible(true);
        persistField.set(null, originalPersistence);
    }

    @Test
    void testNewFileSystem() {
        FSModel model = FSModel.getInstance();
        model.newFileSystem();

        verify(mockFsConfig).newFileSystem();
        assertEquals(FSStatus.NEW_FILESYSTEM, model.getStatus());
    }

    @Test
    void testSaveAs() {
        FSModel model = FSModel.getInstance();
        Path mockPath = Path.of("test.json");
        model.saveAs("test.json", mockPath);

        verify(mockFsPersistence).saveAs("test.json", mockPath);
        assertEquals(FSStatus.SAVE, model.getStatus());
    }
}