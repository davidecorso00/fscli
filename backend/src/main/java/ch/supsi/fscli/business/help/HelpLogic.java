package ch.supsi.fscli.business.help;

import ch.supsi.fscli.model.CommandsList;
import ch.supsi.fscli.model.Manual;

public class HelpLogic implements IHelpLogic {

    private static HelpLogic myself;


    public static HelpLogic getInstance() {
        if (myself == null) {
            myself = new HelpLogic();
        }
        return myself;
    }

    @Override
    public String getHelp() {
        // 1. Recupero delle sorgenti dati (Model)
        CommandsList commands = CommandsList.getInstance();
        Manual manual = Manual.getInstance();

        // 2. Composizione dell'output finale
        // Unisce il testo descrittivo del manuale con l'elenco dei comandi disponibili
        return manual.getManual() + commands.getAllCommands();
    }
}