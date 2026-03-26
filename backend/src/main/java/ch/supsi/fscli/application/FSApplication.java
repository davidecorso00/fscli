package ch.supsi.fscli.application;

import ch.supsi.fscli.business.command_execution.CommandExecutionLogic;
import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.application.persistence.IFSPersistenceApplication;
import ch.supsi.fscli.service.FSConfigurationService;
import ch.supsi.fscli.service.FSPersistenceService;
import ch.supsi.fscli.service.IFSPersistence;
import java.nio.file.Path;

public class FSApplication implements IFSApplication, IFSPersistenceApplication {
    private static FSApplication myself;
    // Istanza del ConfigurationService per le operazioni di base del FileSystem (New, CheckQuit)
    private final FSConfigurationService configurationService = new FSConfigurationService();
    // Istanza per l'esecuzione dei comandi CLI
    private final CommandExecutionLogic commandExecutionLogic = new CommandExecutionLogic();
    // Istanza del PersistenceService per le operazioni di I/O (Save, Open)
    private final IFSPersistence persistenceService = new FSPersistenceService();

    private FSApplication() {}

    public static FSApplication getInstance() {
        if (myself == null) {
            myself = new FSApplication();
        }
        return myself;
    }


    @Override
    public void newFileSystem() {
        // 1. Delega la creazione di un nuovo File System al Configuration Service
        configurationService.newFileSystem();
    }

    @Override
    public boolean checkForQuit(Path lastSavedPath) {
        // 2. Delega la verifica di sicurezza prima dell'uscita al Configuration Service
        return configurationService.checkForQuit(lastSavedPath);
    }

    @Override
    public boolean isRootNull() {
        // 3. Delega il controllo dello stato di inizializzazione del File System
        return configurationService.isRootNull();
    }

    @Override
    public void save() {
        // 4. Delega il salvataggio al Persistence Service
        persistenceService.save();
    }

    @Override
    public CommandResult executeCommand(String commandLine) {
        // 5. Delega l'esecuzione del comando alla Business Logic
        return commandExecutionLogic.executeCommand(commandLine);
    }


    @Override
    public void saveAs(String fileName, Path path) {
        // 6. Delega il salvataggio "Save As" al Persistence Service
        persistenceService.saveAs(path);
    }

    @Override
    public boolean open(Path path, String fileName) {
        // 7. Delega l'apertura/caricamento del File System al Persistence Service
        return persistenceService.open(path);
    }
}