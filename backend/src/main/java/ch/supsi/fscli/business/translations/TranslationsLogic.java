package ch.supsi.fscli.business.translations;

import ch.supsi.fscli.data_access.ITranslationsProperties;
import ch.supsi.fscli.data_access.TranslationsPropertiesData;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class TranslationsLogic implements ITranslationsLogic {

    private static TranslationsLogic myself;
    private final List<String> supportedLanguageTags;
    private Properties translations;
    private final ITranslationsProperties translationsDao;

    private TranslationsLogic() {
        this.translationsDao = TranslationsPropertiesData.getInstance();
        this.supportedLanguageTags = translationsDao.getSupportedLanguageTags();
    }

    public static TranslationsLogic getInstance() {
        if (myself == null) {
            myself = new TranslationsLogic();
        }
        return myself;
    }


    public String translate(String key, Object... args) {
        // 1. Controllo esistenza chiave (key)
        if (translations == null || !translations.containsKey(key)) {
            // Fallback: ritorna la chiave se non c'è traduzione, aggiungendo gli args se presenti
            return key + (args != null && args.length > 0 ? " " + Arrays.toString(args) : "");
        }

        String pattern = translations.getProperty(key);

        // 2. Formattazione del messaggio con argomenti
        // Se ci sono argomenti, formatta la stringa (es: "File {0} non trovato" -> "File pippo non trovato")
        if (args != null && args.length > 0) {
            return MessageFormat.format(pattern, args);
        }

        // 3. Restituzione del pattern semplice (nessun argomento)
        return pattern;
    }


    @Override
    public String translate(String key) {
        // 4. Traduzione semplice (senza argomenti dinamici)
        if (translations == null) return key;
        if (!translations.containsKey(key)) return key;
        return translations.getProperty(key);
    }

    @Override
    public boolean changeLanguage(String languageTag) {
        // 5. Cambio lingua: carica il set di Properties corrispondente
        this.translations = translationsDao.getTranslations(Locale.forLanguageTag(languageTag));
        return this.translations != null;
    }

    @Override
    public boolean isSupportedLanguageTag(String languageTag) {
        // 6. Verifica se il tag lingua è supportato dal DAO
        return this.supportedLanguageTags.contains(languageTag);
    }
}