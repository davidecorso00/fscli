package ch.supsi.fscli.data_access;

public class CommandResult {
    // La chiave di traduzione per il messaggio utente (es. "label.success")
    private final String messageKey;
    private final CommandResultStatus status;
    // Argomenti dinamici per la formattazione del messaggio (es. il nome del file)
    private final Object[] args;

    // 1. Costruttore senza argomenti dinamici
    public CommandResult(String messageKey, CommandResultStatus status) {
        this(messageKey, status, (Object[]) null);
    }

    // 2. Costruttore completo con argomenti dinamici
    public CommandResult(String messageKey, CommandResultStatus status, Object... args) {
        this.messageKey = messageKey;
        this.status = status;
        this.args = args;
    }

    public String getMessage() {
        return messageKey;
    }

    public CommandResultStatus getStatus() {
        return status;
    }

    public Object[] getArgs() {
        return args;
    }

    // 3. Verifica se lo stato richiede la pulizia della console
    public boolean shouldClearTerminal() {
        return status.equals(CommandResultStatus.valueOf("CLEAR_OUTPUT"));
    }
}