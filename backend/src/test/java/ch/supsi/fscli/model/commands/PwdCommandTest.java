package ch.supsi.fscli.model.commands;

import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.data_access.CommandResultStatus;
import ch.supsi.fscli.model.command_management.ParsedCommand;
import ch.supsi.fscli.model.inode.DirectoryINode;
import ch.supsi.fscli.model.inode.FileSystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

public class PwdCommandTest {

    private PwdCommand pwd;
    private FileSystem fs;

    @BeforeEach
    void setup() {
        FileSystem.resetInstance();
        this.fs = FileSystem.getInstance();
        this.pwd = new PwdCommand();
    }

    @AfterEach
    void cleanup() {
        FileSystem.resetInstance();
    }

    @Test
    void parse_withNoArguments_success() throws ParseException {
        ParsedCommand result = pwd.parse(Collections.emptyList());

        Assertions.assertEquals("pwd", result.getCommand());
        Assertions.assertTrue(result.getArguments().isEmpty());
    }

    @Test
    void parse_withOneArgument_throwsParseException() {
        ParseException exception = Assertions.assertThrows(
                ParseException.class,
                () -> pwd.parse(List.of("unexpected"))
        );

        Assertions.assertEquals("label.pwd.usage", exception.getMessage());
        Assertions.assertEquals(0, exception.getErrorOffset());
    }

    @Test
    void parse_withMultipleArguments_throwsParseException() {
        ParseException exception = Assertions.assertThrows(
                ParseException.class,
                () -> pwd.parse(List.of("arg1", "arg2"))
        );

        Assertions.assertEquals("label.pwd.usage", exception.getMessage());
    }

    @Test
    void execute_atRoot_returnsSlash() {
        ParsedCommand cmd = new ParsedCommand("pwd", Collections.emptyList());

        CommandResult result = pwd.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertEquals("/", result.getMessage());
    }

    @Test
    void execute_inSubdirectory_returnsAbsolutePath() {
        DirectoryINode testDir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("testdir", testDir);
        fs.setCurrentWorkingDirectory(testDir);

        ParsedCommand cmd = new ParsedCommand("pwd", Collections.emptyList());

        CommandResult result = pwd.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertEquals("/testdir", result.getMessage());
    }

    @Test
    void execute_inNestedDirectory_returnsFullPath() {
        DirectoryINode parent = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("parent", parent);

        DirectoryINode child = new DirectoryINode(parent);
        parent.addEntry("child", child);

        fs.setCurrentWorkingDirectory(child);

        ParsedCommand cmd = new ParsedCommand("pwd", Collections.emptyList());

        CommandResult result = pwd.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertEquals("/parent/child", result.getMessage());
    }

    @Test
    void execute_deeplyNested_returnsCompletePath() {
        DirectoryINode a = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("a", a);

        DirectoryINode b = new DirectoryINode(a);
        a.addEntry("b", b);

        DirectoryINode c = new DirectoryINode(b);
        b.addEntry("c", c);

        DirectoryINode d = new DirectoryINode(c);
        c.addEntry("d", d);

        fs.setCurrentWorkingDirectory(d);

        ParsedCommand cmd = new ParsedCommand("pwd", Collections.emptyList());

        CommandResult result = pwd.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertEquals("/a/b/c/d", result.getMessage());
    }

    @Test
    void execute_afterMultipleCdOperations_returnsCorrectPath() {
        DirectoryINode dir1 = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("dir1", dir1);

        DirectoryINode dir2 = new DirectoryINode(dir1);
        dir1.addEntry("dir2", dir2);

        fs.setCurrentWorkingDirectory(dir1);
        ParsedCommand cmd1 = new ParsedCommand("pwd", Collections.emptyList());
        CommandResult result1 = pwd.execute(cmd1);
        Assertions.assertEquals("/dir1", result1.getMessage());

        fs.setCurrentWorkingDirectory(dir2);
        ParsedCommand cmd2 = new ParsedCommand("pwd", Collections.emptyList());
        CommandResult result2 = pwd.execute(cmd2);
        Assertions.assertEquals("/dir1/dir2", result2.getMessage());

        fs.setCurrentWorkingDirectory(fs.getRoot());
        ParsedCommand cmd3 = new ParsedCommand("pwd", Collections.emptyList());
        CommandResult result3 = pwd.execute(cmd3);
        Assertions.assertEquals("/", result3.getMessage());
    }

    @Test
    void execute_alwaysReturnsSuccess() {
        ParsedCommand cmd = new ParsedCommand("pwd", Collections.emptyList());

        CommandResult result = pwd.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
    }

    @Test
    void execute_pathDoesNotEndWithSlash() {
        DirectoryINode dir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("mydir", dir);
        fs.setCurrentWorkingDirectory(dir);

        ParsedCommand cmd = new ParsedCommand("pwd", Collections.emptyList());

        CommandResult result = pwd.execute(cmd);

        String path = result.getMessage();
        if (!path.equals("/")) {
            Assertions.assertFalse(path.endsWith("/"));
        }
    }
}