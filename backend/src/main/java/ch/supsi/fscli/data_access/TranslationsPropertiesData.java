package ch.supsi.fscli.data_access;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class TranslationsPropertiesData implements ITranslationsProperties {
    private static TranslationsPropertiesData myself;

    private static final String BUNDLE_PATH = "i18n.labels";

    private static final String SUPPORTED_LANGUAGES_PATH = "/supported-languages.properties";

    private TranslationsPropertiesData() {}

    public static TranslationsPropertiesData getInstance() {
        if (myself == null) {
            myself = new TranslationsPropertiesData();
        }
        return myself;
    }

    @Override
    public List<String> getSupportedLanguageTags() {
        // 1. Carica le lingue supportate da un file di configurazione
        Properties p = loadSupportedLanguageTags();
        List<String> list = new ArrayList<>();
        if (p == null || p.isEmpty()) return list;
        for (Object k : p.keySet()) {
            String v = p.getProperty((String) k);
            if (v != null && !v.trim().isEmpty()) list.add(v.trim());
        }
        return list;
    }

    @Override
    public Properties getTranslations(Locale locale) {
        Properties translations = new Properties();
        if (locale == null) return translations;

        ClassLoader cl = this.getClass().getClassLoader();

        // Blocco di debug che tenta di costruire il nome del file properties
        try {
            String candidate = "i18n/labels"
                    + (locale.getLanguage().isEmpty() ? "" : ("_" + locale.getLanguage()))
                    + (locale.getCountry().isEmpty() ? "" : ("_" + locale.getCountry()))
                    + ".properties";
            URL r = cl.getResource(candidate);
            System.out.println("DEBUG TranslationsPropertiesData: checking resource " + candidate + " -> " + r);
        } catch (Exception ignored) {}

        // 2. Tenta di caricare il ResourceBundle specifico per la Locale richiesta
        ResourceBundle bundle = null;
        try {
            bundle = ResourceBundle.getBundle(
                    BUNDLE_PATH,
                    locale,
                    // Controllo per disabilitare il fallback implicito di Java (es. da it_CH a it)
                    ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT)
            );
        } catch (MissingResourceException mrex) {
            System.err.println("TranslationsPropertiesData: ResourceBundle not found for locale " + locale.toLanguageTag());
        }

        // 3. Logica di Fallback Manuale: se il bundle primario non viene trovato
        if (bundle == null) {
            List<String> supported = getSupportedLanguageTags();
            if (supported != null && !supported.isEmpty()) {
                String fallbackTag = supported.get(0);
                try {
                    // Tenta di caricare la prima lingua della lista delle lingue supportate
                    Locale fbLocale = Locale.forLanguageTag(fallbackTag.replace('_', '-'));
                    System.out.println("TranslationsPropertiesData: trying fallback supported tag " + fallbackTag);
                    bundle = ResourceBundle.getBundle(
                            BUNDLE_PATH,
                            fbLocale,
                            ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT)
                    );
                } catch (MissingResourceException mrex2) {
                    System.err.println("TranslationsPropertiesData: fallback ResourceBundle not found for " + (supported.isEmpty() ? "none" : supported.get(0)));
                } catch (Exception e) {
                    System.err.println("TranslationsPropertiesData: error while trying fallback -> " + e.getMessage());
                }
            }
        }

        // 4. Trasferimento delle chiavi/valori dal Bundle all'oggetto Properties
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                translations.put(key, bundle.getString(key));
            }
            System.out.println("TranslationsPropertiesData: loaded " + translations.size() + " translations for locale " + bundle.getLocale().toLanguageTag());
        } else {
            System.err.println("TranslationsPropertiesData: no translations loaded for locale " + locale.toLanguageTag());
        }

        return translations;
    }

    private Properties loadSupportedLanguageTags() {
        // 5. Metodo helper per caricare il file "supported-languages.properties" come stream
        Properties supported = new Properties();
        InputStream is;
        try {
            is = this.getClass().getResourceAsStream(SUPPORTED_LANGUAGES_PATH);
            if (is == null) {
                System.err.println("TranslationsPropertiesData: supported languages file not found at " + SUPPORTED_LANGUAGES_PATH);
                return supported;
            }
            try (InputStream in = is) {
                supported.load(in);
            }
        } catch (IOException e) {
            System.err.println("TranslationsPropertiesData: error loading supported languages -> " + e.getMessage());
        }
        return supported;
    }
}