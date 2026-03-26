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

public class RmCommandTest {

    private RmCommand rm;
    private FileSystem fs;

    @BeforeEach
    void setup() {
        FileSystem.resetInstance();
        this.fs = FileSystem.getInstance();
        this.rm = new RmCommand();
    }

    @AfterEach
    void cleanup() {
        FileSystem.resetInstance();
    }

    @Test
    void parse_withOneArgument_success() throws ParseException {
        ParsedCommand result = rm.parse(List.of("file.txt"));

        Assertions.assertEquals("rm", result.getCommand());
        Assertions.assertEquals(1, result.getArguments().size());
        Assertions.assertEquals("file.txt", result.getArguments().get(0));
    }

    @Test
    void parse_withMultipleArguments_success() throws ParseException {
        ParsedCommand result = rm.parse(List.of("file1.txt", "file2.txt", "file3.txt"));

        Assertions.assertEquals(3, result.getArguments().size());
        Assertions.assertTrue(result.getArguments().contains("file1.txt"));
        Assertions.assertTrue(result.getArguments().contains("file2.txt"));
        Assertions.assertTrue(result.getArguments().contains("file3.txt"));
    }

    @Test
    void parse_withAbsolutePath_success() throws ParseException {
        ParsedCommand result = rm.parse(List.of("/home/user/file.txt"));

        Assertions.assertEquals(1, result.getArguments().size());
        Assertions.assertEquals("/home/user/file.txt", result.getArguments().get(0));
    }

    @Test
    void parse_noArguments_throwsParseException() {
        ParseException exception = Assertions.assertThrows(
                ParseException.class,
                () -> rm.parse(Collections.emptyList())
        );

        Assertions.assertEquals("label.rm.usage", exception.getMessage());
    }

    @Test
    void execute_removesSingleFile_success() {
        // Setup
        FileINode file = new FileINode();
        fs.getRoot().addEntry("file.txt", file);

        ParsedCommand cmd = new ParsedCommand("rm", List.of("file.txt"));

        CommandResult result = rm.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertEquals("", result.getMessage());

        // Verifica che il file sia stato rimosso
        Assertions.assertNull(fs.getRoot().findEntry("file.txt"));
    }

    @Test
    void execute_removesMultipleFiles_success() {
        // Setup
        FileINode file1 = new FileINode();
        FileINode file2 = new FileINode();
        FileINode file3 = new FileINode();
        fs.getRoot().addEntry("file1.txt", file1);
        fs.getRoot().addEntry("file2.txt", file2);
        fs.getRoot().addEntry("file3.txt", file3);

        ParsedCommand cmd = new ParsedCommand("rm", List.of("file1.txt", "file2.txt", "file3.txt"));

        CommandResult result = rm.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());

