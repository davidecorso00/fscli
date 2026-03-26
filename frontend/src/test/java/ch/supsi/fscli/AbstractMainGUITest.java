package ch.supsi.fscli;

import ch.supsi.fscli.controller.FSController;
import ch.supsi.fscli.model.FSModel;
import ch.supsi.fscli.model.PreferencesModel;
import ch.supsi.fscli.model.inode.FSStatus;
import ch.supsi.fscli.utils.NativeFileChooser;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeoutException;

public abstract class AbstractMainGUITest extends ApplicationTest {

    protected final static int SLEEP_INTERVAL = 1000;

    @Override
    public void start(Stage stage) {
        resetSingletonsState();

        new MainFx().start(stage);
        stage.show();
        stage.toFront();
        stage.requestFocus();
    }

    @BeforeEach
    public void setUp() {
        WaitForAsyncUtils.waitForFxEvents();
    }

    @AfterEach
    public void tearDown() throws TimeoutException {
        release(new KeyCode[]{});
        release(new MouseButton[]{});
        FxToolkit.hideStage();
    }

    //Resetta TUTTI i Singleton e rimuove eventuali Mock rimasti dai Unit Test.
    private void resetSingletonsState() {
        try {
            // --- 1. FSController: Ripristina FileChooser Reale ---
            FSController controller = FSController.getInstance();
            controller.setFileChooserProvider(new NativeFileChooser());

            // Pulisci views per evitare ConcurrentModificationException
            Field viewsField = FSController.class.getDeclaredField("views");
            viewsField.setAccessible(true);
            ((List<?>) viewsField.get(controller)).clear();

            // --- 2. PreferencesModel: Reset ---
            PreferencesModel pm = PreferencesModel.getInstance();

            Field pmChangesField = PreferencesModel.class.getDeclaredField("preferencesChanges");
            pmChangesField.setAccessible(true);
            ((List<?>) pmChangesField.get(pm)).clear();

            Field pmObserversField = PreferencesModel.class.getDeclaredField("observers");
            pmObserversField.setAccessible(true);
            ((List<?>) pmObserversField.get(pm)).clear();

            // --- 3. FSModel: Reset ---
            FSModel fsm = FSModel.getInstance();

            // Rimuovi Mock statici (fondamentale se esegui tutti i test insieme)
            Field fsConfigField = FSModel.class.getDeclaredField("fsConfiguration");
            fsConfigField.setAccessible(true);
            fsConfigField.set(fsm, ch.supsi.fscli.application.FSApplication.getInstance());

            Field fsPersistField = FSModel.class.getDeclaredField("fsPersistence");
            fsPersistField.setAccessible(true);
            fsPersistField.set(fsm, ch.supsi.fscli.application.FSApplication.getInstance());

            // Reset Stato
            Field statusField = FSModel.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(fsm, FSStatus.WELCOME);

            Field fsmObserversField = FSModel.class.getDeclaredField("observers");
            fsmObserversField.setAccessible(true);
            ((List<?>) fsmObserversField.get(fsm)).clear();

            // --- 4. TranslationsModel: Rimuovi Mock ---
            // Risolve i NullPointerException sui testi
            Field tmAppField = ch.supsi.fscli.model.TranslationsModel.class.getDeclaredField("application");
            tmAppField.setAccessible(true);
            tmAppField.set(null, ch.supsi.fscli.application.translations.TranslationsApplication.getInstance());

        } catch (Exception e) {
            System.err.println("ERRORE GRAVE RESET TEST: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected void step(String stepName, Runnable runnable) {
        System.out.println("--- STEP: " + stepName + " [START] ---");
        try {
            runnable.run();
        } catch (Exception e) {
            System.err.println("--- STEP: " + stepName + " [FAILED] ---");
            throw e;
        }
        System.out.println("--- STEP: " + stepName + " [END] ---");
    }
}