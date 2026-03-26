package ch.supsi.fscli.model.inode;

public class SymlinkINode extends INode {
    // Il percorso (path) di destinazione del link simbolico.
    // Non è l'INode effettivo, ma la stringa del percorso.
    private final String targetPath;

    public SymlinkINode(String targetPath) {
        // 1. Chiama il costruttore della classe base INode, specificando il tipo SYMLINK
        super(INodeType.SYMLINK);
        this.targetPath = targetPath;
    }

    // 2. Restituisce il percorso stringa a cui punta il link simbolico
    public String getTargetPath() {
        return this.targetPath;
    }
}