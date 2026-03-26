package ch.supsi.fscli.model.inode;

/**
 * Concetti chiave per l'utilizzo e la gestione degli i-node.
 * - sono identificati tramite un ID univoco incrementale
 * - i file non memorizzano il nome al loro interno
 * - i file non memorizzano la loro parent directory
 * - solo le directory memorizzano la loro parent directory
 */
public abstract class INode implements FileSystemComponent {
    private static int nextINodeId = 0; // Identificatore statico incrementale per l'ID

    private final int id;

    private int linkCount; // Contatore degli Hard Link
    private final INodeType type;

    public INode(INodeType type){
        if (type == null) throw new IllegalArgumentException("INody type cannot be null!");

        // 1. Assegna un ID univoco e incrementa il contatore statico
        this.id = nextINodeId++;
        this.type = type;
        // 2. Inizializza il conteggio dei link a 1 (creazione iniziale)
        this.linkCount = 1;
    }

    @Override
    public final int getId() {
        return id;
    }

    @Override
    public final int getLinkCount() {
        return linkCount;
    }

    @Override
    public final void incrementLinkCount() {
        this.linkCount++;
    }

    @Override
    public final void decrementLinkCount() {
        if (this.linkCount > 0) this.linkCount--;
    }

    // 3. Verifica se l'INode non ha più riferimenti (link)
    public boolean isUnlinked() {
        return this.linkCount == 0;
    }

    // 4. Metodo statico per resettare il contatore ID (usato per caricare un nuovo FS)
    public static void resetNextINodeId() {
        nextINodeId = 0;
    }

    @Override
    public final boolean canBeDeleted() {
        // Un INode può essere eliminato se il contatore link è a zero
        return this.linkCount == 0;
    }

    // Metodi di verifica del tipo
    @Override
    public final boolean isDirectory() {
        return type == INodeType.DIRECTORY;
    }

    @Override
    public final boolean isFile() {
        return type == INodeType.FILE;
    }

    @Override
    public final boolean isSymbolicLink() {
        return type == INodeType.SYMLINK;
    }

    @Override
    public String toString() {
        return String.format("INode [id=%d, type=%s, links=%d]", id, type, linkCount);
    }
}