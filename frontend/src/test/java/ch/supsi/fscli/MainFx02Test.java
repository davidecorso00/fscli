package ch.supsi.fscli;

import org.junit.jupiter.api.Test;

public class MainFx02Test extends AbstractMainGUITest {

    @Test
    public void testOpenAbout() {
        step("Test Menu Help", () -> {
            clickOn("#outputView");

            clickOn("#helpMenu");
            sleep(SLEEP_INTERVAL);
            clickOn("#aboutMenuItem");
        });
    }
}