package ch.supsi.fscli.business.quit;

import ch.supsi.fscli.data_access.JsonLoadDataAccess;
import ch.supsi.fscli.data_access.JsonLoadDataAccessInterface;
import ch.supsi.fscli.model.inode.FileSystem;

import java.io.IOException;
import java.nio.file.Path;

public class Quit implements IQuit {

    private static Quit myself;
    private static final JsonLoadDataAccessInterface loadDataAccess = new JsonLoadDataAccess();

    private Quit() {}

    public static Quit getInstance() {
        if (myself == null) {
            myself = new Quit();
        }
        return myself;
    }
    @Override
    public boolean checkForQuit( Path lastSavedPath) {
        // 1. Controllo caso FileSystem non inizializzato
        if(FileSystem.isRootNull())
            return  true;

        // 2. Controllo caso FileSystem vuoto e mai salvato
        // Se non c'è un path di salvataggio e il FS è vuoto, è sicuro uscire
        if(lastSavedPath == null && FileSystem.getInstance().getRoot().isEmpty())
            return true;

        // 3. Controllo caso FileSystem NON vuoto e mai salvato
        // Se non c'è un path di salvataggio ma il FS contiene dati, NON è sicuro uscire
        if(lastSavedPath == null && !FileSystem.getInstance().getRoot().isEmpty())
            return false;

        // 4. Caricamento dell'ultimo stato salvato
        FileSystem lastSaved;
        try {
            lastSaved = loadDataAccess.loadFromFile(lastSavedPath, FileSystem.class);
        } catch (IOException e) {
            // In caso di errore di caricamento, si assume che NON sia sicuro uscire
            System.err.println("[Quit] Errore durante il caricamento del file di salvataggio: " + e.getMessage());
            return false;
        }

        // 5. Confronto tra lo stato attuale e l'ultimo stato salvato
        // Se i due FileSystem sono identici, è sicuro uscire (true); altrimenti, false
        return FileSystem.getInstance().equals(lastSaved);

    }
}