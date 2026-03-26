package ch.supsi.fscli.application.help;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class HelpAppTest {

    @BeforeEach
    @AfterEach
    public void resetSingleton() throws Exception {
        Field instance = HelpApp.class.getDeclaredField("myself");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testGetInstanceNotNull() {
        assertNotNull(HelpApp.getInstance());
    }

    @Test
    public void testGetInstanceReturnsSameObject() {
        HelpApp instance1 = HelpApp.getInstance();
        HelpApp instance2 = HelpApp.getInstance();
        assertSame(instance1, instance2);
        assertNotNull(HelpApp.getInstance());
        assertNotNull(HelpApp.getInstance());
    }

    @Test
    public void testGetHelp() {
        HelpApp app = HelpApp.getInstance();
        assertNotNull(app.getHelp());
    }
}