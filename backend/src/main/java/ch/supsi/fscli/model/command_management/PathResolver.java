package ch.supsi.fscli.model.command_management;

import ch.supsi.fscli.model.inode.DirectoryINode;
import ch.supsi.fscli.model.inode.FileSystem;
import ch.supsi.fscli.model.inode.FileSystemComponent;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PathResolver {
    /**
     * Restituisce la directory GENITORE dove eseguire il comando.
     * Esempio: path "a/b/c" -> naviga in "a", poi in "b", e restituisce l'oggetto "b".
     */
    public static DirectoryINode resolve(String path) {
        FileSystem fs = FileSystem.getInstance();

        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("label.pathEmpty");
        }

        // 1. Determinazione della directory di partenza
        DirectoryINode currentDir = path.startsWith("/") ?
                fs.getRoot() :  // Se inizia con '/' parte dalla root
                fs.getCurrentWorkingDirectory();  // altrimenti dalla directory corrente

        // 2. Split dei 'segmenti'
        String[] tokens = path.split("/");

        // 3. Navigazione fino al penultimo elemento (= contenitore)
        for (int i = 0; i < tokens.length - 1; i++) {
            String token = tokens[i];

            // Saltiamo stringhe vuote o auto-riferimenti (capita con path assoluti tipo "/usr")
            if (token.isEmpty() || token.equals(".")) {
                continue;
            }

            // Gestione risalita alla directory parent
            if (token.equals("..")) {
                DirectoryINode parent = currentDir.getParentDirectory();
                if (parent != null) {
                    currentDir = parent;
                }
                continue;
            }

            FileSystemComponent nextComp = currentDir.findEntry(token);

            // Controllo sull'esistenza del nodo
            if (nextComp == null) {
                throw new IllegalArgumentException("No such file or directory: " + token);
            }

            // Controllo tipo (il nodo deve essere directory per poterci entrare)
            if (!(nextComp instanceof DirectoryINode)) {
                throw new IllegalArgumentException("Not a directory: " + token);
            }

            // Mi sposto nella directory trovata
            currentDir = (DirectoryINode) nextComp;

        }

        // 4. Restituzione della directory "contenitore" trovata
        return currentDir;
    }

    /**
     * Utility per estrarre solo il nome finale dal path.
     * Es: "a/b/ciao.txt" -> restituisce "ciao.txt"
     */
    public static String getFileName(String path) {
        // 1. Gestione path speciali o vuoti
        if (path == null || path.isEmpty() || path.equals("/")) {
            return "";
        }

        // Rimuove la slash finale se presente
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        String[] tokens = path.split("/");
        if (tokens.length == 0) return "";

        // 2. Restituisce l'ultimo elemento dopo lo split
        return tokens[tokens.length - 1];
    }

    public static List<String> expandWildcards(List<String> args) throws ParseException {
        List<String> expanded = new ArrayList<>();
        FileSystem fs = FileSystem.getInstance();

        StringBuilder errorMessages = new StringBuilder();
        boolean hasErrors = false;

        // 1. Itera su tutti gli argomenti per espandere le wildcard ('*')
        for (String arg : args) {

            // Se non contiene wildcard, lo accettiamo così com'è
            if (!arg.contains("*")) {
                expanded.add(arg);
                continue;
            }

            // Inizio logica Wildcard
            try {
                String pathPart = "";
                String patternPart = arg;

                // 2. Separa il path dalla parte del pattern (es. /dir/f*.txt -> pathPart=/dir, patternPart=f*.txt)
                int lastSlashIndex = arg.lastIndexOf('/');
                if (lastSlashIndex != -1) {
                    pathPart = arg.substring(0, lastSlashIndex);
                    patternPart = arg.substring(lastSlashIndex + 1);
                }

                // 3. Risoluzione directory di ricerca (dove applicare il pattern)
                DirectoryINode searchDir;

                if (pathPart.isEmpty()) {
                    searchDir = fs.getCurrentWorkingDirectory();
                } else {
                    // Risoluzione del path per trovare la directory
                    DirectoryINode parent = resolve(pathPart);
                    String dirName = getFileName(pathPart);

                    if (dirName.isEmpty() || dirName.equals(".")) {
                        searchDir = parent;
                    } else {
                        // Se pathPart non è una directory, lancia eccezione
                        FileSystemComponent comp = parent.findEntry(dirName);
                        if (comp == null) {
                            throw new IllegalArgumentException("No such file or directory: " + pathPart);
                        }
                        if (comp instanceof DirectoryINode) {
                            searchDir = (DirectoryINode) comp;
                        } else {
                            throw new IllegalArgumentException("Not a directory: " + pathPart);
                        }
                    }
                }

                // 4. Ricerca match applicando il pattern GLOB alla directory
                List<String> matches = expandGlobPattern(patternPart, searchDir);

                if (matches.isEmpty()) {
                    throw new IllegalArgumentException("no matches found for '" + arg + "'");
                }

                // 5. Aggiunta risultati, ricostruendo il path completo
                for (String match : matches) {
                    if (pathPart.isEmpty()) {
                        expanded.add(match);
                    } else {
                        String prefix = pathPart.endsWith("/") ? pathPart : pathPart + "/";
                        expanded.add(prefix + match);
                    }
                }

            } catch (Exception e) {
                // Cattura l'errore ma continua l'espansione degli altri argomenti
                hasErrors = true;
                errorMessages.append(e.getMessage()).append("\n");
            }
        }

        // 6. Se ci sono stati errori durante l'espansione, lancia ParseException con i messaggi
        if (hasErrors) {
            // rimuove l'ultimo accapo
            throw new ParseException(errorMessages.toString().trim(), 0);
        }

        return expanded;
    }


    private static List<String> expandGlobPattern(String pattern, DirectoryINode dir) {
        List<String> matches = new ArrayList<>();

        // 1. Conversione del pattern GLOB in una Regular Expression (Regex)
        StringBuilder regexBuilder = new StringBuilder("^");

        for (char c : pattern.toCharArray()) {
            switch (c) {
                case '*': // * -> .* (zero o più caratteri)
                    regexBuilder.append(".*");
                    break;
                case '?': // ? -> . (un singolo carattere)
                    regexBuilder.append(".");
                    break;
                case '\\': // Gestione caratteri di escape
                    regexBuilder.append("\\").append(c);
                    break;
                default:
                    // Caratteri normali
                    regexBuilder.append(c);
                    break;
            }
        }

        regexBuilder.append("$"); // Assicura che la stringa corrisponda completamente
        String regex = regexBuilder.toString();

        Pattern compiledPattern = Pattern.compile(regex);

        // 2. Applicazione della Regex a tutti i nomi degli elementi nella directory
        for (String name : dir.getEntryNames()) {
            if (name.equals(".") || name.equals("..")) {
                continue;
            }

            if (compiledPattern.matcher(name).matches()) {
                matches.add(name);
            }
        }

        // 3. Ordina i match in ordine alfabetico
        matches.sort(String::compareTo);

        return matches;
    }
}