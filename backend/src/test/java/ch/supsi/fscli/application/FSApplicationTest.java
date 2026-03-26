package ch.supsi.fscli.application;

import ch.supsi.fscli.model.inode.FileSystem;
import ch.supsi.fscli.data_access.CommandResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class FSApplicationTest {
    private FSApplication app;

    @BeforeEach
    void setup() {
        app = FSApplication.getInstance();
        FileSystem.getInstance().loadFromFileSystem(FileSystem.getInstance());
    }

    @BeforeEach
    @AfterEach
    public void resetSingleton() throws Exception {
        Field instance = FSApplication.class.getDeclaredField("myself");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testGetInstance() {
        FSApplication app1 = FSApplication.getInstance();
        assertNotNull(app1);
        FSApplication app2 = FSApplication.getInstance();
        assertSame(app1, app2, "Singleton should return the same object");
    }

    @Test
    void newFileSystem() {
        app.newFileSystem();
        assertFalse(app.isRootNull());
    }

    @Test
    void saveDoesNotThrow() throws Exception {
        Path file = Files.createTempFile("fscli-app-save-", ".json");
        file.toFile().deleteOnExit();

        app.open(file, "test");
        app.save();
    }

    @Test
    void saveAsSavesToGivenPath() throws Exception {
        Path file = Files.createTempFile("fscli-app-saveas-", ".json");
        file.toFile().deleteOnExit();

        app.saveAs("test", file);

        assertTrue(Files.exists(file));
    }

    @Test
    void openWithValidPath() throws Exception {
        Path file = Files.createTempFile("fscli-app-open-", ".json");
        file.toFile().deleteOnExit();

        new ch.supsi.fscli.data_access.JsonSaveDataAccess()
                .saveToFile(file, FileSystem.getInstance());

        boolean result = app.open(file, "test");

        assertTrue(result);
    }

    @Test
    void openWithNullPath() {
        boolean result = app.open(null, "test");
        assertFalse(result);
    }

    @Test
    public void testIsRootNull() {
        FSApplication app = FSApplication.getInstance();
        assertDoesNotThrow(app::isRootNull);
    }

    @Test
    public void testNewFileSystem() {
        FSApplication app = FSApplication.getInstance();
        assertDoesNotThrow(app::newFileSystem);
    }

    @Test
    public void testExecuteCommand() {
        FSApplication app = FSApplication.getInstance();

        String testCommand = "pwd";
        CommandResult result = app.executeCommand(testCommand);

        assertNotNull(result, "Execution should return a CommandResult object");
    }
}
