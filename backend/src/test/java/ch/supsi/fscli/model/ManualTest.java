package ch.supsi.fscli.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

class ManualTest {

    @BeforeEach
    void setup() throws Exception {
        resetSingleton();
    }

    @AfterEach
    void cleanup() throws Exception {
        resetSingleton();
    }

    private void resetSingleton() throws Exception {
        Field instance = Manual.class.getDeclaredField("myself");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void getInstance_returnsSingleton() {
        // 1. Prima chiamata: crea l'istanza
        Manual instance1 = Manual.getInstance();
        Assertions.assertNotNull(instance1);

        // 2. Seconda chiamata: deve restituire lo stesso oggetto
        Manual instance2 = Manual.getInstance();
        Assertions.assertSame(instance1, instance2);
    }

    @Test
    void getInstance_lazyInitialization() throws Exception {
        // Verifica che il campo 'myself' sia null prima della chiamata (grazie al reset)
        Field field = Manual.class.getDeclaredField("myself");
        field.setAccessible(true);
        Assertions.assertNull(field.get(null));

        // Chiamata
        Manual.getInstance();

        // Verifica che ora sia inizializzato
        Assertions.assertNotNull(field.get(null));
    }

    @Test
    void getManual_returnsCorrectContent() {
        Manual manual = Manual.getInstance();
        String content = manual.getManual();

        Assertions.assertNotNull(content);

        Assertions.assertTrue(content.isBlank());
    }
}