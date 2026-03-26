package ch.supsi.fscli.model.inode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class INodeTest {

    // Classe helper concreta per testare la classe astratta INode
    private static class ConcreteINode extends INode {
        public ConcreteINode(INodeType type) {
            super(type);
        }
    }

    @BeforeEach
    void setup() {
        // Fondamentale: resetta il contatore statico prima di ogni test
        INode.resetNextINodeId();
    }

    @Test
    void constructor_assignsIncrementalIds() {
        INode node1 = new ConcreteINode(INodeType.FILE);
        INode node2 = new ConcreteINode(INodeType.FILE);

        // Il primo deve essere 0, il secondo 1
        Assertions.assertEquals(0, node1.getId());
        Assertions.assertEquals(1, node2.getId());
    }

    @Test
    void resetNextINodeId_resetsCounterToZero() {
        // Consuma alcuni ID
        new ConcreteINode(INodeType.FILE);
        new ConcreteINode(INodeType.FILE);

        // Resetta
        INode.resetNextINodeId();

        // Il prossimo deve essere di nuovo 0
        INode node3 = new ConcreteINode(INodeType.FILE);
        Assertions.assertEquals(0, node3.getId());
    }

    @Test
    void linkCount_lifecycle_success() {
        INode node = new ConcreteINode(INodeType.FILE);

        // Default: 1
        Assertions.assertEquals(1, node.getLinkCount());
        Assertions.assertFalse(node.isUnlinked());
        Assertions.assertFalse(node.canBeDeleted());

        // Increment
        node.incrementLinkCount();
        Assertions.assertEquals(2, node.getLinkCount());

        // Decrement
        node.decrementLinkCount();
        Assertions.assertEquals(1, node.getLinkCount());
    }

    @Test
    void decrementLinkCount_cannotGoBelowZero() {
        INode node = new ConcreteINode(INodeType.FILE);

        // Da 1 a 0
        node.decrementLinkCount();
        Assertions.assertEquals(0, node.getLinkCount());
        Assertions.assertTrue(node.isUnlinked());
        Assertions.assertTrue(node.canBeDeleted());

        // Tenta di andare sotto 0
        node.decrementLinkCount();

        // Deve rimanere 0
        Assertions.assertEquals(0, node.getLinkCount());
    }

    @Test
    void typeChecks_correctlyIdentifyFile() {
        INode node = new ConcreteINode(INodeType.FILE);

        Assertions.assertTrue(node.isFile());
        Assertions.assertFalse(node.isDirectory());
        Assertions.assertFalse(node.isSymbolicLink());
    }

    @Test
    void typeChecks_correctlyIdentifyDirectory() {
        INode node = new ConcreteINode(INodeType.DIRECTORY);

        Assertions.assertFalse(node.isFile());
        Assertions.assertTrue(node.isDirectory());
        Assertions.assertFalse(node.isSymbolicLink());
    }

    @Test
    void typeChecks_correctlyIdentifySymlink() {
        INode node = new ConcreteINode(INodeType.SYMLINK);

        Assertions.assertFalse(node.isFile());
        Assertions.assertFalse(node.isDirectory());
        Assertions.assertTrue(node.isSymbolicLink());
    }


    @Test
    void constructor_nullType_throwsException() {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ConcreteINode(null)
        );

        Assertions.assertEquals("INody type cannot be null!", exception.getMessage());
    }

    @Test
    void toString_containsCorrectInfo() {
        INode node = new ConcreteINode(INodeType.FILE);
        // id=0, type=FILE, links=1
        String expected = "INode [id=0, type=FILE, links=1]";

        Assertions.assertEquals(expected, node.toString());
    }
}