package ch.supsi.fscli.application.translations;

public interface ITranslationsApplication {
    String translate(String key);
    boolean changeLanguage(String languageTag);
    boolean isSupportedLanguageTag(String languageTag);
}
