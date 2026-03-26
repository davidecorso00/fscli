package ch.supsi.fscli.model.inode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;

public class DirectoryINode extends INode {
    // Mappa dei componenti del file system contenuti, con la chiave = nome
    private Map<String, FileSystemComponent> entries;
    // Riferimento al genitore (usato per navigazione e ricostruzione dopo deserializzazione)
    private DirectoryINode parent;

    // Costruttore per Jackson (deserializzazione)
    public DirectoryINode() {
        super(INodeType.DIRECTORY);
        this.entries = new HashMap<>();
        this.parent = null;
    }

    // 1. Costruttore con genitore (usato in fase di creazione)
    public DirectoryINode(DirectoryINode parent) {
        super(INodeType.DIRECTORY);
        this.entries = new HashMap<>();
        this.parent = parent;

        // Aggiunge le entry speciali "." e ".."
        this.entries.put(".", this);
        if (parent != null) {
            this.entries.put("..", parent);
        }
    }

    // Ignora questo campo durante la serializzazione JSON
    @JsonIgnore
    public DirectoryINode getParentDirectory() {
        return this.parent;
    }

    // 2. Aggiunge una nuova entry (file o dir)
    public boolean addEntry(String name, FileSystemComponent component) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        if (name.equals(".") || name.equals("..")) {
            return false;
        }

        if (entries.containsKey(name)) {
            return false;
        }

        entries.put(name, component);
        return true;
    }

    // 3. Trova una entry (gestisce anche "." e "..")
    public FileSystemComponent findEntry(String name) {
        if (name.equals(".")) {
            return this;
        }

        if (name.equals("..")) {
            // Ritorna il genitore se esiste, altrimenti la directory stessa (per la root)
            return parent != null ? parent : this;
        }

        // Cerca nelle entry normali
        return entries.get(name);
    }

    // 4. Rimuove una entry
    public FileSystemComponent removeEntry(String name) {
        if (name == null || name.equals(".") || name.equals("..")) {
            return null;
        }

        // Rimuove l'entry dalla mappa
        FileSystemComponent removedComponent = entries.remove(name);

        if (removedComponent != null) {
            // Decrementa il contatore di link sull'INode rimosso
            removedComponent.decrementLinkCount();
        }

        return removedComponent;
    }

    @JsonIgnore
    public DirectoryINode getParentViaEntry() {
        FileSystemComponent parentEntry = this.entries.get("..");
        return (DirectoryINode) parentEntry;
    }

    // 5. Getter per serializzazione JSON (esclude "." e "..")
    @JsonProperty("entries")
    public Map<String, FileSystemComponent> getEntriesForJson() {
        Map<String, FileSystemComponent> filtered = new HashMap<>();
        for (Map.Entry<String, FileSystemComponent> entry : entries.entrySet()) {
            if (!entry.getKey().equals(".") && !entry.getKey().equals("..")) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }
        return filtered;
    }

    // 6. Setter per deserializzazione JSON (ricostruisce "." e ".." in base al parent)
    @JsonProperty("entries")
    public void setEntriesFromJson(Map<String, FileSystemComponent> entries) {
        this.entries = new HashMap<>();
        if (entries != null) {
            this.entries.putAll(entries);
        }
        // Aggiunge le entry speciali dopo la deserializzazione
        this.entries.put(".", this);
        if (this.parent != null) {
            this.entries.put("..", this.parent);
        }
    }

    // Metodi Getter/Setter di servizio
    @JsonIgnore
    public Map<String, FileSystemComponent> getEntries() {
        return new HashMap<>(entries);
    }

    public void setEntries(Map<String, FileSystemComponent> entries) {
        this.entries = entries != null ? entries : new HashMap<>();
    }

    public void setParent(DirectoryINode parent) {
        this.parent = parent;
    }

    @JsonIgnore
    public Set<String> getEntryNames() {
        return new HashSet<>(entries.keySet());
    }

    // 7. Restituisce solo i figli (escludendo "." e "..")
    @JsonIgnore
    public Collection<FileSystemComponent> getChildren() {
        return entries.entrySet().stream()
                .filter(e -> !e.getKey().equals(".") && !e.getKey().equals(".."))
                .map(Map.Entry::getValue)
                .toList();
    }

    // 8. Restituisce tutti i nomi di entry, ordinati
    @JsonIgnore
    public List<String> getAllEntryNames() {
        return entries.keySet().stream()
                .sorted()
                .toList();
    }

    // 9. Calcola il percorso assoluto della directory
    public String getAbsolutePath() {
        // Caso root
        if (parent == null) {
            return "/";
        }

        List<String> parts = new ArrayList<>();
        DirectoryINode current = this;

        // Risale l'albero fino alla root (parent == null)
        while (current.parent != null) {
            DirectoryINode parentDir = current.parent;

            // Trova il nome di 'current' all'interno della directory 'parentDir'
            for (Map.Entry<String, FileSystemComponent> entry : parentDir.getEntries().entrySet()) {
                if (entry.getValue() == current) {
                    parts.add(entry.getKey());
                    break;
                }
            }

            current = parentDir;
        }

        Collections.reverse(parts);
        return "/" + String.join("/", parts);
    }

    @JsonIgnore
    public boolean isEmpty() {
        return getChildren().isEmpty();
    }

    @Override
    public String toString() {
        return String.format("DirectoryINode [id=%d, entries=%d, children=%d, parent=%s]",
                getId(), entries.size(), getChildren().size(), parent != null ? parent.getId() : "null");
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof DirectoryINode that)) return false;

        // 10. Logica di uguaglianza ricorsiva: confronta le entry dei figli
        Map<String, FileSystemComponent> thisFilteredEntries = this.getEntriesForJson();
        Map<String, FileSystemComponent> thatFilteredEntries = that.getEntriesForJson();

        if (thisFilteredEntries.size() != thatFilteredEntries.size()) return false;

        for (String key : thisFilteredEntries.keySet()) {
            FileSystemComponent thisComp = thisFilteredEntries.get(key);
            FileSystemComponent thatComp = thatFilteredEntries.get(key);

            // Se i componenti figli non sono uguali (ricorsivamente), la directory non è uguale
            if (!thisComp.equals(thatComp)) {
                return false;
            }
        }

        return true;

    }
}