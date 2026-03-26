package ch.supsi.fscli.model.inode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SymlinkINodeTest {

    @BeforeEach
    void setup() {
        // Garantisce che ogni test parta con il contatore ID resettato a 0
        INode.resetNextINodeId();
    }

    @Test
    void constructor_setsCorrectTypeAndTarget() {
        String target = "/some/path/to/target";
        SymlinkINode link = new SymlinkINode(target);

        // Verifica stato interno specifico di Symlink
        Assertions.assertEquals(target, link.getTargetPath(), "Il target path deve corrispondere a quello passato nel costruttore");

        // Verifica stato ereditato da INode
        Assertions.assertEquals(0, link.getId(), "Il primo ID deve essere 0");
        Assertions.assertEquals(1, link.getLinkCount(), "Il link count iniziale deve essere 1");
    }

    @Test
    void typeChecks_identifyAsSymbolicLink() {
        SymlinkINode link = new SymlinkINode("target");

        Assertions.assertTrue(link.isSymbolicLink(), "Deve essere identificato come Symlink");
        Assertions.assertFalse(link.isDirectory(), "Non deve essere una directory");
        Assertions.assertFalse(link.isFile(), "Non deve essere un file");
    }

    @Test
    void constructor_acceptsEmptyString() {
        // Symlink vuoti potrebbero non essere validi semanticamente per il comando ln,
        // ma la classe modello dovrebbe accettare la stringa così com'è.
        SymlinkINode link = new SymlinkINode("");
        Assertions.assertEquals("", link.getTargetPath());
    }

    @Test
    void toString_containsCorrectInfo() {
        SymlinkINode link = new SymlinkINode("/etc/passwd");

        // Formato atteso da INode.toString(): "INode [id=%d, type=%s, links=%d]"
        String expected = "INode [id=0, type=SYMLINK, links=1]";

        Assertions.assertEquals(expected, link.toString());
    }
}