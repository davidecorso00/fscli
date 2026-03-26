package ch.supsi.fscli.data_access;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonSaveDataAccess implements JsonSaveDataAccessInterface {

    private final ObjectMapper objectMapper;

    public JsonSaveDataAccess() {
        this.objectMapper = new ObjectMapper();

        // Attivazione del "default typing" (necessario per salvare classi polimorfiche come gli INode)
        this.objectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Object.class)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );
    }

    @Override
    public void saveToFile(Path path, Object objectToSave) throws IOException {
        // 1. Assicura che la directory di destinazione esista
        ensureDirectoryExists(path.getParent());

        // 2. Scrittura dell'oggetto serializzato sul file
        this.objectMapper.writeValue(new File(path.toUri()), objectToSave);
        System.out.println("[JsonSaveDataAccess] Salvato con successo: " + path);
    }

    @Override
    public void ensureDirectoryExists(Path dir) throws IOException {
        if (dir == null) return;

        // 3. Verifica esistenza e tipo
        if (Files.exists(dir)) {
            if (!Files.isDirectory(dir)) {
                throw new IOException("[JsonSaveDataAccess] Il percorso esiste ma non è una directory: " + dir);
            }
            return;
        }

        // 4. Se non esiste, lancia eccezione (la creazione dovrebbe avvenire nel livello Business/Service)
        throw new IOException("[JsonSaveDataAccess] Directory non esistente: " + dir);
    }
}