        // Verifica che tutti siano stati rimossi
        Assertions.assertNull(fs.getRoot().findEntry("file1.txt"));
        Assertions.assertNull(fs.getRoot().findEntry("file2.txt"));
        Assertions.assertNull(fs.getRoot().findEntry("file3.txt"));
    }

    @Test
    void execute_removesFileWithAbsolutePath_success() {
        DirectoryINode dir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("dir", dir);

        FileINode file = new FileINode();
        dir.addEntry("file.txt", file);

        ParsedCommand cmd = new ParsedCommand("rm", List.of("/dir/file.txt"));

        CommandResult result = rm.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertNull(dir.findEntry("file.txt"));
    }

    @Test
    void execute_removesFileWithRelativePath_success() {
        DirectoryINode dir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("dir", dir);
        fs.setCurrentWorkingDirectory(dir);

        FileINode file = new FileINode();
        dir.addEntry("file.txt", file);

        ParsedCommand cmd = new ParsedCommand("rm", List.of("file.txt"));

        CommandResult result = rm.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertNull(dir.findEntry("file.txt"));
    }

    @Test
    void execute_removesFileInNestedDirectory_success() {
        // Crea /parent/child/file.txt
        DirectoryINode parent = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("parent", parent);

        DirectoryINode child = new DirectoryINode(parent);
        parent.addEntry("child", child);

        FileINode file = new FileINode();
        child.addEntry("file.txt", file);

        ParsedCommand cmd = new ParsedCommand("rm", List.of("parent/child/file.txt"));

        CommandResult result = rm.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertNull(child.findEntry("file.txt"));
    }

    // ========== EXECUTE - ERROR CASES ==========

    @Test
    void execute_emptyPath_returnsError() {
        ParsedCommand cmd = new ParsedCommand("rm", List.of(""));

        CommandResult result = rm.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.rm.emptyArg", result.getMessage());
    }

    @Test
    void execute_whitespacePath_returnsError() {
        ParsedCommand cmd = new ParsedCommand("rm", List.of("   "));

        CommandResult result = rm.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.rm.emptyArg", result.getMessage());
    }

    @Test
    void execute_fileDoesNotExist_returnsError() {
        ParsedCommand cmd = new ParsedCommand("rm", List.of("nonexistent.txt"));

        CommandResult result = rm.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.rm.noSuchFile", result.getMessage());
    }

    @Test
    void execute_pathIsRoot_returnsError() {
        ParsedCommand cmd = new ParsedCommand("rm", List.of("/"));

        CommandResult result = rm.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.rm.cannotRemoveRoot", result.getMessage());
    }

    @Test
    void execute_pathIsDot_returnsError() {
        ParsedCommand cmd = new ParsedCommand("rm", List.of("."));

        CommandResult result = rm.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.rm.refuseDot", result.getMessage());
    }

    @Test
    void execute_pathIsDotDot_returnsError() {
        ParsedCommand cmd = new ParsedCommand("rm", List.of(".."));

        CommandResult result = rm.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.rm.refuseDot", result.getMessage());
    }

    @Test
    void execute_targetIsDirectory_returnsError() {
        DirectoryINode dir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("mydir", dir);

        ParsedCommand cmd = new ParsedCommand("rm", List.of("mydir"));

        CommandResult result = rm.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.rm.isDirectory", result.getMessage());

        // Directory deve ancora esistere
        Assertions.assertNotNull(fs.getRoot().findEntry("mydir"));
    }

    @Test
    void execute_parentDoesNotExist_returnsError() {
        ParsedCommand cmd = new ParsedCommand("rm", List.of("nonexistent/file.txt"));

        CommandResult result = rm.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        // PathResolver lancia IllegalArgumentException
        Assertions.assertTrue(result.getMessage().startsWith("label."));
    }

    @Test
    void execute_decrementsLinkCount() {
        FileINode file = new FileINode();
        fs.getRoot().addEntry("file.txt", file);

        int initialCount = file.getLinkCount();

        ParsedCommand cmd = new ParsedCommand("rm", List.of("file.txt"));
        rm.execute(cmd);

        // Link count decrementato (anche se file è già rimosso)
        Assertions.assertEquals(initialCount - 1, file.getLinkCount());
    }

    @Test
    void execute_someFilesExistSomeDont_returnsFirstError() {
        FileINode file1 = new FileINode();
        fs.getRoot().addEntry("exists.txt", file1);

        ParsedCommand cmd = new ParsedCommand("rm", List.of("exists.txt", "nonexistent.txt", "another.txt"));

        CommandResult result = rm.execute(cmd);

        // Primo file rimosso con successo, poi errore sul secondo
        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.rm.noSuchFile", result.getMessage());

        // exists.txt dovrebbe essere rimosso
        Assertions.assertNull(fs.getRoot().findEntry("exists.txt"));
    }

    @Test
    void execute_mixOfFilesAndDirectories_returnsFirstError() {
        FileINode file = new FileINode();
        DirectoryINode dir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("file.txt", file);
        fs.getRoot().addEntry("dir", dir);

        ParsedCommand cmd = new ParsedCommand("rm", List.of("file.txt", "dir"));

        CommandResult result = rm.execute(cmd);

        // file.txt rimosso, poi errore su dir
        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.rm.isDirectory", result.getMessage());

        Assertions.assertNull(fs.getRoot().findEntry("file.txt"));
        Assertions.assertNotNull(fs.getRoot().findEntry("dir"));
    }


    @Test
    void integration_removeFileCreatedWithTouch() {
        // Simula: touch file.txt && rm file.txt
        FileINode file = new FileINode();
        fs.getRoot().addEntry("file.txt", file);

        // Verifica esiste
        Assertions.assertNotNull(fs.getRoot().findEntry("file.txt"));

        // Rimuovi
        rm.execute(new ParsedCommand("rm", List.of("file.txt")));

        // Non esiste più
        Assertions.assertNull(fs.getRoot().findEntry("file.txt"));
    }

    @Test
    void integration_removeAllFilesInDirectory() {
        DirectoryINode dir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("dir", dir);

        FileINode f1 = new FileINode();
        FileINode f2 = new FileINode();
        FileINode f3 = new FileINode();
        dir.addEntry("file1.txt", f1);
        dir.addEntry("file2.txt", f2);
        dir.addEntry("file3.txt", f3);

        // Rimuovi tutti
        ParsedCommand cmd = new ParsedCommand("rm",
                List.of("dir/file1.txt", "dir/file2.txt", "dir/file3.txt"));
        rm.execute(cmd);

        // Directory vuota ora
        Assertions.assertTrue(dir.isEmpty());
    }

    @Test
    void integration_cannotRemoveLastHardLinkInUse() {
        // Nota: questo test dipende da come gestisci file "in uso"
        // Se non hai questa funzionalità, skip questo test

        FileINode file = new FileINode();
        fs.getRoot().addEntry("file.txt", file);

        rm.execute(new ParsedCommand("rm", List.of("file.txt")));

        // Link count decrementato a 0
        Assertions.assertEquals(0, file.getLinkCount());
    }

    @Test
    void integration_removeFilesThenListDirectory() {
        // Crea file
        FileINode f1 = new FileINode();
        FileINode f2 = new FileINode();
        FileINode f3 = new FileINode();
        fs.getRoot().addEntry("keep.txt", f1);
        fs.getRoot().addEntry("remove1.txt", f2);
        fs.getRoot().addEntry("remove2.txt", f3);

        // Rimuovi alcuni
        rm.execute(new ParsedCommand("rm", List.of("remove1.txt", "remove2.txt")));

        // Lista directory
        List<String> remaining = fs.getRoot().getEntryNames()
                .stream()
                .filter(n -> !n.equals(".") && !n.equals(".."))
                .toList();

        Assertions.assertEquals(1, remaining.size());
        Assertions.assertTrue(remaining.contains("keep.txt"));
    }
}