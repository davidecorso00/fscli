package ch.supsi.fscli.model.inode;

import java.util.Map;
import java.util.HashMap;

public class FileSystem {
    private static FileSystem myself;

    private DirectoryINode root;
    private DirectoryINode currentWorkingDirectory;

    private FileSystem() {
        // 1. Inizializzazione: crea la root e imposta la CWD su root
        this.root = createRoot();
        this.currentWorkingDirectory = root;
    }

    public static FileSystem getInstance() {
        if (myself == null) {
            myself = new FileSystem();
        }

        return myself;
    }

    private DirectoryINode createRoot() {
        // La root è l'unica directory ad avere parent=null
        return new DirectoryINode(null);
    }

    public DirectoryINode getRoot() {
        return root;
    }

    public DirectoryINode getCurrentWorkingDirectory() {
        return currentWorkingDirectory;
    }

    public void setCurrentWorkingDirectory(DirectoryINode directory) {
        if (directory == null) {
            throw new IllegalArgumentException("Current directory cannot be null");
        }
        this.currentWorkingDirectory = directory;
    }

    // 2. Carica un FileSystem deserializzato
    public void loadFromFileSystem(FileSystem other) {
        if (other != null) {
            this.root = other.root;

            // Ricostruisci i riferimenti parent/entries speciali ('.', '..') a partire dalla root
            rebuildFileSystem(this.root, null);

            // Imposta la CWD sulla nuova root
            this.currentWorkingDirectory = this.root;
        }
    }

    // 3. Resetta l'istanza Singleton (usato da `newFileSystem`)
    public static void resetInstance() {
        // Resetta il contatore INode ID prima di creare una nuova istanza
        INode.resetNextINodeId();
        myself = new FileSystem();
        System.out.println(myself.root);
    }

    public static boolean isRootNull() {
        // Usato dalla Business Logic per verificare l'inizializzazione
        return myself == null;
    }

    // 4. Ricostruisce la struttura gerarchica e i riferimenti '.'/'..' dopo la deserializzazione
    private void rebuildFileSystem(DirectoryINode directory, DirectoryINode parent) {
        if (directory == null) return;

        // Imposta il riferimento al parent ricorsivamente
        directory.setParent(parent);

        Map<String, FileSystemComponent> current = directory.getEntries();
        Map<String, FileSystemComponent> newEntries = new HashMap<>();

        // Copia solo le entry dei figli
        for (Map.Entry<String, FileSystemComponent> entry : current.entrySet()) {
            String name = entry.getKey();
            if (name.equals(".") || name.equals("..")) continue;
            newEntries.put(name, entry.getValue());
        }

        // Aggiunge i riferimenti speciali corretti ('.', '..')
        newEntries.put(".", directory);
        if (parent != null) {
            newEntries.put("..", parent);
        } else {
            newEntries.remove(".."); // La root non ha ".."
        }

        directory.setEntries(newEntries);

        // Ricorsione sui figli
        for (Map.Entry<String, FileSystemComponent> entry : newEntries.entrySet()) {
            String name = entry.getKey();
            if (name.equals(".") || name.equals("..")) continue;
            FileSystemComponent comp = entry.getValue();
            if (comp instanceof DirectoryINode) {
                rebuildFileSystem((DirectoryINode) comp, directory);
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        FileSystem fs = (FileSystem) other;

        // 5. L'uguaglianza del FileSystem è determinata dall'uguaglianza ricorsiva della root
        return root.equals(fs.root);

    }

}