package ch.supsi.fscli.model.commands;

import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.data_access.CommandResultStatus;
import ch.supsi.fscli.model.command_management.ParsedCommand;
import ch.supsi.fscli.model.inode.DirectoryINode;
import ch.supsi.fscli.model.inode.FileINode;
import ch.supsi.fscli.model.inode.FileSystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

public class RmdirCommandTest {

    private RmDirCommand rmdir;
    private FileSystem fs;

    @BeforeEach
    void setup() {
        FileSystem.resetInstance();
        this.fs = FileSystem.getInstance();
        this.rmdir = new RmDirCommand();
    }

    @AfterEach
    void cleanup() {
        FileSystem.resetInstance();
    }


    @Test
    void parse_withOneArgument_success() throws ParseException {
        ParsedCommand result = rmdir.parse(List.of("emptydir"));

        Assertions.assertEquals("rmdir", result.getCommand());
        Assertions.assertEquals(1, result.getArguments().size());
        Assertions.assertEquals("emptydir", result.getArguments().get(0));
    }

    @Test
    void parse_withMultipleArguments_success() throws ParseException {
        ParsedCommand result = rmdir.parse(List.of("dir1", "dir2", "dir3"));

        Assertions.assertEquals(3, result.getArguments().size());
        Assertions.assertTrue(result.getArguments().contains("dir1"));
        Assertions.assertTrue(result.getArguments().contains("dir2"));
        Assertions.assertTrue(result.getArguments().contains("dir3"));
    }

    @Test
    void parse_withAbsolutePath_success() throws ParseException {
        ParsedCommand result = rmdir.parse(List.of("/home/user/emptydir"));

        Assertions.assertEquals(1, result.getArguments().size());
        Assertions.assertEquals("/home/user/emptydir", result.getArguments().get(0));
    }

    @Test
    void parse_noArguments_throwsParseException() {
        ParseException exception = Assertions.assertThrows(
                ParseException.class,
                () -> rmdir.parse(Collections.emptyList())
        );

        Assertions.assertEquals("label.rmdir.usage", exception.getMessage());
    }

    @Test
    void execute_removesEmptyDirectory_success() {
        DirectoryINode emptyDir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("emptydir", emptyDir);

        ParsedCommand cmd = new ParsedCommand("rmdir", List.of("emptydir"));

        CommandResult result = rmdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertEquals("", result.getMessage());

        // Verifica rimossa
        Assertions.assertNull(fs.getRoot().findEntry("emptydir"));
    }

    @Test
    void execute_removesMultipleEmptyDirectories_success() {
        DirectoryINode dir1 = new DirectoryINode(fs.getRoot());
        DirectoryINode dir2 = new DirectoryINode(fs.getRoot());
        DirectoryINode dir3 = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("dir1", dir1);
        fs.getRoot().addEntry("dir2", dir2);
        fs.getRoot().addEntry("dir3", dir3);

        ParsedCommand cmd = new ParsedCommand("rmdir", List.of("dir1", "dir2", "dir3"));

        CommandResult result = rmdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());

