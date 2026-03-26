package ch.supsi.fscli.controller;

import ch.supsi.fscli.model.TranslationsModel;

public class TranslationsController {
    private static TranslationsController myself;
    private final TranslationsModel model = TranslationsModel.getInstance();

    private TranslationsController() {}

    public static TranslationsController getInstance() {
        if (myself == null) {
            myself = new TranslationsController();
        }
        return myself;
    }

    public void initFromPreferences() {
        try {
            String pref = PreferencesController.getInstance().getLanguage();
            if (pref == null || pref.trim().isEmpty()) {
                pref = "en-US";
            }
            String normalized = normalizeLanguageTag(pref);
            boolean ok = model.changeLanguage(normalized);
            if (!ok) {
                // fallback su lingua base (es. "en")
                ok = model.changeLanguage(normalized.split("[-_]")[0]);
            }
            if (!ok) {
                ok = model.changeLanguage("en");
            }
            System.out.println("TranslationsController.initFromPreferences -> pref='" + pref + "' loaded=" + ok);
        } catch (Exception e) {
            System.err.println("TranslationsController.initFromPreferences error: " + e.getMessage());
        }
    }

    public String translate(String key) {
        try {
            String result = model.translate(key);
            return result != null ? result : key;
        } catch (Exception e) {
            System.err.println("TranslationsController.translate error for key=" + key + " -> " + e.getMessage());
            return key;
        }
    }

    public void changeLanguage(String languageTag) {
        try {
            model.changeLanguage(normalizeLanguageTag(languageTag));
        } catch (Exception e) {
            System.err.println("TranslationsController.changeLanguage error for tag=" + languageTag + " -> " + e.getMessage());
        }
    }

    private String normalizeLanguageTag(String tag) {
        if (tag == null) return "";
        tag = tag.trim();
        tag = tag.replace('_', '-');
        switch (tag.toLowerCase()) {
            case "en": return "en-US";
            case "it": return "it-IT";
            case "de": return "de-DE";
            case "fr": return "fr-FR";
            default: return tag;
        }
    }
}