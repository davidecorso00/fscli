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

public class MvCommandTest {

    private MvCommand mv;
    private FileSystem fs;

    @BeforeEach
    void setup() {
        FileSystem.resetInstance();
        this.fs = FileSystem.getInstance();
        this.mv = new MvCommand();
    }

    @AfterEach
    void cleanup() {
        FileSystem.resetInstance();
    }

    @Test
    void parse_twoArguments_success() throws ParseException {
        ParsedCommand result = mv.parse(List.of("source", "dest"));

        Assertions.assertEquals("mv", result.getCommand());
        Assertions.assertEquals(2, result.getArguments().size());
        Assertions.assertEquals("source", result.getArguments().get(0));
        Assertions.assertEquals("dest", result.getArguments().get(1));
    }

    @Test
    void parse_multipleArguments_success() throws ParseException {
        ParsedCommand result = mv.parse(List.of("file1", "file2", "file3", "destdir"));

        Assertions.assertEquals(4, result.getArguments().size());
        Assertions.assertEquals("destdir", result.getArguments().get(3));
    }

    @Test
    void parse_noArguments_throwsParseException() {
        ParseException exception = Assertions.assertThrows(
                ParseException.class,
                () -> mv.parse(Collections.emptyList())
        );

        Assertions.assertEquals("label.mv.usage", exception.getMessage());
    }

    @Test
    void parse_oneArgument_throwsParseException() {
        ParseException exception = Assertions.assertThrows(
                ParseException.class,
                () -> mv.parse(List.of("onlyOne"))
        );

        Assertions.assertEquals("label.mv.missingDest", exception.getMessage());
    }

    @Test
    void execute_renameFile_success() {
        // Setup: crea file source
        FileINode file = new FileINode();
        fs.getRoot().addEntry("oldname.txt", file);

        ParsedCommand cmd = new ParsedCommand("mv", List.of("oldname.txt", "newname.txt"));

        CommandResult result = mv.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertEquals("", result.getMessage());

        // Verifica che oldname non esista più
        Assertions.assertNull(fs.getRoot().findEntry("oldname.txt"));

        // Verifica che newname esista e sia lo stesso file
        FileSystemComponent renamed = fs.getRoot().findEntry("newname.txt");
        Assertions.assertNotNull(renamed);
        Assertions.assertSame(file, renamed);
    }

    @Test
    void execute_renameDirectory_success() {
        DirectoryINode dir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("olddir", dir);

        ParsedCommand cmd = new ParsedCommand("mv", List.of("olddir", "newdir"));

        CommandResult result = mv.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertNull(fs.getRoot().findEntry("olddir"));
        Assertions.assertNotNull(fs.getRoot().findEntry("newdir"));
    }

    @Test
    void execute_moveFileIntoDirectory_success() {
        // Setup
        FileINode file = new FileINode();
        fs.getRoot().addEntry("file.txt", file);

        DirectoryINode destDir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("destdir", destDir);

        ParsedCommand cmd = new ParsedCommand("mv", List.of("file.txt", "destdir"));

        CommandResult result = mv.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());

        // Verifica file non è più nella root
        Assertions.assertNull(fs.getRoot().findEntry("file.txt"));

