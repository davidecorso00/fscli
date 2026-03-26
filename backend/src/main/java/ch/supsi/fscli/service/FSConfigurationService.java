package ch.supsi.fscli.service;

import ch.supsi.fscli.business.new_filesystem.INewFileSystem;
import ch.supsi.fscli.business.new_filesystem.NewFileSystem;
import ch.supsi.fscli.business.quit.IQuit;
import ch.supsi.fscli.business.quit.Quit;

import java.nio.file.Path;

public class FSConfigurationService implements IFSConfiguration {
    // Dipendenza dalla Business Logic per la creazione di un nuovo FS
    private final INewFileSystem newFileSystem = NewFileSystem.getInstance();
    // Dipendenza dalla Business Logic per la verifica dell'uscita
    private final IQuit quit = Quit.getInstance();

    @Override
    public void newFileSystem() {
        // 1. Delega la creazione di un nuovo File System alla Business Logic
        newFileSystem.newFileSystem();
    }

    @Override
    public boolean checkForQuit(Path lastSavedPath) {
        // 2. Delega la logica di controllo sull'uscita alla Business Logic
        return quit.checkForQuit(lastSavedPath);
    }

    public boolean isRootNull(){
        // 3. Delega il controllo dello stato di inizializzazione alla Business Logic
        return newFileSystem.isRootNull();
    }
}