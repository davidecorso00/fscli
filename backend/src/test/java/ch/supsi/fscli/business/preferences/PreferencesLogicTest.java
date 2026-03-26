package ch.supsi.fscli.business.preferences;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class PreferencesLogicTest {

    @BeforeEach
    @AfterEach
    public void resetSingleton() throws Exception {
        Field instance = PreferencesLogic.class.getDeclaredField("myself");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testGetInstanceNotNull() {
        assertNotNull(PreferencesLogic.getInstance());
    }

    @Test
    public void testGetInstanceReturnsSameObject() {
        PreferencesLogic instance1 = PreferencesLogic.getInstance();
        PreferencesLogic instance2 = PreferencesLogic.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    public void testSetAndGetNumCommandLineColumns() {
        PreferencesLogic logic = PreferencesLogic.getInstance();
        logic.setNumCommandLineColumns(80);
        assertEquals(80, logic.getNumCommandLineColumns());
    }

    @Test
    public void testSetAndGetNumVisibleLineCL() {
        PreferencesLogic logic = PreferencesLogic.getInstance();
        logic.setNumVisibleLineCL(10);
        assertEquals(10, logic.getNumVisibleLineCL());
    }

    @Test
    public void testSetAndGetNumVisibleLineOutputArea() {
        PreferencesLogic logic = PreferencesLogic.getInstance();
        logic.setNumVisibleLineOutputArea(20);
        assertEquals(20, logic.getNumVisibleLineOutputArea());
    }

    @Test
    public void testSetAndGetLanguage() {
        PreferencesLogic logic = PreferencesLogic.getInstance();
        logic.setLanguage("en");
        assertEquals("en", logic.getLanguage());
    }

    @Test
    public void testSetAndGetCommandLineFont() {
        PreferencesLogic logic = PreferencesLogic.getInstance();
        logic.setCommandLineFont("Courier");
        assertEquals("Courier", logic.getCommandLineFont());
    }

    @Test
    public void testSetAndGetOutputAreaFont() {
        PreferencesLogic logic = PreferencesLogic.getInstance();
        logic.setOutputAreaFont("Arial");
        assertEquals("Arial", logic.getOutputAreaFont());
    }

    @Test
    public void testSetAndGetLogAreaFont() {
        PreferencesLogic logic = PreferencesLogic.getInstance();
        logic.setLogAreaFont("Monospace");
        assertEquals("Monospace", logic.getLogAreaFont());
    }

    @Test
    public void testSave() {
        PreferencesLogic logic = PreferencesLogic.getInstance();
        logic.setNumCommandLineColumns(100);
        assertDoesNotThrow(logic::save);
    }
}