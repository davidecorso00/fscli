package ch.supsi.fscli.model;

import ch.supsi.fscli.business.translations.TranslationsLogic;
import ch.supsi.fscli.model.command_management.CommandInfo;
import ch.supsi.fscli.model.commands.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CommandsList {

    private static CommandsList myself;

    // Lista delle classi dei comandi
    private final List<Class<? extends ICommand>> commandClasses;

    private CommandsList() {
        this.commandClasses = new ArrayList<>();
        registerCommands();
    }

    public static CommandsList getInstance() {
        if (myself == null) {
            myself = new CommandsList();
        }
        return myself;
    }

    // 1. Registra solo la classe dei comandi tramite reflection
    private void registerCommands() {
        commandClasses.add(PwdCommand.class);
        commandClasses.add(TouchCommand.class);
        commandClasses.add(MkdirCommand.class);
        commandClasses.add(CdCommand.class);
        commandClasses.add(RmCommand.class);
        commandClasses.add(RmDirCommand.class);
        commandClasses.add(MvCommand.class);
        commandClasses.add(LnCommand.class);
        commandClasses.add(LsCommand.class);
        commandClasses.add(ClearCommand.class);
        commandClasses.add(HelpCommand.class);
    }

    public String getAllCommands() {
        StringBuilder sb = new StringBuilder();
        TranslationsLogic translator = TranslationsLogic.getInstance();


        sb.append("Commands:\n");

        commandClasses.stream()
                // 2. Filtra solo le classi annotate con CommandInfo
                .filter(clazz -> clazz.isAnnotationPresent(CommandInfo.class))
                // 3. Mappa l'oggetto Class all'oggetto CommandInfo
                .map(clazz -> clazz.getAnnotation(CommandInfo.class))
                // 4. Ordina i comandi per nome
                .sorted(Comparator.comparing(CommandInfo::name))
                // 5. Per ogni CommandInfo, genera la stringa di aiuto
                .forEach(info -> {
                    // 5a. Traduci la descrizione usando la chiave
                    String translatedDesc = translator.translate(info.descriptionKey());

                    // 5b. Traduci la signature usando la chiave
                    String translatedSig = translator.translate(info.signatureKey());

                    // 5c. Componi la stringa finale (Signature / Name: Description)
                    sb.append(translatedSig)
                            .append(" / ")
                            .append(info.name())
                            .append(": ")
                            .append(translatedDesc)
                            .append("\n");
                });

        return sb.toString();
    }
}