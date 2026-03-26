package ch.supsi.fscli.application.translations;

import ch.supsi.fscli.business.translations.TranslationsLogic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TranslationsAppTest {

    @BeforeEach
    @AfterEach
    public void resetSingleton() throws Exception {
        Field appInstance = TranslationsApplication.class.getDeclaredField("myself");
        appInstance.setAccessible(true);
        appInstance.set(null, null);

        Field logicInstance = TranslationsLogic.class.getDeclaredField("myself");
        logicInstance.setAccessible(true);
        logicInstance.set(null, null);
    }

    @Test
    public void testGetInstanceNotNull() {
        assertNotNull(TranslationsApplication.getInstance());
    }

    @Test
    public void testIsSupportedLanguageTag() throws Exception {
        TranslationsApplication app = TranslationsApplication.getInstance();

        TranslationsLogic logic = TranslationsLogic.getInstance();

        Field listField = TranslationsLogic.class.getDeclaredField("supportedLanguageTags");
        listField.setAccessible(true);
        listField.set(logic, Arrays.asList("en", "it"));

        boolean result = app.isSupportedLanguageTag("en");
        assertTrue(result, "L'inglese dovrebbe essere supportato (dati iniettati)");

        boolean badResult = app.isSupportedLanguageTag("xx");
        assertFalse(badResult, "XX non dovrebbe essere supportato");
    }

    @Test
    public void testChangeLanguage() {
        TranslationsApplication app = TranslationsApplication.getInstance();
        boolean result = app.changeLanguage("en");
        assertDoesNotThrow(() -> app.changeLanguage("it"));
    }
}