package ch.supsi.fscli.model.inode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

public class DirectoryINodeTest {

    private DirectoryINode root;
    private DirectoryINode dir;

    @BeforeEach
    void setup() {
        INode.resetNextINodeId();
        root = new DirectoryINode(null); // Root
        dir = new DirectoryINode(root);  // Child
    }

    @Test
    void constructor_createsDotAndDotDot() {
        Assertions.assertNotNull(dir.findEntry("."));
        Assertions.assertNotNull(dir.findEntry(".."));
        Assertions.assertSame(dir, dir.findEntry("."));
        Assertions.assertSame(root, dir.findEntry(".."));
    }

    @Test
    void constructor_rootHasNoParentInDotDot() {
        // Root ha ".." che punta a null nel costruttore, ma findEntry("..") gestisce il null
        Assertions.assertNull(root.getParentDirectory());
        Assertions.assertSame(root, root.findEntry("..")); // Logica: se parent è null, restituisce this
    }

    @Test
    void addEntry_validComponent_success() {
        FileSystemComponent mockFile = Mockito.mock(FileSystemComponent.class);
        boolean added = dir.addEntry("file.txt", mockFile);

        Assertions.assertTrue(added);
        Assertions.assertSame(mockFile, dir.findEntry("file.txt"));
    }

    @Test
    void addEntry_nullName_returnsFalse() {
        FileSystemComponent mockFile = Mockito.mock(FileSystemComponent.class);
        Assertions.assertFalse(dir.addEntry(null, mockFile));
        Assertions.assertFalse(dir.addEntry("", mockFile));
    }

    @Test
    void addEntry_specialNames_returnsFalse() {
        FileSystemComponent mockFile = Mockito.mock(FileSystemComponent.class);
        Assertions.assertFalse(dir.addEntry(".", mockFile));
        Assertions.assertFalse(dir.addEntry("..", mockFile));
    }

    @Test
    void addEntry_duplicateName_returnsFalse() {
        FileSystemComponent mockFile = Mockito.mock(FileSystemComponent.class);
        dir.addEntry("file", mockFile);

        // Tentativo di aggiungere stesso nome
        boolean result = dir.addEntry("file", Mockito.mock(FileSystemComponent.class));
        Assertions.assertFalse(result);
    }

    @Test
    void removeEntry_validEntry_removesAndDecrementsLinkCount() {
        // MOCK: Creiamo un componente per verificare l'interazione
        FileSystemComponent mockComponent = Mockito.mock(FileSystemComponent.class);
        dir.addEntry("target", mockComponent);

        // Act
        FileSystemComponent removed = dir.removeEntry("target");

        // Assert
        Assertions.assertNotNull(removed);
        Assertions.assertSame(mockComponent, removed);

        Mockito.verify(mockComponent, Mockito.times(1)).decrementLinkCount();
        Assertions.assertNull(dir.findEntry("target"));
    }

    @Test
    void removeEntry_specialNames_returnsNull() {
        Assertions.assertNull(dir.removeEntry("."));
        Assertions.assertNull(dir.removeEntry(".."));
        Assertions.assertNull(dir.removeEntry(null));
    }

    @Test
    void getAbsolutePath_root_returnsSlash() {
        Assertions.assertEquals("/", root.getAbsolutePath());
    }

    @Test
    void getAbsolutePath_nestedStructure_returnsCorrectPath() {
        // root -> dir -> subDir
        root.addEntry("dir", dir);
        DirectoryINode subDir = new DirectoryINode(dir);
        dir.addEntry("subDir", subDir);

        Assertions.assertEquals("/dir/subDir", subDir.getAbsolutePath());
    }

    @Test
    void getChildren_excludesDotAndDotDot() {
        FileSystemComponent f1 = Mockito.mock(FileSystemComponent.class);
        FileSystemComponent f2 = Mockito.mock(FileSystemComponent.class);
        dir.addEntry("f1", f1);
        dir.addEntry("f2", f2);

        var children = dir.getChildren();

        Assertions.assertEquals(2, children.size());
        Assertions.assertTrue(children.contains(f1));
        Assertions.assertTrue(children.contains(f2));
        // Assicura che . e .. non siano nella collezione ritornata
        Assertions.assertFalse(children.contains(dir));
        Assertions.assertFalse(children.contains(root));
    }

    @Test
    void isEmpty_newDirectory_isEmpty() {
        Assertions.assertTrue(dir.isEmpty());
    }

    @Test
    void isEmpty_directoryWithFile_isNotEmpty() {
        dir.addEntry("file", Mockito.mock(FileSystemComponent.class));
        Assertions.assertFalse(dir.isEmpty());
    }

    @Test
    void findEntry_unknownName_returnsNull() {
        Assertions.assertNull(dir.findEntry("notExisting"));
    }

    @Test
    void getAllEntryNames_returnsSortedAndIncludesDotEntries() {
        dir.addEntry("b", Mockito.mock(FileSystemComponent.class));
        dir.addEntry("a", Mockito.mock(FileSystemComponent.class));

        List<String> names = dir.getAllEntryNames();

        Assertions.assertEquals(List.of(".", "..", "a", "b"), names);
    }

    @Test
    void getEntryNames_returnsAllNamesIncludingDotEntries() {
        dir.addEntry("file", Mockito.mock(FileSystemComponent.class));

        var names = dir.getEntryNames();

        Assertions.assertTrue(names.contains("."));
        Assertions.assertTrue(names.contains(".."));
        Assertions.assertTrue(names.contains("file"));
    }

    @Test
    void getParentViaEntry_returnsParentDirectory() {
        Assertions.assertSame(root, dir.getParentViaEntry());
    }

    @Test
    void setEntries_null_createsEmptyMap() {
        dir.setEntries(null);

        Assertions.assertTrue(dir.getEntries().isEmpty());
    }

    @Test
    void toString_doesNotThrow() {
        Assertions.assertNotNull(dir.toString());
    }


}