        // Verifica file è in destdir con nome originale
        FileSystemComponent moved = destDir.findEntry("file.txt");
        Assertions.assertNotNull(moved);
        Assertions.assertSame(file, moved);
    }

    @Test
    void execute_moveFileIntoDirWithTrailingSlash_success() {
        FileINode file = new FileINode();
        fs.getRoot().addEntry("file.txt", file);

        DirectoryINode destDir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("destdir", destDir);

        ParsedCommand cmd = new ParsedCommand("mv", List.of("file.txt", "destdir/"));

        CommandResult result = mv.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertNotNull(destDir.findEntry("file.txt"));
    }

    @Test
    void execute_moveMultipleFilesIntoDirectory_success() {
        // Setup
        FileINode file1 = new FileINode();
        FileINode file2 = new FileINode();
        fs.getRoot().addEntry("file1.txt", file1);
        fs.getRoot().addEntry("file2.txt", file2);

        DirectoryINode destDir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("destdir", destDir);

        ParsedCommand cmd = new ParsedCommand("mv", List.of("file1.txt", "file2.txt", "destdir"));

        CommandResult result = mv.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());

        // Verifica entrambi spostati
        Assertions.assertNull(fs.getRoot().findEntry("file1.txt"));
        Assertions.assertNull(fs.getRoot().findEntry("file2.txt"));
        Assertions.assertNotNull(destDir.findEntry("file1.txt"));
        Assertions.assertNotNull(destDir.findEntry("file2.txt"));
    }

    @Test
    void execute_sourceDoesNotExist_returnsError() {
        ParsedCommand cmd = new ParsedCommand("mv", List.of("nonexistent.txt", "dest.txt"));

        CommandResult result = mv.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.mv.noSuchFile", result.getMessage());
    }

    @Test
    void execute_sourceIsDot_returnsError() {
        ParsedCommand cmd = new ParsedCommand("mv", List.of(".", "dest"));

        CommandResult result = mv.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.mv.cannotMoveSpecial", result.getMessage());
    }

    @Test
    void execute_sourceIsDotDot_returnsError() {
        ParsedCommand cmd = new ParsedCommand("mv", List.of("..", "dest"));

        CommandResult result = mv.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.mv.cannotMoveSpecial", result.getMessage());
    }

    @Test
    void execute_destIsDotDot_returnsError() {
        FileINode file = new FileINode();
        fs.getRoot().addEntry("file.txt", file);

        ParsedCommand cmd = new ParsedCommand("mv", List.of("file.txt", ".."));

        CommandResult result = mv.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.mv.sameFile", result.getMessage());
    }

    @Test
    void execute_multipleSourcesWithNonDirDest_returnsError() {
        FileINode file1 = new FileINode();
        FileINode file2 = new FileINode();
        FileINode destFile = new FileINode();
        fs.getRoot().addEntry("file1.txt", file1);
        fs.getRoot().addEntry("file2.txt", file2);
        fs.getRoot().addEntry("dest.txt", destFile);

        ParsedCommand cmd = new ParsedCommand("mv", List.of("file1.txt", "file2.txt", "dest.txt"));

        CommandResult result = mv.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.mv.targetNotDir", result.getMessage());
    }

    @Test
    void execute_moveToSameFile_returnsError() {
        FileINode file = new FileINode();
        fs.getRoot().addEntry("file.txt", file);

        ParsedCommand cmd = new ParsedCommand("mv", List.of("file.txt", "file.txt"));

        CommandResult result = mv.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.mv.sameFile", result.getMessage());
    }

    @Test
    void execute_moveDirectoryIntoItself_returnsError() {
        DirectoryINode dir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("dir", dir);

        ParsedCommand cmd = new ParsedCommand("mv", List.of("dir", "dir/subdir"));

        CommandResult result = mv.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.mv.intoItself", result.getMessage());
    }

    @Test
    void execute_moveDirectoryIntoDescendant_returnsError() {
        // Crea /parent/child
        DirectoryINode parent = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("parent", parent);

        DirectoryINode child = new DirectoryINode(parent);
        parent.addEntry("child", child);

        // Prova a spostare parent dentro child (ciclo!)
        ParsedCommand cmd = new ParsedCommand("mv", List.of("parent", "parent/child/newparent"));

        CommandResult result = mv.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.mv.intoItself", result.getMessage());
    }


    @Test
    void execute_overwriteFile_success() {
        FileINode source = new FileINode();
        FileINode dest = new FileINode();
        fs.getRoot().addEntry("source.txt", source);
        fs.getRoot().addEntry("dest.txt", dest);

        ParsedCommand cmd = new ParsedCommand("mv", List.of("source.txt", "dest.txt"));

        CommandResult result = mv.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());

        // dest.txt ora è il vecchio source
        Assertions.assertNull(fs.getRoot().findEntry("source.txt"));
        FileSystemComponent newDest = fs.getRoot().findEntry("dest.txt");
        Assertions.assertSame(source, newDest);
    }

    @Test
    void execute_cannotOverwriteDirectoryWithFile_returnsError() {
        FileINode file = new FileINode();
        fs.getRoot().addEntry("file.txt", file);

        DirectoryINode dir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("destdir", dir);

        ParsedCommand cmd = new ParsedCommand("mv", List.of("file.txt", "destdir"));

        // file.txt va dentro destdir, non sovrascrive
        CommandResult result = mv.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertNotNull(dir.findEntry("file.txt"));
    }

    @Test
    void execute_cannotOverwriteFileWithDirectory_returnsError() {
        DirectoryINode sourceDir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("sourcedir", sourceDir);

        FileINode destFile = new FileINode();
        fs.getRoot().addEntry("destfile.txt", destFile);

        ParsedCommand cmd = new ParsedCommand("mv", List.of("sourcedir", "destfile.txt"));

        CommandResult result = mv.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.ERROR, result.getStatus());
        Assertions.assertEquals("label.mv.cannotOverwriteNonDir", result.getMessage());
    }

    @Test
    void execute_cannotOverwriteDirectoryWithDirectory_returnsError() {
        DirectoryINode sourceDir = new DirectoryINode(fs.getRoot());
        DirectoryINode destDir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("sourcedir", sourceDir);
        fs.getRoot().addEntry("destdir", destDir);

        ParsedCommand cmd = new ParsedCommand("mv", List.of("sourcedir", "destdir"));

        CommandResult result = mv.execute(cmd);

        // destdir è directory esistente → sourcedir va dentro
        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertNotNull(destDir.findEntry("sourcedir"));
    }


    @Test
    void execute_withAbsolutePaths_success() {
        FileINode file = new FileINode();
        fs.getRoot().addEntry("file.txt", file);

        ParsedCommand cmd = new ParsedCommand("mv", List.of("/file.txt", "/newname.txt"));

        CommandResult result = mv.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertNull(fs.getRoot().findEntry("file.txt"));
        Assertions.assertNotNull(fs.getRoot().findEntry("newname.txt"));
    }

    @Test
    void execute_withRelativePaths_success() {
        DirectoryINode dir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("dir", dir);
        fs.setCurrentWorkingDirectory(dir);

        FileINode file = new FileINode();
        dir.addEntry("file.txt", file);

        ParsedCommand cmd = new ParsedCommand("mv", List.of("file.txt", "renamed.txt"));

        CommandResult result = mv.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertNull(dir.findEntry("file.txt"));
        Assertions.assertNotNull(dir.findEntry("renamed.txt"));
    }

    @Test
    void execute_moveAcrossDirectories_success() {
        // Setup: /source/file.txt → /dest/
        DirectoryINode sourceDir = new DirectoryINode(fs.getRoot());
        DirectoryINode destDir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("source", sourceDir);
        fs.getRoot().addEntry("dest", destDir);

        FileINode file = new FileINode();
        sourceDir.addEntry("file.txt", file);

        ParsedCommand cmd = new ParsedCommand("mv", List.of("source/file.txt", "dest/"));

        CommandResult result = mv.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertNull(sourceDir.findEntry("file.txt"));
        Assertions.assertNotNull(destDir.findEntry("file.txt"));
    }


    @Test
    void integration_moveAndRenameInOneOperation() {
        DirectoryINode sourceDir = new DirectoryINode(fs.getRoot());
        DirectoryINode destDir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("source", sourceDir);
        fs.getRoot().addEntry("dest", destDir);

        FileINode file = new FileINode();
        sourceDir.addEntry("oldname.txt", file);

        ParsedCommand cmd = new ParsedCommand("mv", List.of("source/oldname.txt", "dest/newname.txt"));

        CommandResult result = mv.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertNull(sourceDir.findEntry("oldname.txt"));

        FileSystemComponent moved = destDir.findEntry("newname.txt");
        Assertions.assertNotNull(moved);
        Assertions.assertSame(file, moved);
    }

    @Test
    void integration_movePreservesDirectoryContents() {
        // Crea /source con file interno
        DirectoryINode sourceDir = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("source", sourceDir);

        FileINode innerFile = new FileINode();
        sourceDir.addEntry("inner.txt", innerFile);

        // Rinomina source
        ParsedCommand cmd = new ParsedCommand("mv", List.of("source", "renamed"));

        mv.execute(cmd);

        DirectoryINode renamedDir = (DirectoryINode) fs.getRoot().findEntry("renamed");
        Assertions.assertNotNull(renamedDir);

        // Verifica contenuto preservato
        FileSystemComponent stillThere = renamedDir.findEntry("inner.txt");
        Assertions.assertNotNull(stillThere);
        Assertions.assertSame(innerFile, stillThere);
    }

    @Test
    void integration_complexMove_nestedDirectories() {
        // Crea /a/b/file.txt
        DirectoryINode a = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("a", a);

        DirectoryINode b = new DirectoryINode(a);
        a.addEntry("b", b);

        FileINode file = new FileINode();
        b.addEntry("file.txt", file);

        // Crea /c
        DirectoryINode c = new DirectoryINode(fs.getRoot());
        fs.getRoot().addEntry("c", c);

        // Muovi /a/b/file.txt → /c/
        ParsedCommand cmd = new ParsedCommand("mv", List.of("a/b/file.txt", "c/"));

        CommandResult result = mv.execute(cmd);

        Assertions.assertEquals(CommandResultStatus.SUCCESS, result.getStatus());
        Assertions.assertNull(b.findEntry("file.txt"));
        Assertions.assertNotNull(c.findEntry("file.txt"));
    }
}