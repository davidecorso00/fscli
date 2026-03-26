package ch.supsi.fscli.model.inode;

public class FileINode extends INode {

    public FileINode() {
        super(INodeType.FILE);
    }

    @Override
    public String toString() {
        // Formattazione specifica per i file
        return String.format("FileINode [id=%d, links=%d]", getId(), getLinkCount());
    }
}