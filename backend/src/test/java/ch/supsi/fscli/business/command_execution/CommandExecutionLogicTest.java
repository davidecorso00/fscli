package ch.supsi.fscli.business.command_execution;

import ch.supsi.fscli.data_access.CommandResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class CommandExecutionLogicTest {

    @BeforeEach
    @AfterEach
    public void resetSingleton() throws Exception {
        Field instance = CommandExecutionLogic.class.getDeclaredField("myself");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testGetInstance() {
        CommandExecutionLogic instance1 = CommandExecutionLogic.getInstance();
        assertNotNull(instance1);
        assertSame(instance1, CommandExecutionLogic.getInstance());
    }

    @Test
    public void testExecuteCommandDelegation() {
        CommandExecutionLogic logic = CommandExecutionLogic.getInstance();

        String command = "pwd";

        CommandResult result = logic.executeCommand(command);

        assertNotNull(result, "Il risultato non deve essere null");
        assertNotNull(result.getStatus(), "Lo status del risultato non deve essere null");
    }
}