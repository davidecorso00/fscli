package ch.supsi.fscli.model.inode;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class FileSystemTest {

    @BeforeEach
    void setup() {
        FileSystem.resetInstance();
    }

    @AfterEach
    void cleanup() {
        FileSystem.resetInstance();
    }


    @Test
    void getInstance_returnsSameInstance() {
        FileSystem instance1 = FileSystem.getInstance();
        FileSystem instance2 = FileSystem.getInstance();

        Assertions.assertNotNull(instance1);
        Assertions.assertSame(instance1, instance2, "getInstance deve restituire sempre lo stesso oggetto Singleton");
    }

    @Test
    void resetInstance_createsNewInstanceAndResetsIds() {
        // 1. Prendi istanza e ID corrente
        FileSystem fs1 = FileSystem.getInstance();
        int rootId1 = fs1.getRoot().getId(); // Dovrebbe essere 0

        // Consuma un ID creando un nodo
        new FileINode();

        // 2. Resetta
        FileSystem.resetInstance();

        // 3. Prendi nuova istanza
        FileSystem fs2 = FileSystem.getInstance();
        int rootId2 = fs2.getRoot().getId();

        // Assert: Le istanze sono diverse
        Assertions.assertNotSame(fs1, fs2, "resetInstance deve creare un nuovo oggetto FileSystem");

        // Assert: L'ID della root è tornato a 0 (reset statico di INode)
        Assertions.assertEquals(0, rootId1);
        Assertions.assertEquals(0, rootId2);
    }

    @Test
    void isRootNull_behavior() throws Exception {
        // Dopo il setup (che chiama resetInstance), myself non è null
        Assertions.assertFalse(FileSystem.isRootNull());

        // Per testare il caso TRUE, dobbiamo forzare myself a null via Reflection
        // perché resetInstance() lo inizializza sempre.
        Field instanceField = FileSystem.class.getDeclaredField("myself");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        Assertions.assertTrue(FileSystem.isRootNull(), "Deve ritornare true se il singleton è null");
    }


    @Test
    void initialization_createsRootAndSetsCWD() {
        FileSystem fs = FileSystem.getInstance();

        DirectoryINode root = fs.getRoot();
        DirectoryINode cwd = fs.getCurrentWorkingDirectory();

        Assertions.assertNotNull(root, "La root non deve essere null");
        Assertions.assertNotNull(cwd, "La CWD non deve essere null");
        Assertions.assertSame(root, cwd, "All'inizio, CWD deve coincidere con la Root");
        Assertions.assertNull(root.getParentDirectory(), "La Root non deve avere un genitore");
        Assertions.assertEquals("/", root.getAbsolutePath(), "Il path della root deve essere /");
    }

    @Test
    void setCurrentWorkingDirectory_validDirectory_updatesState() {
        FileSystem fs = FileSystem.getInstance();
        DirectoryINode root = fs.getRoot();

        DirectoryINode newDir = new DirectoryINode(root);
        root.addEntry("home", newDir);

        fs.setCurrentWorkingDirectory(newDir);

        Assertions.assertSame(newDir, fs.getCurrentWorkingDirectory());
        Assertions.assertNotSame(root, fs.getCurrentWorkingDirectory());
    }

    @Test
    void setCurrentWorkingDirectory_null_throwsException() {
        FileSystem fs = FileSystem.getInstance();

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> fs.setCurrentWorkingDirectory(null)
        );

        Assertions.assertEquals("Current directory cannot be null", ex.getMessage());
    }

    @Test
    void loadFromFileSystem_validOtherInstance_copiesState() {
        // 1. Configura "Vecchio" FileSystem (fs1)
        FileSystem fs1 = FileSystem.getInstance();
        DirectoryINode root1 = fs1.getRoot();
        DirectoryINode childDir = new DirectoryINode(root1);
        root1.addEntry("docs", childDir);
        fs1.setCurrentWorkingDirectory(childDir); // fs1 è in /docs

        // Salva riferimenti per verifica
        DirectoryINode expectedRoot = fs1.getRoot();
        DirectoryINode expectedCwd = fs1.getCurrentWorkingDirectory();

        // 2. Resetta per ottenere un "Nuovo" FileSystem (fs2) pulito
        FileSystem.resetInstance();
        FileSystem fs2 = FileSystem.getInstance();

        // Verifica stato iniziale fs2
        Assertions.assertNotSame(expectedRoot, fs2.getRoot());
        Assertions.assertNotSame(expectedCwd, fs2.getCurrentWorkingDirectory());

        // 3. Carica stato di fs1 in fs2
        fs2.loadFromFileSystem(fs1);

        // 4. Verifica che fs2 ora punti agli oggetti di fs1
        Assertions.assertSame(expectedRoot, fs2.getRoot());
        Assertions.assertSame(expectedRoot, fs2.getCurrentWorkingDirectory());
    }

    @Test
    void loadFromFileSystem_null_doesNothing() {
        FileSystem fs = FileSystem.getInstance();
        DirectoryINode originalRoot = fs.getRoot();
        DirectoryINode originalCwd = fs.getCurrentWorkingDirectory();

        fs.loadFromFileSystem(null);

        Assertions.assertSame(originalRoot, fs.getRoot());
        Assertions.assertSame(originalCwd, fs.getCurrentWorkingDirectory());
    }
}