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

public class MkdirCommandTest {

    private MkdirCommand mkdir;
    private FileSystem fs;

    @BeforeEach
    void setup() {
        FileSystem.resetInstance();
        this.fs = FileSystem.getInstance();
        this.mkdir = new MkdirCommand();
    }

    @AfterEach
    void cleanup() {
        FileSystem.resetInstance();
    }

    @Test
    void parse_withOneArgument_success() throws ParseException {
        ParsedCommand result = mkdir.parse(List.of("newdir"));

        Assertions.assertEquals("mkdir", result.getCommand());
        Assertions.assertEquals(1, result.getArguments().size());
        Assertions.assertEquals("newdir", result.getArguments().get(0));
    }

    @Test
    void parse_withMultipleArguments_success() throws ParseException {
        ParsedCommand result = mkdir.parse(List.of("dir1", "dir2", "dir3"));

        Assertions.assertEquals(3, result.getArguments().size());
        Assertions.assertTrue(result.getArguments().contains("dir1"));
        Assertions.assertTrue(result.getArguments().contains("dir2"));
        Assertions.assertTrue(result.getArguments().contains("dir3"));
    }

    @Test
    void parse_withAbsolutePath_success() throws ParseException {
        ParsedCommand result = mkdir.parse(List.of("/home/user/newdir"));

        Assertions.assertEquals(1, result.getArguments().size());
        Assertions.assertEquals("/home/user/newdir", result.getArguments().get(0));
    }

    @Test
    void parse_noArguments_throwsParseException() {
        ParseException exception = Assertions.assertThrows(
                ParseException.class,
                () -> mkdir.parse(Collections.emptyList())
        );

        Assertions.assertEquals("label.mkdir.usage", exception.getMessage());
    }


    @Test
    void execute_createsSingleDirectory_success() {
        ParsedCommand cmd = new ParsedCommand("mkdir", List.of("newdir"));

        CommandResult result = mkdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertEquals("", result.getMessage());

        FileSystemComponent created = fs.getRoot().findEntry("newdir");
        Assertions.assertNotNull(created);
        Assertions.assertInstanceOf(DirectoryINode.class, created);
    }

    @Test
    void execute_createsMultipleDirectories_success() {
        ParsedCommand cmd = new ParsedCommand("mkdir", List.of("dir1", "dir2", "dir3"));

        CommandResult result = mkdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());

