package ch.supsi.fscli.service;

import ch.supsi.fscli.business.open_fs.IOpenFS;
import ch.supsi.fscli.business.open_fs.OpenFS;
import ch.supsi.fscli.business.save_fs.ISaveFS;
import ch.supsi.fscli.business.save_fs.SaveFS;

import java.io.IOException;
import java.nio.file.Path;

public class FSPersistenceService implements IFSPersistence {

    private final ISaveFS saveFS = SaveFS.getInstance();
    private final IOpenFS openFS = OpenFS.getInstance();
    // Traccia il path del file correntemente aperto/salvato
    private Path currentFilePath = null;

    @Override
    public void save() {
        // 1. Controllo: se non c'è un file path noto, richiede saveAs
        if (currentFilePath == null) {
            System.err.println("[FSPersistenceService] Errore: nessun file da salvare. Usa saveAs()");
            return;
        }
        try {
            // 2. Delega il salvataggio alla Business Logic
            saveFS.save(currentFilePath);
        } catch (IOException e) {
            System.err.println("[FSPersistenceService] Errore salvataggio: " + e.getMessage());
        }
    }

    @Override
    public void saveAs(Path filePath) {
        // 3. Validazione del path
        if (filePath == null) {
            System.err.println("[FSPersistenceService] Errore: filePath non può essere null");
            return;
        }
        try {
            // 4. Delega il salvataggio "as" e aggiorna il path corrente
            saveFS.saveAs(filePath);
            this.currentFilePath = filePath;
        } catch (IOException e) {
            System.err.println("[FSPersistenceService] Errore saveAs: " + e.getMessage());
        }
    }

    @Override
    public boolean open(Path filePath) {
        // 5. Validazione del path
        if (filePath == null) {
            System.err.println("[FSPersistenceService] Errore: filePath non può essere null");
            return false;
        }
        // 6. Delega l'apertura alla Business Logic
        boolean success = openFS.open(filePath);
        // 7. Se l'apertura ha successo, aggiorna il path corrente
        if (success) {
            this.currentFilePath = filePath;
        }
        return success;
    }

    @Override
    public Path getCurrentFilePath() {
        return currentFilePath;
    }

    @Override
    public void setCurrentFilePath(Path filePath) {
        this.currentFilePath = filePath;
    }
}