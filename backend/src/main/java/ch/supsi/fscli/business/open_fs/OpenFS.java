package ch.supsi.fscli.business.open_fs;

import ch.supsi.fscli.data_access.JsonLoadDataAccess;
import ch.supsi.fscli.data_access.JsonLoadDataAccessInterface;
import ch.supsi.fscli.model.inode.FileSystem;
import ch.supsi.fscli.model.inode.INode;
import java.nio.file.Path;

public class OpenFS implements IOpenFS {

    private static OpenFS myself;
    private final JsonLoadDataAccessInterface loadDataAccess = new JsonLoadDataAccess();

    private OpenFS() {}

    public static OpenFS getInstance() {
        if (myself == null) {
            myself = new OpenFS();
        }
        return myself;
    }

    @Override
    public boolean open(Path path) {
        // 1. Controllo validità input
        if (path == null) {
            System.err.println("[OpenFS] Errore: path non può essere null");
            return false;
        }

        try {
            System.out.println("[OpenFS] Caricando file: " + path);

            // 2. Reset degli identificatori degli INode per evitare duplicazioni
            INode.resetNextINodeId();
            System.out.println("[OpenFS] INode IDs resettati");

            // 3. Caricamento dell'oggetto FileSystem dal JSON
            FileSystem loadedFS = loadDataAccess.loadFromFile(path, FileSystem.class);
            System.out.println("[OpenFS] FileSystem caricato dal JSON: " + loadedFS);

            if (loadedFS == null) {
                System.err.println("[OpenFS] Errore: FileSystem caricato è null");
                return false;
            }

            // 4. Aggiornamento dell'istanza Singleton globale con i dati caricati
            FileSystem globalInstance = FileSystem.getInstance();
            globalInstance.loadFromFileSystem(loadedFS);
            System.out.println("[OpenFS] Singleton FileSystem aggiornato");
            System.out.println("[OpenFS] Root del singleton: " + FileSystem.getInstance().getRoot());

            return true;

        } catch (Exception e) {
            System.err.println("[OpenFS] Errore durante il caricamento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}