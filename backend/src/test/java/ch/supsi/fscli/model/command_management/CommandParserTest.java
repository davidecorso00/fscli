package ch.supsi.fscli.model.command_management;

import ch.supsi.fscli.model.commands.ICommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class CommandParserTest {

    private CommandParser parser;
    private ICommand mockCommand;

    @BeforeEach
    void setUp() throws Exception {
        Field instance = CommandParser.class.getDeclaredField("myself");
        instance.setAccessible(true);
        instance.set(null, null);

        parser = CommandParser.getInstance();

        mockCommand = Mockito.mock(ICommand.class);

        Field commandsField = CommandParser.class.getDeclaredField("commands");
        commandsField.setAccessible(true);
        Map<String, ICommand> commandMap = (Map<String, ICommand>) commandsField.get(parser);

        commandMap.clear();
        commandMap.put("testcmd", mockCommand);
    }

    @Test
    void testParseValidCommand() throws ParseException {
        // Configura il mock
        ParsedCommand expectedResult = new ParsedCommand("testcmd", List.of("arg1"));
        when(mockCommand.parse(anyList())).thenReturn(expectedResult);

        // Esegui
        ParsedCommand result = parser.parse("testcmd arg1");

        // Verifica
        assertNotNull(result);
        assertEquals("testcmd", result.getCommand());
        verify(mockCommand).parse(argThat(list -> list.contains("arg1")));
    }

    @Test
    void testParseUnknownCommand() {
        assertThrows(ParseException.class, () -> parser.parse("unknown command"));
    }

    @Test
    void testParseEmptyInput() {
        assertThrows(ParseException.class, () -> parser.parse("   "));
    }
}