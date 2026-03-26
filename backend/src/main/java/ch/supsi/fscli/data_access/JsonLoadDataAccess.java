package ch.supsi.fscli.data_access;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class JsonLoadDataAccess implements JsonLoadDataAccessInterface {

    private final ObjectMapper objectMapper;

    public JsonLoadDataAccess() {
        this.objectMapper = new ObjectMapper();

        // Configurazione per ignorare proprietà JSON sconosciute durante la deserializzazione
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Attivazione del "default typing" per supportare la deserializzazione di gerarchie di classi
        // (necessario per caricare INode, DirectoryINode, FileINode da una singola classe base)
        this.objectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Object.class)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );
    }

    @Override
    public <T> T loadFromFile(Path path, Class<T> clazz) throws IOException {
        try {
            System.out.println("[JsonLoadDataAccess] Caricando: " + path);

            // 1. Lettura del file e deserializzazione nell'oggetto T
            T result = this.objectMapper.readValue(new File(path.toUri()), clazz);

            System.out.println("[JsonLoadDataAccess] Caricato con successo");
            // 2. Restituzione dell'oggetto ricostruito (es. FileSystem)
            return result;
        } catch (IOException e) {
            System.err.println("[JsonLoadDataAccess] Errore caricamento: " + e.getMessage());
            // 3. Rilancio dell'eccezione per la gestione al livello superiore
            throw e;
        }
    }
}