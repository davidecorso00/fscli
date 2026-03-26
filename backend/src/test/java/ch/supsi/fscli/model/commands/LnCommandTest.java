package ch.supsi.fscli.model.commands;

import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.data_access.CommandResultStatus;
import ch.supsi.fscli.model.command_management.LocalizedParseException;
import ch.supsi.fscli.model.command_management.ParsedCommand;
import ch.supsi.fscli.model.inode.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.List;

public class LnCommandTest {

    private LnCommand ln;
    private FileSystem fs;

    @BeforeEach
    void setup() {
        FileSystem.resetInstance();
        this.fs = FileSystem.getInstance();
        this.ln = new LnCommand();
    }

    @AfterEach
    void cleanup() {
        FileSystem.resetInstance();
    }

    @Test
    void parse_twoArguments_hardLink() throws ParseException {
        ParsedCommand result = ln.parse(List.of("target", "link"));

        Assertions.assertEquals("ln", result.getCommand());
        Assertions.assertEquals(2, result.getArguments().size());
        Assertions.assertEquals("target", result.getArguments().get(0));
        Assertions.assertEquals("link", result.getArguments().get(1));
        Assertions.assertFalse(result.getFlag("s"));
    }

    @Test
    void parse_withFlagS_symbolicLink() throws ParseException {
        ParsedCommand result = ln.parse(List.of("-s", "target", "link"));

        Assertions.assertEquals(2, result.getArguments().size());
        Assertions.assertTrue(result.getFlag("s"));
    }

    @Test
    void parse_flagAtEnd_symbolicLink() throws ParseException {
        ParsedCommand result = ln.parse(List.of("target", "-s", "link"));

        Assertions.assertEquals(2, result.getArguments().size());
        Assertions.assertTrue(result.getFlag("s"));
    }

    @Test
    void parse_flagInMiddle_symbolicLink() throws ParseException {
        ParsedCommand result = ln.parse(List.of("target", "link", "-s"));

        Assertions.assertEquals(2, result.getArguments().size());
        Assertions.assertTrue(result.getFlag("s"));
    }

    @Test
    void parse_noArguments_throwsParseException() {
        ParseException exception = Assertions.assertThrows(
                ParseException.class,
                () -> ln.parse(List.of())
        );

        Assertions.assertEquals("label.ln.usage", exception.getMessage());
    }

    @Test
    void parse_oneArgument_throwsParseException() {
        ParseException exception = Assertions.assertThrows(
                ParseException.class,
                () -> ln.parse(List.of("target"))
        );

        Assertions.assertEquals("label.ln.usage", exception.getMessage());
    }

    @Test
    void parse_threeArguments_throwsParseException() {
        ParseException exception = Assertions.assertThrows(
                ParseException.class,
                () -> ln.parse(List.of("arg1", "arg2", "arg3"))
        );

        Assertions.assertEquals("label.ln.usage", exception.getMessage());
    }

    @Test
    void parse_multipleFlagsWithS_throwsException() {
        // -sx dove x è invalido
        Assertions.assertThrows(
                LocalizedParseException.class,
                () -> ln.parse(List.of("-sx", "target", "link"))
        );
    }

    @Test
    void execute_hardLink_success() {
        // Setup: crea file target
        FileINode targetFile = new FileINode();
        fs.getRoot().addEntry("target.txt", targetFile);

        ParsedCommand cmd = new ParsedCommand("ln", List.of("target.txt", "link.txt"));
        cmd.setFlag("s", false);

        CommandResult result = ln.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertEquals("", result.getMessage());

        // Verifica che il link esista
        FileSystemComponent link = fs.getRoot().findEntry("link.txt");
        Assertions.assertNotNull(link);
        Assertions.assertEquals(targetFile, link); // Stesso oggetto
    }

    @Test
    void execute_hardLink_incrementsLinkCount() {
        FileINode targetFile = new FileINode();
        fs.getRoot().addEntry("target.txt", targetFile);

        int initialCount = targetFile.getLinkCount();

        ParsedCommand cmd = new ParsedCommand("ln", List.of("target.txt", "link.txt"));
        cmd.setFlag("s", false);

        ln.execute(cmd);

        Assertions.assertEquals(initialCount + 1, targetFile.getLinkCount());
    }

