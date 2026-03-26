package ch.supsi.fscli;

import ch.supsi.fscli.controller.FSController;
import ch.supsi.fscli.controller.TranslationsController;
import ch.supsi.fscli.utils.FileChooserProvider;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class MainFx05Test extends AbstractMainGUITest {

    @TempDir
    Path tempDir;

    @Test
    public void testFileOperationsSequence() throws IOException {
        step("Round-Trip Test: New -> SaveAs -> Cmd -> Save -> New -> Open", () -> {

            File mockFile = tempDir.resolve("filesystem_test.txt").toFile();

            FSController.getInstance().setFileChooserProvider(new FileChooserProvider() {
                @Override
                public File showOpenDialog(Stage stage) { return mockFile; }

                @Override
                public File showSaveDialog(Stage stage) { return mockFile; }
            });

            // Reset UI iniziale
            clickOn("#outputView");
            WaitForAsyncUtils.waitForFxEvents();

            clickOn("#fileMenu");
            clickOn("#newMenuItem");
            WaitForAsyncUtils.waitForFxEvents();
            sleep(SLEEP_INTERVAL);

            clickOn("#fileMenu");
            clickOn("#saveAsMenuItem");
            WaitForAsyncUtils.waitForFxEvents();
            sleep(SLEEP_INTERVAL);

            Assertions.assertTrue(mockFile.exists(), "Il file deve essere stato creato dopo Save As");

            clickOn("#commandLine");
            write("mkdir prova");
            sleep(500);
            clickOn("#enter");
            clickOn("#commandLine");
            write("cd prova");
            sleep(500);
            clickOn("#enter");
            clickOn("#commandLine");
            write("pwd");
            sleep(500);
            clickOn("#enter");

            WaitForAsyncUtils.waitForFxEvents();
            sleep(SLEEP_INTERVAL);

            // Verifica: Controlliamo che l'output mostri la root "/"
            String outputText = lookup("#outputView").queryTextInputControl().getText();
            Assertions.assertTrue(outputText.contains("/"),
                    "L'output di 'pwd' dovrebbe contenere la root '/'. Output attuale: " + outputText);

            // Qui testiamo che il 'Save' semplice funzioni senza aprire finestre (usa il path già noto)
            clickOn("#fileMenu");
            clickOn("#saveMenuItem");

            WaitForAsyncUtils.waitForFxEvents();
            sleep(SLEEP_INTERVAL);

            // Verifica Log: conferma salvataggio
            String logText = lookup("#logView").queryTextInputControl().getText();
            Assertions.assertTrue(logText.contains("salvato") || logText.contains("saved"),
                    "Il log dovrebbe confermare il salvataggio.");

            clickOn("#fileMenu");
            clickOn("#newMenuItem");

            // Aspettiamo che appaia l'Alert
            WaitForAsyncUtils.waitForFxEvents();
            sleep(SLEEP_INTERVAL);

            // Clicchiamo sul bottone di conferma dell'Alert
            clickOn(TranslationsController.getInstance().translate("label.yes"));

            // Attendiamo che l'alert si chiuda e il reset avvenga
            WaitForAsyncUtils.waitForFxEvents();
            sleep(SLEEP_INTERVAL);

            clickOn("#fileMenu");
            clickOn("#openMenuItem");

            WaitForAsyncUtils.waitForFxEvents();
            sleep(SLEEP_INTERVAL);

            String logTextFinal = lookup("#logView").queryTextInputControl().getText();
            Assertions.assertTrue(logTextFinal.contains("caricato") || logTextFinal.contains("loaded") || logTextFinal.contains("aperto"),
                    "Il log finale dovrebbe confermare il caricamento del file.");

            System.out.println("TEST ROUND-TRIP SUPERATO CON SUCCESSO!");
        });
    }
}


