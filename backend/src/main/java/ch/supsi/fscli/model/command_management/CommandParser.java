package ch.supsi.fscli.model.command_management;

import ch.supsi.fscli.model.commands.*;

import java.text.ParseException;
import java.util.*;

public class CommandParser {
    private static CommandParser myself;

    // Mappa che associa il nome del comando (stringa) all'istanza ICommand
    private final Map<String, ICommand> commands;

    private CommandParser() {
        commands = new HashMap<>();

        // 1. Inizializzazione di tutte le istanze dei comandi
        commands.put("pwd", new PwdCommand());
        commands.put("help", new HelpCommand());
        commands.put("clear", new ClearCommand());
        commands.put("cd", new CdCommand());
        commands.put("touch", new TouchCommand());
        commands.put("mkdir", new MkdirCommand());
        commands.put("rm", new RmCommand());
        commands.put("rmdir", new RmDirCommand());
        commands.put("ls", new LsCommand());
        commands.put("mv", new MvCommand());
        commands.put("ln", new LnCommand());
    }

    public static CommandParser getInstance() {
        if (myself == null) {
            myself = new CommandParser();
        }
        return myself;
    }

    // 2. Suddivide la riga di input in token (parole separate da spazio)
    private String[] tokenize(String input) {
        StringTokenizer tokenizer = new StringTokenizer(input.trim());
        String[] tokens = new String[tokenizer.countTokens()];
        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            tokens[i++] = tokenizer.nextToken();
        }
        return tokens;
    }

    public ParsedCommand parse(String input) throws ParseException {
        String[] tokens = tokenize(input);

        // 3. Gestione input vuoto
        if (tokens.length == 0) {
            throw new ParseException("label.commandEmpty", 0);
        }

        String commandName = tokens[0];
        // Gli argomenti sono tutti i token successivi al primo
        List<String> args = Arrays.asList(Arrays.copyOfRange(tokens, 1, tokens.length));

        ICommand command = commands.get(commandName);

        // 4. Controllo comando non trovato
        if (command == null) {
            // Lancia un'eccezione con chiave localizzata
            throw new LocalizedParseException("label.command.notFound",commandName);
        }

        // 5. Delega il parsing degli argomenti al comando specifico
        // (Ogni comando gestisce la propria sintassi)
        return command.parse(args);
    }
}