package ch.supsi.fscli.model.commands;

import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.data_access.CommandResultStatus;
import ch.supsi.fscli.model.command_management.LocalizedParseException;
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

public class LsCommandTest {

    private LsCommand ls;
    private FileSystem fs;

    @BeforeEach
    void setup() {
        FileSystem.resetInstance();
        this.fs = FileSystem.getInstance();
        this.ls = new LsCommand();
    }

    @AfterEach
    void cleanup() {
        FileSystem.resetInstance();
    }

    @Test
    void parse_noArguments_defaultsToDot() throws ParseException {
        ParsedCommand result = ls.parse(Collections.emptyList());

        Assertions.assertEquals("ls", result.getCommand());
        Assertions.assertEquals(1, result.getArguments().size());
        Assertions.assertEquals(".", result.getArguments().get(0));
    }

    @Test
    void parse_withPath_returnsSamePath() throws ParseException {
        ParsedCommand result = ls.parse(List.of("/home"));

        Assertions.assertEquals(1, result.getArguments().size());
        Assertions.assertEquals("/home", result.getArguments().get(0));
    }

    @Test
    void parse_withMultiplePaths_returnsAllPaths() throws ParseException {
        ParsedCommand result = ls.parse(List.of("dir1", "dir2", "dir3"));

        Assertions.assertEquals(3, result.getArguments().size());
        Assertions.assertTrue(result.getArguments().contains("dir1"));
        Assertions.assertTrue(result.getArguments().contains("dir2"));
        Assertions.assertTrue(result.getArguments().contains("dir3"));
    }

    @Test
    void parse_withFlagI_setsInodeFlag() throws ParseException {
        ParsedCommand result = ls.parse(List.of("-i"));

        Assertions.assertTrue(result.getFlag("i"));
        Assertions.assertFalse(result.getFlag("a"));
    }

    @Test
    void parse_withFlagA_setsHiddenFlag() throws ParseException {
        ParsedCommand result = ls.parse(List.of("-a"));

        Assertions.assertTrue(result.getFlag("a"));
        Assertions.assertFalse(result.getFlag("i"));
    }

    @Test
    void parse_withCombinedFlags_setsBothFlags() throws ParseException {
        ParsedCommand result = ls.parse(List.of("-ia"));

        Assertions.assertTrue(result.getFlag("i"));
        Assertions.assertTrue(result.getFlag("a"));
    }

    @Test
    void parse_withFlagsAndPath_parsesBoth() throws ParseException {
        ParsedCommand result = ls.parse(List.of("-ia", "/home"));

        Assertions.assertTrue(result.getFlag("i"));
        Assertions.assertTrue(result.getFlag("a"));
        Assertions.assertTrue(result.getArguments().contains("/home"));
    }

    @Test
    void parse_withInvalidFlagInCombination_throwsException() {
        Assertions.assertThrows(
                LocalizedParseException.class,
                () -> ls.parse(List.of("-iax"))
        );
    }


    @Test
    void execute_emptyDirectory_returnsEmptyOutput() {
        ParsedCommand cmd = new ParsedCommand("ls", List.of("."));

        CommandResult result = ls.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        // Root con . e .. → output vuoto senza -a
        Assertions.assertTrue(result.getMessage().isEmpty() ||
                result.getMessage().trim().isEmpty());
    }

    @Test
    void execute_directoryWithFiles_listsFiles() {
        // Setup: crea file nella root
        FileINode file1 = new FileINode();
        FileINode file2 = new FileINode();
        fs.getRoot().addEntry("file1.txt", file1);
        fs.getRoot().addEntry("file2.txt", file2);

        ParsedCommand cmd = new ParsedCommand("ls", List.of("."));

        CommandResult result = ls.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        String output = result.getMessage();
        Assertions.assertTrue(output.contains("file1.txt"));
        Assertions.assertTrue(output.contains("file2.txt"));
    }

    @Test
    void execute_directoryWithSubdirectories_listsDirectories() {
        DirectoryINode dir1 = new DirectoryINode(fs.getRoot());
        DirectoryINode dir2 = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("dir1", dir1);
        fs.getRoot().addEntry("dir2", dir2);

        ParsedCommand cmd = new ParsedCommand("ls", List.of("."));

        CommandResult result = ls.execute(cmd);

        String output = result.getMessage();
        Assertions.assertTrue(output.contains("dir1"));
        Assertions.assertTrue(output.contains("dir2"));
    }

    @Test
    void execute_listsSortedAlphabetically() {
        FileINode fileZ = new FileINode();
        FileINode fileA = new FileINode();
        FileINode fileM = new FileINode();
        fs.getRoot().addEntry("zzz.txt", fileZ);
        fs.getRoot().addEntry("aaa.txt", fileA);
        fs.getRoot().addEntry("mmm.txt", fileM);

        ParsedCommand cmd = new ParsedCommand("ls", List.of("."));

        CommandResult result = ls.execute(cmd);

        String output = result.getMessage();
        int posA = output.indexOf("aaa.txt");
        int posM = output.indexOf("mmm.txt");
        int posZ = output.indexOf("zzz.txt");

        Assertions.assertTrue(posA < posM);
        Assertions.assertTrue(posM < posZ);
    }