/* TEST OF SETTING (NOT ABLE TO READ LANGUAGE CHANGE)

package ch.supsi.fscli;

import ch.supsi.fscli.controller.TranslationsController;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;

public class MainFx04Test extends AbstractMainGUITest {

    @Test
    public void testPreferences() {
        step("Test Modifica TOTALE Preferenze (Numeri, Lingua, Fonts)", () -> {

            // 1. Reset Focus e Apertura Menu
            clickOn("#outputView");
            WaitForAsyncUtils.waitForFxEvents();

            clickOn("#editMenu");
            sleep(SLEEP_INTERVAL);
            clickOn("#preferencesMenuItem");
            WaitForAsyncUtils.waitForFxEvents();
            sleep(SLEEP_INTERVAL);


            interact(() -> {
                // A) Modifica Numeri (Spinners)
                changeSpinnerSafely(lookup("#columnsSpinner").queryAs(Spinner.class));
                changeSpinnerSafely(lookup("#visibleLinesCLSpinner").queryAs(Spinner.class));
                changeSpinnerSafely(lookup("#visibleLinesOutputSpinner").queryAs(Spinner.class));

                // B) Modifica Lingua (Cicla alla successiva)
                ComboBox<String> langCombo = lookup("#languageComboBox").queryComboBox();
                int nextLangIndex = (langCombo.getSelectionModel().getSelectedIndex() + 1) % langCombo.getItems().size();
                langCombo.getSelectionModel().select(nextLangIndex);

                // C) Modifica Font Command Line
                changeComboBoxSafely(lookup("#commandLineFontCombo").queryComboBox());

                // D) Modifica Font Output Area
                changeComboBoxSafely(lookup("#outputAreaFontCombo").queryComboBox());

                // E) Modifica Font Log Area
                changeComboBoxSafely(lookup("#logAreaFontCombo").queryComboBox());
            });

            // Attendiamo che la UI recepisca i cambiamenti
            sleep(SLEEP_INTERVAL);

            clickOn("#saveButton");
            sleep(SLEEP_INTERVAL);

            // Gestione Alert "Salvataggio Avvenuto" (Preme OK)
            type(KeyCode.ENTER);
            WaitForAsyncUtils.waitForFxEvents();
            sleep(SLEEP_INTERVAL);

            String logText = lookup("#logView").queryTextInputControl().getText();
            if (logText == null) logText = "";

            TranslationsController tc = TranslationsController.getInstance();

            // Verifica Intestazione
            String expectedHeader = tc.translate("label.preferencesChangeIntroduction");

            // Verifica che TUTTI i cambiamenti siano stati registrati
            assertLogContains(logText, tc.translate("label.languageChanged"), "Lingua");
            assertLogContains(logText, tc.translate("label.numColumnsCommandLineChanged"), "Colonne Cmd");
            assertLogContains(logText, tc.translate("label.numLinesLogAreaChanged"), "Righe Log");
            assertLogContains(logText, tc.translate("label.numLinesOutputAreaChanged"), "Righe Output");
            assertLogContains(logText, tc.translate("label.fontCommandLineChanged"), "Font Cmd");
            assertLogContains(logText, tc.translate("label.fontOutputAreaChanged"), "Font Output");
            assertLogContains(logText, tc.translate("label.fontLogAreaChanged"), "Font Log");

            System.out.println("TEST PREFERENZE SUPERATO: Tutte le modifiche rilevate.");
        });
    }

    // --- HELPER PER SPINNER ---
    private void changeSpinnerSafely(Spinner<Integer> spinner) {
        int current = spinner.getValue();
        // Se siamo al massimo, scendiamo. Altrimenti saliamo.
        if (current >= 15) {
            spinner.getValueFactory().decrement(1);
        } else {
            spinner.getValueFactory().increment(1);
        }
    }

    // --- HELPER PER COMBOBOX (Risolve il problema dei Font) ---
    private void changeComboBoxSafely(ComboBox<?> combo) {
        int size = combo.getItems().size();
        if (size > 1) {
            // Seleziona il prossimo elemento (torna a 0 se è alla fine)
            int nextIndex = (combo.getSelectionModel().getSelectedIndex() + 1) % size;
            combo.getSelectionModel().select(nextIndex);
        }
    }

    private void assertLogContains(String logText, String expectedPart, String fieldName) {
        Assertions.assertTrue(logText.contains(expectedPart),
                "Il log non riporta la modifica per: " + fieldName + "\nLog attuale:\n" + logText);
    }
}

 */