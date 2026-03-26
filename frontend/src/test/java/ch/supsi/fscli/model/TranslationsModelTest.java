package ch.supsi.fscli.model;

import ch.supsi.fscli.application.translations.TranslationsApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TranslationsModelTest {

    @Mock
    private TranslationsApplication mockApp;

    // Per salvare e ripristinare il valore originale (pulizia)
    private Object originalApp;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Reflection su campo STATICO 'application'
        Field appField = TranslationsModel.class.getDeclaredField("application");
        appField.setAccessible(true);

        // Salviamo l'originale
        originalApp = appField.get(null);

        // Iniettiamo il mock (null perché è statico)
        appField.set(null, mockApp);
    }

    @AfterEach
    void tearDown() throws Exception {
        // Ripristino del campo statico originale per non rompere altri test
        Field appField = TranslationsModel.class.getDeclaredField("application");
        appField.setAccessible(true);
        appField.set(null, originalApp);
    }

    @Test
    void testTranslate() {
        when(mockApp.translate("label.ok")).thenReturn("D'accordo");

        String result = TranslationsModel.getInstance().translate("label.ok");

        assertEquals("D'accordo", result);
        verify(mockApp).translate("label.ok");
    }

    @Test
    void testChangeLanguage() {
        when(mockApp.changeLanguage("fr-FR")).thenReturn(true);

        boolean result = TranslationsModel.getInstance().changeLanguage("fr-FR");

        assertTrue(result);
        verify(mockApp).changeLanguage("fr-FR");
    }

    @Test
    void testIsSupportedLanguageTag() {
        when(mockApp.isSupportedLanguageTag("de-DE")).thenReturn(true);
        when(mockApp.isSupportedLanguageTag("xx-YY")).thenReturn(false);

        assertTrue(TranslationsModel.getInstance().isSupportedLanguageTag("de-DE"));
        assertFalse(TranslationsModel.getInstance().isSupportedLanguageTag("xx-YY"));
    }
}