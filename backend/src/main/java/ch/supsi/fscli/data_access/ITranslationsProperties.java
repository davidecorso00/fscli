package ch.supsi.fscli.data_access;

import java.util.List;
import java.util.Locale;
import java.util.Properties;

public interface ITranslationsProperties {
    Properties getTranslations(Locale locale);
    List<String> getSupportedLanguageTags();
}
