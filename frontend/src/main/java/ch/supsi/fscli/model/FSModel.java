package ch.supsi.fscli.model;


import ch.supsi.fscli.application.FSApplication;
import ch.supsi.fscli.application.IFSApplication;
import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.application.persistence.IFSPersistenceApplication;
import ch.supsi.fscli.model.inode.FSStatus;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FSModel extends AbstractModel implements IFSConfigurationModel, IFSPersistenceModel {

    private static IFSApplication fsConfiguration = FSApplication.getInstance();
    private static IFSPersistenceApplication fsPersistence = FSApplication.getInstance();
    private static FSModel myself;
    private FSStatus status;

    private final List<FSModelObserver> observers = new ArrayList<>();

    private FSModel() {
        super();
        this.status = FSStatus.WELCOME;
    }



    public static FSModel getInstance() {
        if (myself == null) {
            myself = new FSModel();
        }
        return myself;
    }

    public void setStatus(FSStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = newStatus;
        notifyObservers();
    }

    public FSStatus getStatus() {
        return status;
    }

    @Override
    public void newFileSystem() {
        fsConfiguration.newFileSystem();
        setStatus(FSStatus.NEW_FILESYSTEM);
    }

    @Override
    public boolean checkForQuit(Path lastSavedPath) {
        return fsConfiguration.checkForQuit(lastSavedPath);
    }

    @Override
    public boolean isRootNull() {
        return fsConfiguration.isRootNull();
    }

    @Override
    public CommandResult executeCommand(String commandLine) {
        return fsConfiguration.executeCommand(commandLine);
    }
    @Override
    public void save() {
        fsPersistence.save();
    }
    @Override
    public void saveAs(String fileName, Path filePath) {
        setStatus(FSStatus.SAVE);
        fsPersistence.saveAs(fileName, filePath);
    }

    @Override
    public boolean open(Path filePath, String fileName) {
        if (fsPersistence.open(filePath, fileName)) {
            setStatus(FSStatus.OPEN);
            return true;
        } else {
            setStatus(FSStatus.OPEN_ERROR);
            return false;
        }
    }

    public void addObserver(FSModelObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers() {
        for (FSModelObserver observer : observers) {
            observer.updateStatus(this.status);
        }
    }
}
