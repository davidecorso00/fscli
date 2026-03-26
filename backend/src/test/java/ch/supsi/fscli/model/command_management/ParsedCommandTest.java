package ch.supsi.fscli.model.command_management;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ParsedCommandTest {

    @Test
    void testConstructorAndGetters() {
        List<String> args = List.of("arg1", "arg2");
        Map<String, Boolean> flags = new HashMap<>();
        flags.put("f", true);

        ParsedCommand cmd = new ParsedCommand("test", args, flags);

        assertEquals("test", cmd.getCommand());
        assertEquals(2, cmd.getArguments().size());
        assertEquals("arg1", cmd.getArguments().get(0));
        assertTrue(cmd.getFlag("f"));
        assertFalse(cmd.getFlag("nonexistent"));
    }

    @Test
    void testConstructorWithoutFlags() {
        ParsedCommand cmd = new ParsedCommand("simple", List.of());
        assertNotNull(cmd.getArguments());
        assertTrue(cmd.getArguments().isEmpty());
        assertFalse(cmd.getFlag("any"));
    }

    @Test
    void testSetFlag() {
        ParsedCommand cmd = new ParsedCommand("cmd", List.of());
        cmd.setFlag("x", true);
        assertTrue(cmd.getFlag("x"));
    }
}