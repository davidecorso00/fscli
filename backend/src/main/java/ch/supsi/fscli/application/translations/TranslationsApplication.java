package ch.supsi.fscli.application.translations;

import ch.supsi.fscli.business.translations.ITranslationsLogic;
import ch.supsi.fscli.business.translations.TranslationsLogic;

public class TranslationsApplication implements ITranslationsLogic {

    private static TranslationsApplication myself;
    // Istanza della Business Logic, a cui verranno delegate tutte le operazioni
    private final TranslationsLogic translationsLogic = TranslationsLogic.getInstance();

    public static TranslationsApplication getInstance() {
        if (myself == null) {
            myself = new TranslationsApplication();
        }
        return myself;
    }


    // 1. Delega per la traduzione di una chiave semplice
    @Override
    public String translate(String key) {
        return this.translationsLogic.translate(key);
    }

    // 2. Delega per il cambio di lingua
    @Override
    public boolean changeLanguage(String languageTag) {
        return this.translationsLogic.changeLanguage(languageTag);
    }

    // 3. Delega per la verifica del supporto del tag lingua
    @Override
    public boolean isSupportedLanguageTag(String languageTag) {
        return this.translationsLogic.isSupportedLanguageTag(languageTag);
    }
}