        Assertions.assertNotNull(fs.getRoot().findEntry("dir1"));
        Assertions.assertNotNull(fs.getRoot().findEntry("dir2"));
        Assertions.assertNotNull(fs.getRoot().findEntry("dir3"));
    }

    @Test
    void execute_createsDirectoryWithAbsolutePath_success() {
        DirectoryINode parent = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("parent", parent);

        ParsedCommand cmd = new ParsedCommand("mkdir", List.of("/parent/child"));

        CommandResult result = mkdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertNotNull(parent.findEntry("child"));
    }

    @Test
    void execute_createsDirectoryWithRelativePath_success() {
        // Crea struttura /parent
        DirectoryINode parent = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("parent", parent);
        fs.setCurrentWorkingDirectory(parent);

        ParsedCommand cmd = new ParsedCommand("mkdir", List.of("child"));

        CommandResult result = mkdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertNotNull(parent.findEntry("child"));
    }

    @Test
    void execute_directoryHasCorrectParent() {
        ParsedCommand cmd = new ParsedCommand("mkdir", List.of("newdir"));

        mkdir.execute(cmd);

        DirectoryINode created = (DirectoryINode) fs.getRoot().findEntry("newdir");
        Assertions.assertEquals(fs.getRoot(), created.getParentDirectory());
    }

    @Test
    void execute_directoryContainsDotAndDotDot() {
        ParsedCommand cmd = new ParsedCommand("mkdir", List.of("newdir"));

        mkdir.execute(cmd);

        DirectoryINode created = (DirectoryINode) fs.getRoot().findEntry("newdir");

        Assertions.assertNotNull(created.findEntry("."));
        Assertions.assertNotNull(created.findEntry(".."));
    }

    @Test
    void execute_directoryAlreadyExists_returnsError() {
        DirectoryINode existing = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("existing", existing);

        ParsedCommand cmd = new ParsedCommand("mkdir", List.of("existing"));

        CommandResult result = mkdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.mkdir.fileExists", result.getMessage());
    }

    @Test
    void execute_fileWithSameNameExists_returnsError() {
        FileINode file = new FileINode();
        fs.getRoot().addEntry("file.txt", file);

        ParsedCommand cmd = new ParsedCommand("mkdir", List.of("file.txt"));

        CommandResult result = mkdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.mkdir.fileExists", result.getMessage());
    }

    @Test
    void execute_pathEndingWithSlash_createsDirectory() {
        ParsedCommand cmd = new ParsedCommand("mkdir", List.of("newdir/"));

        CommandResult result = mkdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertNotNull(fs.getRoot().findEntry("newdir"));
    }

    @Test
    void execute_pathIsRoot_returnsError() {
        ParsedCommand cmd = new ParsedCommand("mkdir", List.of("/"));

        CommandResult result = mkdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.mkdir.cannotCreate", result.getMessage());
    }

    @Test
    void execute_pathIsDot_returnsError() {
        ParsedCommand cmd = new ParsedCommand("mkdir", List.of("."));

        CommandResult result = mkdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.mkdir.invalidName", result.getMessage());
    }

    @Test
    void execute_pathIsDotDot_returnsError() {
        ParsedCommand cmd = new ParsedCommand("mkdir", List.of(".."));

        CommandResult result = mkdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.mkdir.invalidName", result.getMessage());
    }

    @Test
    void execute_pathContainsDotInName_returnsError() {
        ParsedCommand cmd = new ParsedCommand("mkdir", List.of("some/path/."));

        CommandResult result = mkdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.mkdir.pathError", result.getMessage());
    }

    @Test
    void execute_parentDoesNotExist_returnsError() {
        ParsedCommand cmd = new ParsedCommand("mkdir", List.of("nonexistent/child"));

        CommandResult result = mkdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertTrue(result.getMessage().startsWith("label."));
    }

    @Test
    void execute_nestedPathWithoutParent_returnsError() {
        ParsedCommand cmd = new ParsedCommand("mkdir", List.of("/a/b/c"));

        CommandResult result = mkdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
    }

    @Test
    void execute_someSucceedSomeFail_returnsFirstError() {
        DirectoryINode existing = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("existing", existing);

        ParsedCommand cmd = new ParsedCommand("mkdir", List.of("new1", "existing", "new2"));

        CommandResult result = mkdir.execute(cmd);


        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
    }

    @Test
    void execute_allArgumentsFail_returnsError() {
        DirectoryINode existing1 = new DirectoryINode(fs.getRoot());
        DirectoryINode existing2 = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("existing1", existing1);
        fs.getRoot().addEntry("existing2", existing2);

        ParsedCommand cmd = new ParsedCommand("mkdir", List.of("existing1", "existing2"));

        CommandResult result = mkdir.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
    }

    @Test
    void integration_createNestedDirectories_manually() {
        // Crea /parent
        ParsedCommand cmd1 = new ParsedCommand("mkdir", List.of("parent"));
        mkdir.execute(cmd1);

        DirectoryINode parent = (DirectoryINode) fs.getRoot().findEntry("parent");
        Assertions.assertNotNull(parent);

        // Crea /parent/child
        ParsedCommand cmd2 = new ParsedCommand("mkdir", List.of("parent/child"));
        CommandResult result = mkdir.execute(cmd2);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertNotNull(parent.findEntry("child"));
    }

    @Test
    void integration_createMultipleSiblings_success() {
        ParsedCommand cmd = new ParsedCommand("mkdir", List.of("dir1", "dir2", "dir3"));
        mkdir.execute(cmd);

        Assertions.assertNotNull(fs.getRoot().findEntry("dir1"));
        Assertions.assertNotNull(fs.getRoot().findEntry("dir2"));
        Assertions.assertNotNull(fs.getRoot().findEntry("dir3"));

        Assertions.assertInstanceOf(DirectoryINode.class, fs.getRoot().findEntry("dir1"));
        Assertions.assertInstanceOf(DirectoryINode.class, fs.getRoot().findEntry("dir2"));
        Assertions.assertInstanceOf(DirectoryINode.class, fs.getRoot().findEntry("dir3"));
    }

    @Test
    void integration_directoryCanBeUsedAfterCreation() {
        ParsedCommand cmd = new ParsedCommand("mkdir", List.of("newdir"));
        mkdir.execute(cmd);

        DirectoryINode created = (DirectoryINode) fs.getRoot().findEntry("newdir");

        FileINode file = new FileINode();
        boolean added = created.addEntry("file.txt", file);

        Assertions.assertTrue(added);
        Assertions.assertNotNull(created.findEntry("file.txt"));
    }
}