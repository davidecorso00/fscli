package ch.supsi.fscli.service;

import ch.supsi.fscli.business.new_filesystem.NewFileSystem;
import ch.supsi.fscli.model.inode.FileSystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class FSConfigurationServiceTest {

    private FSConfigurationService service;

    @BeforeEach
    public void setUp() throws Exception {
        resetSingletons();
        service = new FSConfigurationService();
    }

    @AfterEach
    public void tearDown() throws Exception {
        resetSingletons();
    }

    private void resetSingletons() throws Exception {
        Field nfsField = NewFileSystem.class.getDeclaredField("myself");
        nfsField.setAccessible(true);
        nfsField.set(null, null);

        Field fsField = FileSystem.class.getDeclaredField("myself");
        fsField.setAccessible(true);
        fsField.set(null, null);
    }

    @Test
    public void testNewFileSystemCreatesInstance() {
        service.newFileSystem();

        assertFalse(service.isRootNull(), "Dopo newFileSystem, la root non dovrebbe essere null");
        assertNotNull(FileSystem.getInstance(), "Il FileSystem dovrebbe essere stato istanziato");
    }

    @Test
    public void testIsRootNull() {
        service.newFileSystem();
        assertFalse(service.isRootNull());
    }
}