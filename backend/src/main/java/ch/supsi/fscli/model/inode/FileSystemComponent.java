package ch.supsi.fscli.model.inode;

public interface FileSystemComponent {
    int getId();
    int getLinkCount();
    void incrementLinkCount();
    void decrementLinkCount();
    boolean canBeDeleted();
    boolean isDirectory();
    boolean isFile();
    boolean isSymbolicLink();
}