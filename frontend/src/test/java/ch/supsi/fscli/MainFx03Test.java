package ch.supsi.fscli;

import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class MainFx03Test extends AbstractMainGUITest {

    @Test
    public void testPreferences() {
        step("Test Preferences File", () -> {
            // 1. Open Preferences
            clickOn("#outputView");
            WaitForAsyncUtils.waitForFxEvents();

            clickOn("#editMenu");
            sleep(SLEEP_INTERVAL);
            clickOn("#preferencesMenuItem");

            // Wait for window to open
            WaitForAsyncUtils.waitForFxEvents();
            sleep(SLEEP_INTERVAL);

            // Check Columns Spinner
            verifyThat("#columnsSpinner", isVisible());
            verifyThat("#columnsSpinner", isEnabled());

            // Check Visible Lines Spinner
            verifyThat("#visibleLinesCLSpinner", isVisible());
            verifyThat("#visibleLinesCLSpinner", isEnabled());

            // Check Output Lines Spinner
            verifyThat("#visibleLinesOutputSpinner", isVisible());
            verifyThat("#visibleLinesOutputSpinner", isEnabled());

            verifyThat("#languageComboBox", isVisible());
            verifyThat("#languageComboBox", isEnabled());


            interact(() -> {
                Window prefWindow = window(Window::isFocused);
                if (prefWindow != null) {
                    ((Stage) prefWindow).close();
                }
            });

            WaitForAsyncUtils.waitForFxEvents();
            sleep(SLEEP_INTERVAL);
        });
    }
}