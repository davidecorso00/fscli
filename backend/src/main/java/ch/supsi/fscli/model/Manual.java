package ch.supsi.fscli.model;

public class Manual {

    private static Manual myself;

    private final String manual;

    private Manual() {
        // 1. Il contenuto del manuale (attualmente vuoto)
        this.manual = """ 
                """;
    }

    public static Manual getInstance() {
        if (myself == null) {
            myself = new Manual();
        }
        return myself;
    }

    // 2. Restituisce il testo del manuale
    public String getManual() {
        return manual;
    }
}