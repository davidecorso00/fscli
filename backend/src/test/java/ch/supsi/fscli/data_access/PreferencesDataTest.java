package ch.supsi.fscli.data_access;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class PreferencesDataTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    @AfterEach
    public void resetSingleton() throws Exception {
        Field instance = PreferencesData.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testSaveAndLoadPreferences() throws Exception {
        Path tempConfigFile = tempDir.resolve("test-prefs.txt");

        overrideConfigFileLocation(tempConfigFile.toString());

        PreferencesData prefs = PreferencesData.getInstance();
        assertNotNull(prefs);

        prefs.setLanguage("en");
        prefs.setNumCommandLineColumns(120);

        boolean saved = prefs.save();
        assertTrue(saved, "Il salvataggio dovrebbe avere successo");
        assertTrue(tempConfigFile.toFile().exists(), "Il file di configurazione dovrebbe essere stato creato");

        resetSingleton();

        PreferencesData reloadedPrefs = PreferencesData.getInstance();
        assertEquals("en", reloadedPrefs.getLanguage());
        assertEquals(120, reloadedPrefs.getNumCommandLineColumns());
    }

    @Test
    public void testDefaults() throws Exception {
        Path tempConfigFile = tempDir.resolve("non-existent.txt");
        overrideConfigFileLocation(tempConfigFile.toString());

        PreferencesData prefs = PreferencesData.getInstance();

        assertTrue(prefs.getNumCommandLineColumns() > 0);
        assertNotNull(prefs.getCommandLineFont());
        assertEquals("it", prefs.getLanguage());
    }

    private void overrideConfigFileLocation(String newPath) throws Exception {
        Field field = PreferencesData.class.getDeclaredField("CONFIG_FILE");
        field.setAccessible(true);
        field.set(null, newPath);
    }
}