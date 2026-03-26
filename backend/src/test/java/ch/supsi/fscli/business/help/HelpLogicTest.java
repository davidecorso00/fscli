package ch.supsi.fscli.business.help;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class HelpLogicTest {

    @BeforeEach
    @AfterEach
    public void resetSingleton() throws Exception {
        Field instance = HelpLogic.class.getDeclaredField("myself");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testGetInstance() {
        HelpLogic instance1 = HelpLogic.getInstance();
        HelpLogic instance2 = HelpLogic.getInstance();

        assertSame(instance1, instance2);
        assertNotNull(HelpLogic.getInstance());
    }

    @Test
    public void testGetHelp() {
        HelpLogic logic = HelpLogic.getInstance();
        String help = logic.getHelp();
        assertNotNull(logic.getHelp());
        assertFalse(help.isEmpty());
        assertTrue(help.contains("Commands:"));
    }

}