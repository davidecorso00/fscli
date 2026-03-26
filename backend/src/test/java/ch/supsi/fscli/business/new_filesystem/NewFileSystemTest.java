package ch.supsi.fscli.business.new_filesystem;

import ch.supsi.fscli.model.inode.FileSystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class NewFileSystemTest {

    private NewFileSystem service = NewFileSystem.getInstance();

    @BeforeEach
    void setUp() throws Exception {
        Field f = FileSystem.class.getDeclaredField("myself");
        f.setAccessible(true);
        f.set(null, null);
    }

    @AfterEach
    void tearDown() throws Exception {
        Field f = FileSystem.class.getDeclaredField("myself");
        f.setAccessible(true);
        if (f.get(null) == null) {
            FileSystem.resetInstance();
        }
    }

    @Test
    void testIsRootNullAndNewFileSystem() {
        assertTrue(service.isRootNull(), "Expected root null before creating FS");

        service.newFileSystem();

        assertFalse(service.isRootNull(), "Expected root not null after creating FS");

        FileSystem fs = FileSystem.getInstance();
        assertNotNull(fs);
        assertNotNull(fs.getRoot());
    }
}

