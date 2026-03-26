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

public class CdCommandTest {

    private CdCommand cd;
    private FileSystem fs;

    @BeforeEach
    void setup() {
        FileSystem.resetInstance();
        this.fs = FileSystem.getInstance();
        this.cd = new CdCommand();
    }

    @AfterEach
    void cleanup() {
        FileSystem.resetInstance();
    }

    @Test
    public void testParse_WithNoArguments_ReturnsEmptyList() throws ParseException {
        List<String> args = Collections.emptyList();

        ParsedCommand result = cd.parse(args);

        Assertions.assertEquals("cd", result.getCommand());
        Assertions.assertTrue(result.getArguments().isEmpty());
    }

    @Test
    public void testParse_WithOneArgument_ReturnsArgumentList() throws ParseException {
        List<String> args = List.of("/some/path");

        ParsedCommand result = cd.parse(args);

        Assertions.assertEquals("cd", result.getCommand());
        Assertions.assertEquals(1, result.getArguments().size());
        Assertions.assertEquals("/some/path", result.getArguments().get(0));
    }

    @Test
    public void testParse_WithTwoArguments_ThrowsParseException() {
        List<String> args = List.of("arg1", "arg2");

        ParseException exception = Assertions.assertThrows(
                ParseException.class,
                () -> cd.parse(args)
        );

        Assertions.assertEquals("label.cd.error", exception.getMessage());
    }

    @Test
    public void testCd_ToExistingDirectory_Success() {
        DirectoryINode testDir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("test_directory", testDir);
        ParsedCommand cmd = new ParsedCommand("cd", List.of("test_directory"));

        CommandResult result = cd.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertEquals(testDir, fs.getCurrentWorkingDirectory());
    }

    @Test
    public void testCd_ToNonExistentDirectory_ReturnsError() {
        ParsedCommand cmd = new ParsedCommand("cd", List.of("non_existent"));

        CommandResult result = cd.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.cd.notfound", result.getMessage());
        Assertions.assertEquals(fs.getRoot(), fs.getCurrentWorkingDirectory());
    }

    @Test
    public void test_WithDotDot_NavigatesToParent() {
        DirectoryINode child = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("child", child);
        fs.setCurrentWorkingDirectory(child);
        ParsedCommand cmd = new ParsedCommand("cd", List.of(".."));

        cd.execute(cmd);

        Assertions.assertEquals(fs.getRoot(), fs.getCurrentWorkingDirectory());
    }

    @Test
    public void test_WithDot_StaysInCurrentDirectory() {
        DirectoryINode testDir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("test_dir", testDir);
        fs.setCurrentWorkingDirectory(testDir);
        ParsedCommand cmd = new ParsedCommand("cd", List.of("."));

        cd.execute(cmd);

        Assertions.assertEquals(testDir, fs.getCurrentWorkingDirectory());
    }

    @Test
    public void test_WithNoArguments_GoesToRoot() {
        DirectoryINode testDir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("test_dir", testDir);
        fs.setCurrentWorkingDirectory(testDir);
        ParsedCommand cmd = new ParsedCommand("cd", Collections.emptyList());

        cd.execute(cmd);

        Assertions.assertEquals(fs.getRoot(), fs.getCurrentWorkingDirectory());
    }

    @Test
    public void test_ToFile_ReturnsError() {
        FileINode file = new FileINode();
        fs.getRoot().addEntry("test.txt", file);
        ParsedCommand cmd = new ParsedCommand("cd", List.of("test.txt"));

        CommandResult result = cd.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.cd.notADirectory", result.getMessage());
        Assertions.assertEquals(fs.getRoot(), fs.getCurrentWorkingDirectory());
    }

    @Test
    public void test_WithAbsolutePath_Success() {
        DirectoryINode level1 = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("level1", level1);
        DirectoryINode level2 = new DirectoryINode(level1);
        level1.addEntry("level2", level2);
        ParsedCommand cmd = new ParsedCommand("cd", List.of("/level1/level2"));

        CommandResult result = cd.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertEquals(level2, fs.getCurrentWorkingDirectory());
    }

    @Test
    public void test_WithRelativePath_Success() {
        DirectoryINode dir1 = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("dir1", dir1);
        DirectoryINode dir2 = new DirectoryINode(dir1);
        dir1.addEntry("dir2", dir2);
        fs.setCurrentWorkingDirectory(dir1);
        ParsedCommand cmd = new ParsedCommand("cd", List.of("dir2"));

        CommandResult result = cd.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertEquals(dir2, fs.getCurrentWorkingDirectory());
    }

    @Test
    public void test_ToRootWithSlash_Success() {
        DirectoryINode testDir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("test", testDir);
        fs.setCurrentWorkingDirectory(testDir);
        ParsedCommand cmd = new ParsedCommand("cd", List.of("/"));

        CommandResult result = cd.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertEquals(fs.getRoot(), fs.getCurrentWorkingDirectory());
    }

    @Test
    public void test_WithTrailingSlash_Success() {
        DirectoryINode testDir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("test_dir", testDir);
        ParsedCommand cmd = new ParsedCommand("cd", List.of("test_dir/"));

        CommandResult result = cd.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertEquals(testDir, fs.getCurrentWorkingDirectory());
    }

    @Test
    public void test_PathWithIntermediateNonExistent_ReturnsError() {
        ParsedCommand cmd = new ParsedCommand("cd", List.of("non_existent/child"));

        CommandResult result = cd.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.cd.notfound", result.getMessage());
        Assertions.assertEquals(fs.getRoot(), fs.getCurrentWorkingDirectory());
    }


    @Test
    public void test_WithPathEndingInDot_Success() {
        DirectoryINode testDir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("mydir", testDir);
        ParsedCommand cmd = new ParsedCommand("cd", List.of("mydir/."));

        CommandResult result = cd.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertEquals(testDir, fs.getCurrentWorkingDirectory());
    }

    @Test
    public void test_ComplexPathWithDots_Success() {
        DirectoryINode level1 = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("level1", level1);
        DirectoryINode level2 = new DirectoryINode(level1);
        level1.addEntry("level2", level2);

        fs.setCurrentWorkingDirectory(level1);
        ParsedCommand cmd = new ParsedCommand("cd", List.of("level2/../level2"));

        CommandResult result = cd.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertEquals(level2, fs.getCurrentWorkingDirectory());
    }

    @Test
    public void testGetFileSystem_ReturnsInstance() {
        FileSystem result = cd.getFileSystem();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(fs, result);
    }
}