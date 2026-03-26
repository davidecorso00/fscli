package ch.supsi.fscli.model.commands;

import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.data_access.CommandResultStatus;
import ch.supsi.fscli.model.command_management.ParsedCommand;
import ch.supsi.fscli.model.inode.DirectoryINode;
import ch.supsi.fscli.model.inode.FileINode;
import ch.supsi.fscli.model.inode.FileSystem;
import ch.supsi.fscli.model.inode.FileSystemComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

public class TouchCommandTest {

    private TouchCommand touch;
    private FileSystem fs;

    @BeforeEach
    void setup() {
        FileSystem.resetInstance();
        this.fs = FileSystem.getInstance();
        this.touch = new TouchCommand();
    }

    @AfterEach
    void cleanup() {
        FileSystem.resetInstance();
    }

    @Test
    void parse_withOneArgument_success() throws ParseException {
        ParsedCommand result = touch.parse(List.of("file.txt"));

        Assertions.assertEquals("touch", result.getCommand());
        Assertions.assertEquals(1, result.getArguments().size());
        Assertions.assertEquals("file.txt", result.getArguments().get(0));
    }

    @Test
    void parse_withMultipleArguments_success() throws ParseException {
        ParsedCommand result = touch.parse(List.of("file1.txt", "file2.txt", "file3.txt"));

        Assertions.assertEquals(3, result.getArguments().size());
        Assertions.assertTrue(result.getArguments().contains("file1.txt"));
        Assertions.assertTrue(result.getArguments().contains("file2.txt"));
        Assertions.assertTrue(result.getArguments().contains("file3.txt"));
    }

    @Test
    void parse_withAbsolutePath_success() throws ParseException {
        ParsedCommand result = touch.parse(List.of("/home/user/file.txt"));

        Assertions.assertEquals(1, result.getArguments().size());
        Assertions.assertEquals("/home/user/file.txt", result.getArguments().get(0));
    }

    @Test
    void parse_noArguments_throwsParseException() {
        ParseException exception = Assertions.assertThrows(
                ParseException.class,
                () -> touch.parse(Collections.emptyList())
        );

        Assertions.assertEquals("label.touch.usage", exception.getMessage());
    }

    @Test
    void execute_createsSingleFile_success() {
        ParsedCommand cmd = new ParsedCommand("touch", List.of("newfile.txt"));

        CommandResult result = touch.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertEquals("", result.getMessage());

        FileSystemComponent created = fs.getRoot().findEntry("newfile.txt");
        Assertions.assertNotNull(created);
        Assertions.assertInstanceOf(FileINode.class, created);
    }

    @Test
    void execute_createsMultipleFiles_success() {
        ParsedCommand cmd = new ParsedCommand("touch", List.of("file1.txt", "file2.txt", "file3.txt"));

        CommandResult result = touch.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());

