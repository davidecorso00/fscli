package ch.supsi.fscli.business.translations;

public interface ITranslationsLogic {
    String translate(String key);
    boolean changeLanguage(String languageTag);
    boolean isSupportedLanguageTag(String languageTag);
}