    @Test
    void execute_hardLink_targetNotFound_error() {
        ParsedCommand cmd = new ParsedCommand("ln", List.of("nonexistent.txt", "link.txt"));
        cmd.setFlag("s", false);

        CommandResult result = ln.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.ln.targetNotFound", result.getMessage());
    }

    @Test
    void execute_hardLink_toDirectory_error() {
        DirectoryINode dir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("mydir", dir);

        ParsedCommand cmd = new ParsedCommand("ln", List.of("mydir", "link"));
        cmd.setFlag("s", false);

        CommandResult result = ln.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.ln.directoryNotAllowed", result.getMessage());
    }

    @Test
    void execute_hardLink_linkAlreadyExists_error() {
        FileINode file1 = new FileINode();
        FileINode file2 = new FileINode();
        fs.getRoot().addEntry("target.txt", file1);
        fs.getRoot().addEntry("existing.txt", file2);

        ParsedCommand cmd = new ParsedCommand("ln", List.of("target.txt", "existing.txt"));
        cmd.setFlag("s", false);

        CommandResult result = ln.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.ln.fileExists", result.getMessage());
    }

    @Test
    void execute_hardLink_emptyTargetPath_error() {
        ParsedCommand cmd = new ParsedCommand("ln", List.of("", "link.txt"));
        cmd.setFlag("s", false);

        CommandResult result = ln.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.ln.emptyTarget", result.getMessage());
    }

    @Test
    void execute_hardLink_emptyLinkPath_error() {
        FileINode file = new FileINode();
        fs.getRoot().addEntry("target.txt", file);

        ParsedCommand cmd = new ParsedCommand("ln", List.of("target.txt", ""));
        cmd.setFlag("s", false);

        CommandResult result = ln.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.ln.emptyLink", result.getMessage());
    }

    @Test
    void execute_hardLink_targetIsDot_error() {
        ParsedCommand cmd = new ParsedCommand("ln", List.of(".", "link"));
        cmd.setFlag("s", false);

        CommandResult result = ln.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.ln.invalidTarget", result.getMessage());
    }

    @Test
    void execute_hardLink_targetIsDotDot_error() {
        ParsedCommand cmd = new ParsedCommand("ln", List.of("..", "link"));
        cmd.setFlag("s", false);

        CommandResult result = ln.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.ln.invalidTarget", result.getMessage());
    }

    @Test
    void execute_hardLink_linkNameIsDot_error() {
        FileINode file = new FileINode();
        fs.getRoot().addEntry("target.txt", file);

        ParsedCommand cmd = new ParsedCommand("ln", List.of("target.txt", "."));
        cmd.setFlag("s", false);

        CommandResult result = ln.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.ln.invalidLinkName", result.getMessage());
    }

    @Test
    void execute_hardLink_withAbsolutePaths_success() {
        FileINode file = new FileINode();
        fs.getRoot().addEntry("target.txt", file);

        ParsedCommand cmd = new ParsedCommand("ln", List.of("/target.txt", "/link.txt"));
        cmd.setFlag("s", false);

        CommandResult result = ln.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertNotNull(fs.getRoot().findEntry("link.txt"));
    }

    @Test
    void execute_symbolicLink_success() {
        // Symlink non richiede che il target esista
        ParsedCommand cmd = new ParsedCommand("ln", List.of("target.txt", "link.txt"));
        cmd.setFlag("s", true);

        CommandResult result = ln.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertEquals("", result.getMessage());

        FileSystemComponent link = fs.getRoot().findEntry("link.txt");
        Assertions.assertNotNull(link);
        Assertions.assertTrue(link instanceof SymlinkINode);
    }

    @Test
    void execute_symbolicLink_targetNotNeeded() {
        // Symlink può puntare a file inesistente
        ParsedCommand cmd = new ParsedCommand("ln", List.of("nonexistent.txt", "link.txt"));
        cmd.setFlag("s", true);

        CommandResult result = ln.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());