        // Verifica tutti i file
        Assertions.assertNotNull(fs.getRoot().findEntry("file1.txt"));
        Assertions.assertNotNull(fs.getRoot().findEntry("file2.txt"));
        Assertions.assertNotNull(fs.getRoot().findEntry("file3.txt"));
    }

    @Test
    void execute_createsFileWithAbsolutePath_success() {
        // Crea parent directory
        DirectoryINode parent = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("parent", parent);

        ParsedCommand cmd = new ParsedCommand("touch", List.of("/parent/child.txt"));

        CommandResult result = touch.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertNotNull(parent.findEntry("child.txt"));
    }

    @Test
    void execute_createsFileWithRelativePath_success() {
        DirectoryINode dir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("dir", dir);
        fs.setCurrentWorkingDirectory(dir);

        ParsedCommand cmd = new ParsedCommand("touch", List.of("file.txt"));

        CommandResult result = touch.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertNotNull(dir.findEntry("file.txt"));
    }

    @Test
    void execute_fileAlreadyExists_success() {
        // Crea file esistente
        FileINode existing = new FileINode();
        fs.getRoot().addEntry("existing.txt", existing);

        ParsedCommand cmd = new ParsedCommand("touch", List.of("existing.txt"));

        CommandResult result = touch.execute(cmd);

        // Unix touch: se esiste, aggiorna timestamp (qui skip)
        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());

        // Verifica che il file esista ancora
        FileSystemComponent stillThere = fs.getRoot().findEntry("existing.txt");
        Assertions.assertNotNull(stillThere);
        Assertions.assertSame(existing, stillThere);
    }

    @Test
    void execute_createsFileWithExtension_success() {
        ParsedCommand cmd = new ParsedCommand("touch", List.of("document.pdf"));

        touch.execute(cmd);

        FileSystemComponent created = fs.getRoot().findEntry("document.pdf");
        Assertions.assertNotNull(created);
        Assertions.assertInstanceOf(FileINode.class, created);
    }

    @Test
    void execute_createsFileWithoutExtension_success() {
        ParsedCommand cmd = new ParsedCommand("touch", List.of("README"));

        touch.execute(cmd);

        FileSystemComponent created = fs.getRoot().findEntry("README");
        Assertions.assertNotNull(created);
        Assertions.assertInstanceOf(FileINode.class, created);
    }

    @Test
    void execute_emptyPath_returnsError() {
        ParsedCommand cmd = new ParsedCommand("touch", List.of(""));

        CommandResult result = touch.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.touch.emptyPath", result.getMessage());
    }

    @Test
    void execute_whitespacePath_returnsError() {
        ParsedCommand cmd = new ParsedCommand("touch", List.of("   "));

        CommandResult result = touch.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.touch.emptyPath", result.getMessage());
    }

    @Test
    void execute_pathEndingWithSlash_returnsError() {
        ParsedCommand cmd = new ParsedCommand("touch", List.of("file.txt/"));

        CommandResult result = touch.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.touch.invalidTrailingSlash", result.getMessage());
    }

    @Test
    void execute_pathIsDot_returnsError() {
        ParsedCommand cmd = new ParsedCommand("touch", List.of("."));

        CommandResult result = touch.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.touch.invalidName", result.getMessage());
    }

    @Test
    void execute_pathIsDotDot_returnsError() {
        ParsedCommand cmd = new ParsedCommand("touch", List.of(".."));

        CommandResult result = touch.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.touch.invalidName", result.getMessage());
    }

    @Test
    void execute_parentDoesNotExist_returnsError() {
        ParsedCommand cmd = new ParsedCommand("touch", List.of("nonexistent/file.txt"));

        CommandResult result = touch.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertTrue(result.getMessage().startsWith("label."));
    }

    @Test
    void execute_nestedPathWithoutParent_returnsError() {
        ParsedCommand cmd = new ParsedCommand("touch", List.of("/a/b/c/file.txt"));

        CommandResult result = touch.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
    }

    @Test
    void execute_directoryWithSameNameExists_cannotCreate() {
        // Crea directory con lo stesso nome
        DirectoryINode dir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("dirname", dir);

        ParsedCommand cmd = new ParsedCommand("touch", List.of("dirname"));

        CommandResult result = touch.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
    }

    // ========== INTEGRATION TESTS ==========

    @Test
    void integration_createMultipleFilesInDifferentDirectories() {
        DirectoryINode dir1 = new DirectoryINode(fs.getRoot());
        DirectoryINode dir2 = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("dir1", dir1);
        fs.getRoot().addEntry("dir2", dir2);

        ParsedCommand cmd = new ParsedCommand("touch",
                List.of("file1.txt", "dir1/file2.txt", "dir2/file3.txt"));

        CommandResult result = touch.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());

        Assertions.assertNotNull(fs.getRoot().findEntry("file1.txt"));
        Assertions.assertNotNull(dir1.findEntry("file2.txt"));
        Assertions.assertNotNull(dir2.findEntry("file3.txt"));
    }

    @Test
    void integration_touchExistingAndNewFiles() {
        FileINode existing = new FileINode();
        fs.getRoot().addEntry("existing.txt", existing);

        ParsedCommand cmd = new ParsedCommand("touch",
                List.of("existing.txt", "new.txt"));

        CommandResult result = touch.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());

        Assertions.assertSame(existing, fs.getRoot().findEntry("existing.txt"));

        Assertions.assertNotNull(fs.getRoot().findEntry("new.txt"));
    }

    @Test
    void integration_createFilesThenListThem() {
        // Crea file con touch
        touch.execute(new ParsedCommand("touch", List.of("file1.txt", "file2.txt")));

        List<String> entries = fs.getRoot().getAllEntryNames()
                .stream()
                .filter(name -> !name.equals(".") && !name.equals(".."))
                .sorted()
                .toList();

        Assertions.assertTrue(entries.contains("file1.txt"));
        Assertions.assertTrue(entries.contains("file2.txt"));
    }

    @Test
    void integration_filesAreFileINodeType() {
        touch.execute(new ParsedCommand("touch", List.of("test.txt")));

        FileSystemComponent created = fs.getRoot().findEntry("test.txt");

        // Verifica tipo corretto
        Assertions.assertInstanceOf(FileINode.class, created);
        Assertions.assertFalse(created.isDirectory());
    }
}