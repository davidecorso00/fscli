package ch.supsi.fscli.model;

import ch.supsi.fscli.model.inode.FSStatus;

public interface FSModelObserver {
    void updateStatus(FSStatus status);
}

