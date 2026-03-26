package ch.supsi.fscli.data_access;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class TranslationsPropertiesDataTest {

    @BeforeEach
    @AfterEach
    public void resetSingleton() throws Exception {
        Field instance = TranslationsPropertiesData.class.getDeclaredField("myself");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testSingleton() {
        TranslationsPropertiesData i1 = TranslationsPropertiesData.getInstance();
        TranslationsPropertiesData i2 = TranslationsPropertiesData.getInstance();
        assertSame(i1, i2);
    }

    @Test
    public void testGetTranslationsInvalidLocale() {
        TranslationsPropertiesData data = TranslationsPropertiesData.getInstance();

        Properties props = data.getTranslations(null);
        assertNotNull(props);
        assertTrue(props.isEmpty());
    }

    @Test
    public void testGetTranslationsFallback() {
        TranslationsPropertiesData data = TranslationsPropertiesData.getInstance();

        Locale klingon = Locale.forLanguageTag("tlh");
        Properties props = data.getTranslations(klingon);

        assertNotNull(props);
    }

    @Test
    public void testGetSupportedLanguageTags() {
        TranslationsPropertiesData data = TranslationsPropertiesData.getInstance();
        List<String> tags = data.getSupportedLanguageTags();

        assertNotNull(tags);
    }
}