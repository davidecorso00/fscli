package ch.supsi.fscli.data_access;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CommandResultTest {

    @Test
    public void testShouldClearTerminal() {
        CommandResult normalResult = new CommandResult("Ok", CommandResultStatus.SUCCESS);
        assertFalse(normalResult.shouldClearTerminal());

        CommandResult errorResult = new CommandResult("Err", CommandResultStatus.ERROR);
        assertFalse(errorResult.shouldClearTerminal());

        CommandResult clearResult = new CommandResult("", CommandResultStatus.CLEAR_OUTPUT);
        assertTrue(clearResult.shouldClearTerminal());
    }
}