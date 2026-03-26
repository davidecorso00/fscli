package ch.supsi.fscli.application.preferences;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class PreferencesAppTest {

    @BeforeEach
    @AfterEach
    public void resetSingleton() throws Exception {
        Field instance = PreferencesApp.class.getDeclaredField("myself");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testGetInstanceNotNull() {
        assertNotNull(PreferencesApp.getInstance());
    }

    @Test
    public void testGetInstanceReturnsSameObject() {
        PreferencesApp instance1 = PreferencesApp.getInstance();
        PreferencesApp instance2 = PreferencesApp.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    public void testSetAndGetNumCommandLineColumns() {
        PreferencesApp app = PreferencesApp.getInstance();
        app.setNumCommandLineColumns(80);
        assertEquals(80, app.getNumCommandLineColumns());
    }

    @Test
    public void testSetAndGetNumVisibleLineCL() {
        PreferencesApp app = PreferencesApp.getInstance();
        app.setNumVisibleLineCL(10);
        assertEquals(10, app.getNumVisibleLineCL());
    }

    @Test
    public void testSetAndGetNumVisibleLineOutputArea() {
        PreferencesApp app = PreferencesApp.getInstance();
        app.setNumVisibleLineOutputArea(20);
        assertEquals(20, app.getNumVisibleLineOutputArea());
    }

    @Test
    public void testSetAndGetLanguage() {
        PreferencesApp app = PreferencesApp.getInstance();
        app.setLanguage("en");
        assertEquals("en", app.getLanguage());
    }

    @Test
    public void testSetAndGetCommandLineFont() {
        PreferencesApp app = PreferencesApp.getInstance();
        app.setCommandLineFont("Courier");
        assertEquals("Courier", app.getCommandLineFont());
    }

    @Test
    public void testSetAndGetOutputAreaFont() {
        PreferencesApp app = PreferencesApp.getInstance();
        app.setOutputAreaFont("Arial");
        assertEquals("Arial", app.getOutputAreaFont());
    }

    @Test
    public void testSetAndGetLogAreaFont() {
        PreferencesApp app = PreferencesApp.getInstance();
        app.setLogAreaFont("Monospace");
        assertEquals("Monospace", app.getLogAreaFont());
    }

    @Test
    public void testSave() {
        PreferencesApp app = PreferencesApp.getInstance();
        app.setNumCommandLineColumns(100);
        app.setLanguage("it");
        assertDoesNotThrow(app::save);
    }
}