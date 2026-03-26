package ch.supsi.fscli.model;

import ch.supsi.fscli.application.translations.TranslationsApplication;

public class TranslationsModel extends AbstractModel implements ITranslationsModel {

    private static TranslationsModel myself;
    private static TranslationsApplication application = TranslationsApplication.getInstance();

    public static TranslationsModel getInstance() {
        if (myself == null) {
            myself = new TranslationsModel();
        }
        return myself;
    }

    protected TranslationsModel() {}


    @Override
    public String translate(String key) {
        return application.translate(key);
    }

    @Override
    public boolean changeLanguage(String languageTag) {
        return application.changeLanguage(languageTag);
    }

    @Override
    public boolean isSupportedLanguageTag(String languageTag) {
        return application.isSupportedLanguageTag(languageTag);
    }
}
