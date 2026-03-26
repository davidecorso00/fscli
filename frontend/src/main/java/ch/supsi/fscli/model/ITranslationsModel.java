package ch.supsi.fscli.model;

public interface ITranslationsModel {
    String translate(String key);
    boolean changeLanguage(String languageTag);
    boolean isSupportedLanguageTag(String languageTag);
}
