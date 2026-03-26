package ch.supsi.fscli.model.inode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileINodeTest {

    @BeforeEach
    void setUp() {
        INode.resetNextINodeId();
    }


    @Test
    void testToString() {
        FileINode file = new FileINode();
        String expected = "FileINode [id=0, links=1]";
        assertEquals(expected, file.toString());
    }
}