    @Test
    void execute_withFlagI_showsInodeNumbers() {
        FileINode file = new FileINode();
        fs.getRoot().addEntry("file.txt", file);

        ParsedCommand cmd = new ParsedCommand("ls", List.of("."));
        cmd.setFlag("i", true);

        CommandResult result = ls.execute(cmd);

        String output = result.getMessage();
        // Format: "ID filename"
        Assertions.assertTrue(output.matches(".*\\d+\\s+file\\.txt.*"));
    }

    @Test
    void execute_withFlagA_showsHiddenFiles() {
        FileINode hidden = new FileINode();
        fs.getRoot().addEntry(".hidden", hidden);

        // Senza -a
        ParsedCommand cmd1 = new ParsedCommand("ls", List.of("."));
        CommandResult result1 = ls.execute(cmd1);
        Assertions.assertFalse(result1.getMessage().contains(".hidden"));

        // Con -a
        ParsedCommand cmd2 = new ParsedCommand("ls", List.of("."));
        cmd2.setFlag("a", true);
        CommandResult result2 = ls.execute(cmd2);
        Assertions.assertTrue(result2.getMessage().contains(".hidden"));
    }


    @Test
    void execute_withAbsolutePath_listsTargetDirectory() {
        DirectoryINode subdir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("subdir", subdir);

        FileINode file = new FileINode();
        subdir.addEntry("file.txt", file);

        ParsedCommand cmd = new ParsedCommand("ls", List.of("/subdir"));

        CommandResult result = ls.execute(cmd);

        Assertions.assertTrue(result.getMessage().contains("file.txt"));
    }

    @Test
    void execute_onSingleFile_showsOnlyThatFile() {
        FileINode file = new FileINode();
        fs.getRoot().addEntry("myfile.txt", file);

        ParsedCommand cmd = new ParsedCommand("ls", List.of("myfile.txt"));

        CommandResult result = ls.execute(cmd);

        String output = result.getMessage().trim();
        Assertions.assertEquals("myfile.txt", output);
    }

    @Test
    void execute_onNonExistentPath_showsError() {
        ParsedCommand cmd = new ParsedCommand("ls", List.of("nonexistent"));

        CommandResult result = ls.execute(cmd);

        // Success status ma con errore nell'output
        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertTrue(result.getMessage().contains("cannot access"));
        Assertions.assertTrue(result.getMessage().contains("nonexistent"));
    }

    @Test
    void execute_withMultiplePaths_showsAllWithHeaders() {
        DirectoryINode dir1 = new DirectoryINode(fs.getRoot());
        DirectoryINode dir2 = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("dir1", dir1);
        fs.getRoot().addEntry("dir2", dir2);

        FileINode file1 = new FileINode();
        FileINode file2 = new FileINode();
        dir1.addEntry("file1.txt", file1);
        dir2.addEntry("file2.txt", file2);

        ParsedCommand cmd = new ParsedCommand("ls", List.of("dir1", "dir2"));

        CommandResult result = ls.execute(cmd);

        String output = result.getMessage();
        Assertions.assertTrue(output.contains("dir1:"));
        Assertions.assertTrue(output.contains("dir2:"));
        Assertions.assertTrue(output.contains("file1.txt"));
        Assertions.assertTrue(output.contains("file2.txt"));
    }

    @Test
    void execute_withDot_listsCurrentDirectory() {
        FileINode file = new FileINode();
        fs.getRoot().addEntry("file.txt", file);

        ParsedCommand cmd = new ParsedCommand("ls", List.of("."));

        CommandResult result = ls.execute(cmd);

        Assertions.assertTrue(result.getMessage().contains("file.txt"));
    }

    @Test
    void execute_withTrailingSlash_listsDirectory() {
        DirectoryINode dir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("mydir", dir);

        FileINode file = new FileINode();
        dir.addEntry("file.txt", file);

        ParsedCommand cmd = new ParsedCommand("ls", List.of("mydir/"));

        CommandResult result = ls.execute(cmd);

        Assertions.assertTrue(result.getMessage().contains("file.txt"));
    }

    @Test
    void execute_mixOfValidAndInvalidPaths_showsBothResultsAndErrors() {
        DirectoryINode validDir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("valid", validDir);

        FileINode file = new FileINode();
        validDir.addEntry("file.txt", file);

        ParsedCommand cmd = new ParsedCommand("ls", List.of("valid", "invalid"));

        CommandResult result = ls.execute(cmd);

        String output = result.getMessage();
        Assertions.assertTrue(output.contains("file.txt"));
        Assertions.assertTrue(output.contains("cannot access"));
        Assertions.assertTrue(output.contains("invalid"));
    }
}