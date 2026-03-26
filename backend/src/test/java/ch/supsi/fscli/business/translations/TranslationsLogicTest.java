package ch.supsi.fscli.business.translations;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TranslationsLogicTest {

    @BeforeEach
    @AfterEach
    public void resetSingleton() throws Exception {
        Field instance = TranslationsLogic.class.getDeclaredField("myself");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testGetInstanceNotNull() {
        assertNotNull(TranslationsLogic.getInstance());
    }

    @Test
    public void testGetInstanceReturnsSameObject() {
        TranslationsLogic instance1 = TranslationsLogic.getInstance();
        TranslationsLogic instance2 = TranslationsLogic.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    public void testTranslateWhenTranslationsNull() {
        TranslationsLogic logic = TranslationsLogic.getInstance();
        String result = logic.translate("test.key");
        assertEquals("test.key", result);
    }

    @Test
    public void testChangeLanguageAndTranslate() {
        TranslationsLogic logic = TranslationsLogic.getInstance();
        boolean result = logic.changeLanguage("en");


        assertDoesNotThrow(() -> logic.translate("test.key"));
    }

    @Test
    public void testTranslateKeyNotFound() {
        TranslationsLogic logic = TranslationsLogic.getInstance();
        logic.changeLanguage("en");

        String result = logic.translate("non.existing.key");
        assertEquals("non.existing.key", result);
    }

    @Test
    public void testIsSupportedLanguageTag() throws Exception {
        TranslationsLogic logic = TranslationsLogic.getInstance();

        Field listField = TranslationsLogic.class.getDeclaredField("supportedLanguageTags");
        listField.setAccessible(true);
        listField.set(logic, Arrays.asList("en", "it"));

        assertTrue(logic.isSupportedLanguageTag("en"), "Inglese dovrebbe essere supportato (iniettato)");

        assertFalse(logic.isSupportedLanguageTag("xx"), "Lingua inesistente non dovrebbe essere supportata");
    }
}