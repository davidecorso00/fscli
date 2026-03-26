package ch.supsi.fscli.model.commands;

import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.data_access.CommandResultStatus;
import ch.supsi.fscli.model.command_management.ParsedCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

public class ClearCommandTest {

    private ClearCommand clear;

    @BeforeEach
    void setup() {
        this.clear = new ClearCommand();
    }

    @Test
    public void testParse_WithNoArguments_Success() throws ParseException {
        List<String> args = Collections.emptyList();

        ParsedCommand result = clear.parse(args);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("clear", result.getCommand());
        Assertions.assertTrue(result.getArguments().isEmpty());
    }

    @Test
    public void testParse_WithOneArgument_ThrowsParseException() {
        List<String> args = List.of("unexpected");

        ParseException exception = Assertions.assertThrows(
                ParseException.class,
                () -> clear.parse(args)
        );

        Assertions.assertEquals("label.clear.usage", exception.getMessage());
        Assertions.assertEquals(0, exception.getErrorOffset());
    }

    @Test
    public void testParse_WithMultipleArguments_ThrowsParseException() {
        List<String> args = List.of("arg1", "arg2", "arg3");

        ParseException exception = Assertions.assertThrows(
                ParseException.class,
                () -> clear.parse(args)
        );

        Assertions.assertEquals("label.clear.usage", exception.getMessage());
        Assertions.assertEquals(0, exception.getErrorOffset());
    }

    @Test
    public void testExecute_ReturnsClearOutputStatus() {
        ParsedCommand cmd = new ParsedCommand("clear", Collections.emptyList());

        CommandResult result = clear.execute(cmd);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(CommandResultStatus.CLEAR_OUTPUT, result.getStatus());
        Assertions.assertEquals("", result.getMessage());
    }

    @Test
    public void testExecute_MessageIsEmpty() {
        ParsedCommand cmd = new ParsedCommand("clear", Collections.emptyList());

        CommandResult result = clear.execute(cmd);

        Assertions.assertTrue(result.getMessage().isEmpty());
    }
}