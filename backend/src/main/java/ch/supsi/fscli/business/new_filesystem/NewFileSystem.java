package ch.supsi.fscli.business.new_filesystem;

import ch.supsi.fscli.model.inode.FileSystem;

public class NewFileSystem implements INewFileSystem {

    private static NewFileSystem myself;

    private NewFileSystem() {}

    public static NewFileSystem getInstance() {
        if (myself == null) {
            myself = new NewFileSystem();
        }
        return myself;
    }

    @Override
    public void newFileSystem() {
        FileSystem.resetInstance();
    }

    public boolean isRootNull(){
        return FileSystem.isRootNull();
    }
}