        // Tutte rimosse
        Assertions.assertNull(fs.getRoot().findEntry("dir1"));
        Assertions.assertNull(fs.getRoot().findEntry("dir2"));
        Assertions.assertNull(fs.getRoot().findEntry("dir3"));
    }

    @Test
    void execute_removesDirectoryWithAbsolutePath_success() {
        DirectoryINode parent = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("parent", parent);

        DirectoryINode child = new DirectoryINode(parent);
        parent.addEntry("child", child);

        ParsedCommand cmd = new ParsedCommand("rmdir", List.of("/parent/child"));

        CommandResult result = rmdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertNull(parent.findEntry("child"));
    }

    @Test
    void execute_removesDirectoryWithRelativePath_success() {
        DirectoryINode parent = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("parent", parent);
        fs.setCurrentWorkingDirectory(parent);

        DirectoryINode child = new DirectoryINode(parent);
        parent.addEntry("child", child);

        ParsedCommand cmd = new ParsedCommand("rmdir", List.of("child"));

        CommandResult result = rmdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertNull(parent.findEntry("child"));
    }

    @Test
    void execute_emptyPath_returnsError() {
        ParsedCommand cmd = new ParsedCommand("rmdir", List.of(""));

        CommandResult result = rmdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.rmdir.emptyPath", result.getMessage());
    }

    @Test
    void execute_whitespacePath_returnsError() {
        ParsedCommand cmd = new ParsedCommand("rmdir", List.of("   "));

        CommandResult result = rmdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.rmdir.emptyPath", result.getMessage());
    }

    @Test
    void execute_directoryDoesNotExist_returnsError() {
        ParsedCommand cmd = new ParsedCommand("rmdir", List.of("nonexistent"));

        CommandResult result = rmdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.rmdir.noSuchFile", result.getMessage());
    }

    @Test
    void execute_pathIsRoot_returnsError() {
        ParsedCommand cmd = new ParsedCommand("rmdir", List.of("/"));

        CommandResult result = rmdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.rmdir.cannotRemoveRoot", result.getMessage());
    }

    @Test
    void execute_pathIsDot_returnsError() {
        ParsedCommand cmd = new ParsedCommand("rmdir", List.of("."));

        CommandResult result = rmdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.rmdir.invalidName", result.getMessage());
    }

    @Test
    void execute_pathIsDotDot_returnsError() {
        ParsedCommand cmd = new ParsedCommand("rmdir", List.of(".."));

        CommandResult result = rmdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.rmdir.invalidName", result.getMessage());
    }

    @Test
    void execute_targetIsFile_returnsError() {
        FileINode file = new FileINode();
        fs.getRoot().addEntry("file.txt", file);

        ParsedCommand cmd = new ParsedCommand("rmdir", List.of("file.txt"));

        CommandResult result = rmdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.rmdir.notDirectory", result.getMessage());

        // File deve ancora esistere
        Assertions.assertNotNull(fs.getRoot().findEntry("file.txt"));
    }

    @Test
    void execute_directoryNotEmpty_returnsError() {
        DirectoryINode dir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("dir", dir);

        // Aggiungi file dentro
        FileINode file = new FileINode();
        dir.addEntry("file.txt", file);

        ParsedCommand cmd = new ParsedCommand("rmdir", List.of("dir"));

        CommandResult result = rmdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.rmdir.notEmpty", result.getMessage());

        // Directory deve ancora esistere
        Assertions.assertNotNull(fs.getRoot().findEntry("dir"));
    }

    @Test
    void execute_directoryWithSubdirectory_returnsError() {
        DirectoryINode parent = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("parent", parent);

        DirectoryINode child = new DirectoryINode(parent);
        parent.addEntry("child", child);

        ParsedCommand cmd = new ParsedCommand("rmdir", List.of("parent"));

        CommandResult result = rmdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.rmdir.notEmpty", result.getMessage());
    }

    @Test
    void execute_currentWorkingDirectory_returnsError() {
        DirectoryINode dir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("mydir", dir);

        // Spostati nella directory
        fs.setCurrentWorkingDirectory(dir);

        // Prova a rimuoverla
        ParsedCommand cmd = new ParsedCommand("rmdir", List.of("/mydir"));

        CommandResult result = rmdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.rmdir.currentDir", result.getMessage());

        // Directory deve ancora esistere
        Assertions.assertNotNull(fs.getRoot().findEntry("mydir"));
    }

    @Test
    void execute_currentWorkingDirectoryRelative_returnsError() {
        DirectoryINode dir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("mydir", dir);
        fs.setCurrentWorkingDirectory(dir);

        // Prova a rimuovere . (current)
        ParsedCommand cmd = new ParsedCommand("rmdir", List.of("."));

        CommandResult result = rmdir.execute(cmd);

        // Bloccato da validazione nome, non da check currentDir
        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
    }

    @Test
    void execute_parentDoesNotExist_returnsError() {
        ParsedCommand cmd = new ParsedCommand("rmdir", List.of("nonexistent/child"));

        CommandResult result = rmdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertTrue(result.getMessage().startsWith("label."));
    }

    @Test
    void integration_mkdirThenRmdir() {
        // Simula: mkdir testdir && rmdir testdir
        DirectoryINode dir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("testdir", dir);

        Assertions.assertNotNull(fs.getRoot().findEntry("testdir"));

        rmdir.execute(new ParsedCommand("rmdir", List.of("testdir")));

        Assertions.assertNull(fs.getRoot().findEntry("testdir"));
    }

    @Test
    void integration_removeNestedEmptyDirectories() {
        // Crea /a/b/c (tutte vuote)
        DirectoryINode a = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("a", a);

        DirectoryINode b = new DirectoryINode(a);
        a.addEntry("b", b);

        DirectoryINode c = new DirectoryINode(b);
        b.addEntry("c", c);

        // Rimuovi dal più interno al più esterno
        rmdir.execute(new ParsedCommand("rmdir", List.of("a/b/c")));
        Assertions.assertNull(b.findEntry("c"));

        rmdir.execute(new ParsedCommand("rmdir", List.of("a/b")));
        Assertions.assertNull(a.findEntry("b"));

        rmdir.execute(new ParsedCommand("rmdir", List.of("a")));
        Assertions.assertNull(fs.getRoot().findEntry("a"));
    }

    @Test
    void integration_cannotRemoveNonEmptyThenRemoveAfterCleanup() {
        DirectoryINode dir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("dir", dir);

        FileINode file = new FileINode();
        dir.addEntry("file.txt", file);

        // Primo tentativo: fallisce (non vuota)
        CommandResult result1 = rmdir.execute(new ParsedCommand("rmdir", List.of("dir")));
        Assertions.assertEquals(CommandResultStatus.ERROR, result1.getStatus());

        // Rimuovi il file
        dir.removeEntry("file.txt");

        // Secondo tentativo: successo (ora vuota)
        CommandResult result2 = rmdir.execute(new ParsedCommand("rmdir", List.of("dir")));
        Assertions.assertEquals(CommandResultStatus.SUCCESS, result2.getStatus());
        Assertions.assertNull(fs.getRoot().findEntry("dir"));
    }

    @Test
    void integration_removeMultipleSomeFailSomeSucceed() {
        DirectoryINode empty = new DirectoryINode(fs.getRoot());
        DirectoryINode notEmpty = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("empty", empty);
        fs.getRoot().addEntry("notempty", notEmpty);

        FileINode file = new FileINode();
        notEmpty.addEntry("file.txt", file);

        ParsedCommand cmd = new ParsedCommand("rmdir", List.of("empty", "notempty"));

        CommandResult result = rmdir.execute(cmd);

        // Prima rimuove empty, poi fallisce su notempty
        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.rmdir.notEmpty", result.getMessage());

        // empty rimossa, notempty ancora lì
        Assertions.assertNull(fs.getRoot().findEntry("empty"));
        Assertions.assertNotNull(fs.getRoot().findEntry("notempty"));
    }

    @Test
    void integration_canRemoveAfterLeavingDirectory() {
        DirectoryINode dir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("leaveme", dir);

        // Vai dentro
        fs.setCurrentWorkingDirectory(dir);

        // Non può rimuovere (sei dentro)
        CommandResult result1 = rmdir.execute(new ParsedCommand("rmdir", List.of("/leaveme")));
        Assertions.assertEquals(CommandResultStatus.ERROR, result1.getStatus());

        // Esci
        fs.setCurrentWorkingDirectory(fs.getRoot());

        // Ora puoi rimuovere
        CommandResult result2 = rmdir.execute(new ParsedCommand("rmdir", List.of("leaveme")));
        Assertions.assertEquals(CommandResultStatus.SUCCESS, result2.getStatus());
        Assertions.assertNull(fs.getRoot().findEntry("leaveme"));
    }
}