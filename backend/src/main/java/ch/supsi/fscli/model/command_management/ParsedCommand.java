package ch.supsi.fscli.model.command_management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParsedCommand {
    private final String command;
    private final List<String> arguments;
    private final Map<String, Boolean> flag;

    // 1. Costruttore completo (comando, argomenti, flag)
    public ParsedCommand(String command, List<String> arguments, Map<String, Boolean> flag) {
        this.command = command;
        this.arguments = arguments;
        this.flag = flag;
    }

    // 2. Costruttore semplificato (solo comando e argomenti)
    public ParsedCommand(String command, List<String> arguments) {
        this.command = command;
        this.arguments = arguments;
        this.flag = new HashMap<>();
    }

    public String getCommand() {
        return command;
    }

    // 3. Restituisce gli argomenti (come copia difensiva)
    public List<String> getArguments() {
        return new ArrayList<>(arguments);
    }

    // 4. Imposta il valore di un flag
    public void setFlag(String i, boolean showInode) {
        if(flag != null)
            flag.put(i, showInode);
    }

    // 5. Ottiene il valore di un flag (default: false)
    public boolean getFlag(String name) {
        if (flag == null) return false;
        return flag.getOrDefault(name, false);
    }
}