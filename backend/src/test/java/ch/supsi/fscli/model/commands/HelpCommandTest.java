package ch.supsi.fscli.model.commands;

import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.data_access.CommandResultStatus;
import ch.supsi.fscli.model.CommandsList;
import ch.supsi.fscli.model.command_management.ParsedCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class HelpCommandTest {

    private HelpCommand help;

    @Mock
    private CommandsList mockCommandsList;

    @BeforeEach
    void setup() {
        this.help = new HelpCommand();
    }

    @Test
    void parse_withNoArguments_success() throws ParseException {
        ParsedCommand result = help.parse(Collections.emptyList());

        Assertions.assertEquals("help", result.getCommand());
        Assertions.assertTrue(result.getArguments().isEmpty());
    }

    @Test
    void parse_withOneArgument_throwsParseException() {
        ParseException exception = Assertions.assertThrows(
                ParseException.class,
                () -> help.parse(List.of("unexpected"))
        );

        Assertions.assertEquals("label.help.usage", exception.getMessage());
        Assertions.assertEquals(0, exception.getErrorOffset());
    }

    @Test
    void parse_withMultipleArguments_throwsParseException() {
        ParseException exception = Assertions.assertThrows(
                ParseException.class,
                () -> help.parse(List.of("arg1", "arg2"))
        );

        Assertions.assertEquals("label.help.usage", exception.getMessage());
    }

    @Test
    void execute_returnsSuccessStatus() {
        try (MockedStatic<CommandsList> mockedStatic = Mockito.mockStatic(CommandsList.class)) {
            // Setup mock
            String expectedCommands = "Available commands:\n- cd\n- ls\n- mkdir\n- help";
            mockedStatic.when(CommandsList::getInstance).thenReturn(mockCommandsList);
            Mockito.when(mockCommandsList.getAllCommands()).thenReturn(expectedCommands);

            ParsedCommand cmd = new ParsedCommand("help", Collections.emptyList());

            // Execute
            CommandResult result = help.execute(cmd);

            // Assert
            Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
            Assertions.assertEquals(expectedCommands, result.getMessage());
        }
    }

    @Test
    void execute_callsGetAllCommandsExactlyOnce() {
        try (MockedStatic<CommandsList> mockedStatic = Mockito.mockStatic(CommandsList.class)) {
            // Setup
            mockedStatic.when(CommandsList::getInstance).thenReturn(mockCommandsList);
            Mockito.when(mockCommandsList.getAllCommands()).thenReturn("test commands");

            ParsedCommand cmd = new ParsedCommand("help", Collections.emptyList());

            // Execute
            help.execute(cmd);

            // Verify interaction
            Mockito.verify(mockCommandsList, Mockito.times(1)).getAllCommands();
        }
    }

    @Test
    void execute_returnsExactStringFromCommandsList() {
        try (MockedStatic<CommandsList> mockedStatic = Mockito.mockStatic(CommandsList.class)) {
            // Setup con stringa specifica
            String mockCommands = "Mock Commands:\n  cd - change directory\n  ls - list files";
            mockedStatic.when(CommandsList::getInstance).thenReturn(mockCommandsList);
            Mockito.when(mockCommandsList.getAllCommands()).thenReturn(mockCommands);

            ParsedCommand cmd = new ParsedCommand("help", Collections.emptyList());

            // Execute
            CommandResult result = help.execute(cmd);

            // Assert - verifica stringa esatta
            Assertions.assertEquals(mockCommands, result.getMessage());
        }
    }

    @Test
    void execute_whenCommandsListEmpty_returnsEmptyString() {
        try (MockedStatic<CommandsList> mockedStatic = Mockito.mockStatic(CommandsList.class)) {
            // Setup - lista vuota
            mockedStatic.when(CommandsList::getInstance).thenReturn(mockCommandsList);
            Mockito.when(mockCommandsList.getAllCommands()).thenReturn("");

            ParsedCommand cmd = new ParsedCommand("help", Collections.emptyList());

            // Execute
            CommandResult result = help.execute(cmd);

            // Assert
            Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
            Assertions.assertEquals("", result.getMessage());
        }
    }

    @Test
    void execute_multipleCallsToExecute_callsGetAllCommandsMultipleTimes() {
        try (MockedStatic<CommandsList> mockedStatic = Mockito.mockStatic(CommandsList.class)) {
            // Setup
            mockedStatic.when(CommandsList::getInstance).thenReturn(mockCommandsList);
            Mockito.when(mockCommandsList.getAllCommands()).thenReturn("commands");

            ParsedCommand cmd = new ParsedCommand("help", Collections.emptyList());

            // Execute multiple times
            help.execute(cmd);
            help.execute(cmd);
            help.execute(cmd);

            // Verify called 3 times
            Mockito.verify(mockCommandsList, Mockito.times(3)).getAllCommands();
        }
    }

    @Test
    void execute_doesNotModifyCommandsList() {
        try (MockedStatic<CommandsList> mockedStatic = Mockito.mockStatic(CommandsList.class)) {
            // Setup
            mockedStatic.when(CommandsList::getInstance).thenReturn(mockCommandsList);
            Mockito.when(mockCommandsList.getAllCommands()).thenReturn("commands");

            ParsedCommand cmd = new ParsedCommand("help", Collections.emptyList());

            // Execute
            help.execute(cmd);

            // Verify - solo getAllCommands chiamato, nessun setter/modifier
            Mockito.verify(mockCommandsList, Mockito.only()).getAllCommands();
            Mockito.verifyNoMoreInteractions(mockCommandsList);
        }
    }

    @Test
    void execute_withDifferentCommandsListContent_returnsDifferentResults() {
        try (MockedStatic<CommandsList> mockedStatic = Mockito.mockStatic(CommandsList.class)) {
            mockedStatic.when(CommandsList::getInstance).thenReturn(mockCommandsList);

            ParsedCommand cmd = new ParsedCommand("help", Collections.emptyList());

            // Prima esecuzione con un set di comandi
            Mockito.when(mockCommandsList.getAllCommands()).thenReturn("Set 1");
            CommandResult result1 = help.execute(cmd);
            Assertions.assertEquals("Set 1", result1.getMessage());

            // Seconda esecuzione con comandi diversi
            Mockito.when(mockCommandsList.getAllCommands()).thenReturn("Set 2");
            CommandResult result2 = help.execute(cmd);
            Assertions.assertEquals("Set 2", result2.getMessage());

            // Verifica chiamato 2 volte
            Mockito.verify(mockCommandsList, Mockito.times(2)).getAllCommands();
        }
    }

    @Test
    void execute_doesNotCallGetInstanceDirectly() {
        try (MockedStatic<CommandsList> mockedStatic = Mockito.mockStatic(CommandsList.class)) {
            // Setup
            mockedStatic.when(CommandsList::getInstance).thenReturn(mockCommandsList);
            Mockito.when(mockCommandsList.getAllCommands()).thenReturn("commands");

            ParsedCommand cmd = new ParsedCommand("help", Collections.emptyList());

            // Execute
            help.execute(cmd);

            // Verify getInstance was called
            mockedStatic.verify(CommandsList::getInstance, Mockito.times(1));
        }
    }
}