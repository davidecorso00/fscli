package ch.supsi.fscli.business.save_fs;

import ch.supsi.fscli.data_access.JsonSaveDataAccess;
import ch.supsi.fscli.data_access.JsonSaveDataAccessInterface;
import ch.supsi.fscli.model.inode.FileSystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SaveFS implements ISaveFS {

    private static SaveFS myself;
    private final JsonSaveDataAccessInterface saveDataAccess = new JsonSaveDataAccess();
    private Path lastSavedPath = null;

    private SaveFS() {}

    public static SaveFS getInstance() {
        if (myself == null) {
            myself = new SaveFS();
        }
        return myself;
    }

    @Override
    public void save(Path filepath) throws IOException {
        // 1. Validazione dell'input
        if (filepath == null) {
            throw new IllegalArgumentException("Filepath cannot be null");
        }

        // 2. Assicura che la directory di destinazione esista
        Path dir = filepath.getParent();
        if (dir != null && !Files.exists(dir)) {
            saveDataAccess.ensureDirectoryExists(dir);
        }

        System.out.println("[SaveFS] Salvando filesystem: " + filepath);

        // 3. Scrittura effettiva del FileSystem su file
        saveDataAccess.saveToFile(filepath, FileSystem.getInstance());

        // 4. Aggiornamento del path di salvataggio
        this.lastSavedPath = filepath;
        System.out.println("[SaveFS] Filesystem salvato con successo");
    }

    @Override
    public void saveAs(Path filepath) throws IOException {
        // saveAs delega a save
        if (filepath == null) {
            throw new IllegalArgumentException("Filepath cannot be null");
        }
        save(filepath);
    }


    // --- Metodi Getter/Setter per il path di salvataggio ---

    @Override
    public Path getLastSavedPath() {
        return lastSavedPath;
    }

    @Override
    public void setLastSavedPath(Path path) {
        this.lastSavedPath = path;
    }
}