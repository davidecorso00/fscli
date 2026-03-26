package ch.supsi.fscli.model.commands;

import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.data_access.CommandResultStatus;
import ch.supsi.fscli.model.command_management.CommandInfo;
import ch.supsi.fscli.model.command_management.ParsedCommand;
import ch.supsi.fscli.model.command_management.PathResolver;
import ch.supsi.fscli.model.inode.DirectoryINode;
import ch.supsi.fscli.model.inode.FileSystemComponent;

import java.text.ParseException;
import java.util.List;

@CommandInfo(
        name = "mv",
        signatureKey = "command.mv.signature",
        descriptionKey = "command.mv.description"
)
public class MvCommand implements ICommand {

    @Override
    public ParsedCommand parse(List<String> args) throws ParseException {
        if (args.isEmpty()) {
            throw new ParseException("label.mv.usage", 0);
        }

        List<String> expandedArgs = PathResolver.expandWildcards(args);

        if (expandedArgs.size() < 2) {
            throw new ParseException("label.mv.missingDest", 0);
        }

        return new ParsedCommand("mv", expandedArgs);
    }

    @Override
    public CommandResult execute(ParsedCommand cmd) {
        List<String> args = cmd.getArguments();
        String destArg = args.get(args.size() - 1); // L'ultimo è la destinazione
        List<String> sources = args.subList(0, args.size() - 1); // Tutti gli altri sono sorgenti

        boolean isDestDirectory = false;

        // Pre-verifica se la destinazione esiste ed è una directory
        try {
            DirectoryINode parent = PathResolver.resolve(destArg);
            String fileName = PathResolver.getFileName(destArg);

            if (parent != null) {
                // Se il path termina in una directory o ".", "..", è una destinazione directory
                if (fileName.isEmpty() || fileName.equals(".") || fileName.equals("..")) {
                    isDestDirectory = true;
                } else {
                    FileSystemComponent destComponent = parent.findEntry(fileName);
                    if (destComponent instanceof DirectoryINode) {
                        isDestDirectory = true;
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            // Se il path è invalido, mv fallirà dopo, ma qui non possiamo trattarlo come directory
            return new CommandResult("label.mv.invalidDestPath", CommandResultStatus.ERROR, destArg);
        }

        // Validazione multi-source: se ci sono più sorgenti, la destinazione DEVE essere una directory
        if (sources.size() > 1 && !isDestDirectory) {
            return new CommandResult("label.mv.targetNotDir", CommandResultStatus.ERROR, destArg);
        }

        // Esecuzione Iterativa (sposta una entry alla volta)
        for (String sourcePath : sources) {
            String targetPathForThisFile;

            // Determina il path di destinazione finale (rinominazione o spostamento in directory)
            if (isDestDirectory) {
                String sourceName = PathResolver.getFileName(sourcePath);
                String cleanDest = destArg.endsWith("/") ? destArg : destArg + "/";
                targetPathForThisFile = cleanDest + sourceName;
            } else {
                targetPathForThisFile = destArg; // Rinominazione
            }

            // Esegue lo spostamento di una singola entry
            CommandResult res = moveSingleEntry(sourcePath, targetPathForThisFile);
            if (res.getStatus() == CommandResultStatus.ERROR) {
                return res; // Interrompe e restituisce il primo errore
            }
        }

        return new CommandResult("", CommandResultStatus.SUCCESS);
    }

    private CommandResult moveSingleEntry(String sourcePath, String destPath) {
        try {
            // --- 1. RISOLUZIONE SOURCE ---
            DirectoryINode sourceParent = PathResolver.resolve(sourcePath);
            String sourceName = PathResolver.getFileName(sourcePath);

            if (sourceParent == null || sourceName.isEmpty()) {
                return new CommandResult("label.mv.invalidSource", CommandResultStatus.ERROR);
            }

            // PROTEZIONE CRITICA: Blocca . e ..
            if (sourceName.equals(".") || sourceName.equals("..")) {
                return new CommandResult("label.mv.cannotMoveSpecial", CommandResultStatus.ERROR, sourceName);
            }

            FileSystemComponent sourceComponent = sourceParent.findEntry(sourceName);
            if (sourceComponent == null) {
                return new CommandResult("label.mv.noSuchFile", CommandResultStatus.ERROR, sourcePath);
            }

            // --- 2. RISOLUZIONE DESTINATION ---
            DirectoryINode destParent = PathResolver.resolve(destPath);
            String destName = PathResolver.getFileName(destPath);

            if (destParent == null) {
                return new CommandResult("label.mv.invalidDest", CommandResultStatus.ERROR);
            }
            // Se la destinazione è una directory (termina con /), usa il nome della sorgente
            if (destName.isEmpty() || destName.equals(".")) {
                destName = sourceName;
            }

            // PROTEZIONE CRITICA: Blocca .. come nome finale della destinazione
            if (destName.equals("..")) {
                return new CommandResult("label.mv.invalidDestName", CommandResultStatus.ERROR, "..");
            }

            FileSystemComponent destExisting = destParent.findEntry(destName);

            // --- 3. CONTROLLO CICLI ---
            // Se la sorgente è una directory, verifica che la destinazione non sia al suo interno
            if (sourceComponent instanceof DirectoryINode) {
                DirectoryINode check = destParent;
                while (check != null) {
                    if (check == sourceComponent) {
                        return new CommandResult("label.mv.intoItself", CommandResultStatus.ERROR, sourcePath, destPath);
                    }
                    check = check.getParentDirectory();
                }
            }

            // --- 4. GESTIONE CONFLITTI e SOVRASCRITTURA ---
            if (destExisting != null) {
                if (destExisting == sourceComponent) {
                    // Spostare un file su se stesso (rinominazione non effettuata)
                    return new CommandResult("label.mv.sameFile", CommandResultStatus.ERROR, sourcePath, destPath);
                }

                if (destExisting.isDirectory()) {
                    // mv non può sovrascrivere una directory esistente con un'altra cosa
                    return new CommandResult("label.mv.overwriteDir", CommandResultStatus.ERROR, destName);
                } else {
                    if (sourceComponent instanceof DirectoryINode) {
                        // Non si può sovrascrivere un file con una directory
                        return new CommandResult("label.mv.cannotOverwriteNonDir", CommandResultStatus.ERROR, destPath);
                    }
                    // Sovrascrive il file esistente (rimuovendolo prima)
                    destParent.removeEntry(destName);
                }
            }

            // --- 5. MOVIMENTO EFFETTIVO (RIMUOVI -> AGGIUNGI) ---
            FileSystemComponent removed = sourceParent.removeEntry(sourceName);
            if (removed == null) {
                return new CommandResult("label.mv.failedRemove", CommandResultStatus.ERROR, sourcePath);
            }

            boolean added = destParent.addEntry(destName, removed);
            if (!added) {
                // Se l'aggiunta fallisce, ripristina l'entry nella sorgente
                sourceParent.addEntry(sourceName, removed);
                return new CommandResult("label.mv.failedAdd", CommandResultStatus.ERROR, destPath);
            }

            return new CommandResult("", CommandResultStatus.SUCCESS);

        } catch (IllegalArgumentException e) {
            return new CommandResult("label.mv.pathError", CommandResultStatus.ERROR, e.getMessage());
        }
    }
}