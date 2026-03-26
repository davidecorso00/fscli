package ch.supsi.fscli.model.command_management;

import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.data_access.CommandResultStatus;
import ch.supsi.fscli.model.commands.*;

import java.util.HashMap;
import java.util.Map;

public class CommandExecutor {
    private static CommandExecutor myself;

    private final Map<String, ICommand> commands;

    private CommandExecutor() {
        commands = new HashMap<>();

        // 1. Inizializzazione della mappa dei comandi
        commands.put("pwd", new PwdCommand());
        commands.put("help", new HelpCommand());
        commands.put("clear", new ClearCommand());
        commands.put("cd", new CdCommand());
        commands.put("touch", new TouchCommand());
        commands.put("mkdir", new MkdirCommand());
        commands.put("rm", new RmCommand());
        commands.put("rmdir", new RmDirCommand());
        commands.put("mv", new MvCommand());
        commands.put("ls", new LsCommand());
        commands.put("ln", new LnCommand());
    }

    public static CommandExecutor getInstance() {
        if (myself == null) {
            myself = new CommandExecutor();
        }
        return myself;
    }

    public CommandResult executeCommand(String inputLine) {
        try {
            // 2. Parsing dell'input e risoluzione del comando
            ParsedCommand cmdParsed = CommandParser.getInstance().parse(inputLine);

            ICommand command = commands.get(cmdParsed.getCommand());

            // 3. Esecuzione del comando e ritorno del risultato
            return command.execute(cmdParsed);

        } catch (LocalizedParseException e) {
            // 4. Gestione eccezioni di parsing con messaggio localizzato
            return new CommandResult(
                    e.getLocalizedMessage(),
                    CommandResultStatus.ERROR,
                    e.getArgs()
            );
        } catch (Exception e) {
            // 5. Gestione eccezioni generiche
            return new CommandResult(
                    e.getMessage(),
                    CommandResultStatus.ERROR
            );
        }
    }
}