        SymlinkINode symlink = (SymlinkINode) fs.getRoot().findEntry("link.txt");
        Assertions.assertNotNull(symlink);
        Assertions.assertEquals("nonexistent.txt", symlink.getTargetPath());
    }

    @Test
    void execute_symbolicLink_storesCorrectTargetPath() {
        ParsedCommand cmd = new ParsedCommand("ln", List.of("/some/path/target", "link"));
        cmd.setFlag("s", true);

        ln.execute(cmd);

        SymlinkINode symlink = (SymlinkINode) fs.getRoot().findEntry("link");
        Assertions.assertEquals("/some/path/target", symlink.getTargetPath());
    }

    @Test
    void execute_symbolicLink_linkAlreadyExists_error() {
        FileINode existing = new FileINode();
        fs.getRoot().addEntry("existing.txt", existing);

        ParsedCommand cmd = new ParsedCommand("ln", List.of("target", "existing.txt"));
        cmd.setFlag("s", true);

        CommandResult result = ln.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.ln.fileExists", result.getMessage());
    }

    @Test
    void execute_symbolicLink_emptyLinkName_error() {
        ParsedCommand cmd = new ParsedCommand("ln", List.of("target", ""));
        cmd.setFlag("s", true);

        CommandResult result = ln.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.ln.emptyLink", result.getMessage());
    }

    @Test
    void execute_symbolicLink_linkNameIsDot_error() {
        ParsedCommand cmd = new ParsedCommand("ln", List.of("target", "."));
        cmd.setFlag("s", true);

        CommandResult result = ln.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.ln.invalidLinkName", result.getMessage());
    }

    @Test
    void execute_symbolicLink_linkNameIsDotDot_error() {
        ParsedCommand cmd = new ParsedCommand("ln", List.of("target", ".."));
        cmd.setFlag("s", true);

        CommandResult result = ln.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.ln.invalidLinkName", result.getMessage());
    }

    @Test
    void execute_symbolicLink_toDirectory_success() {
        // Symlink può puntare a directory
        DirectoryINode dir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("mydir", dir);

        ParsedCommand cmd = new ParsedCommand("ln", List.of("mydir", "linkdir"));
        cmd.setFlag("s", true);

        CommandResult result = ln.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
    }

    @Test
    void integration_hardLink_bothLinksPointToSameContent() {
        // Crea file, fai hard link, verifica che siano lo stesso oggetto
        FileINode original = new FileINode();
        fs.getRoot().addEntry("original.txt", original);

        ParsedCommand cmd = new ParsedCommand("ln", List.of("original.txt", "hardlink.txt"));
        cmd.setFlag("s", false);
        ln.execute(cmd);

        FileSystemComponent link = fs.getRoot().findEntry("hardlink.txt");
        Assertions.assertSame(original, link);

        // Verifica link count
        Assertions.assertEquals(2, original.getLinkCount());
    }

    @Test
    void integration_multipleHardLinks_allPointToSame() {
        FileINode file = new FileINode();
        fs.getRoot().addEntry("file.txt", file);

        ln.execute(new ParsedCommand("ln", List.of("file.txt", "link1.txt")));
        ln.execute(new ParsedCommand("ln", List.of("file.txt", "link2.txt")));
        ln.execute(new ParsedCommand("ln", List.of("file.txt", "link3.txt")));

        Assertions.assertSame(file, fs.getRoot().findEntry("link1.txt"));
        Assertions.assertSame(file, fs.getRoot().findEntry("link2.txt"));
        Assertions.assertSame(file, fs.getRoot().findEntry("link3.txt"));
        Assertions.assertEquals(4, file.getLinkCount()); // original + 3 links
    }

    @Test
    void integration_symbolicLink_canBeFollowed() {
        FileINode target = new FileINode();
        fs.getRoot().addEntry("target.txt", target);

        ParsedCommand cmd = new ParsedCommand("ln", List.of("target.txt", "symlink.txt"));
        cmd.setFlag("s", true);
        ln.execute(cmd);

        SymlinkINode symlink = (SymlinkINode) fs.getRoot().findEntry("symlink.txt");
        Assertions.assertEquals("target.txt", symlink.getTargetPath());

        // NON sono lo stesso oggetto (diverso da hard link)
        Assertions.assertNotSame(target, symlink);
    }
}