package ch.supsi.fscli;

import org.junit.jupiter.api.Test;

public class MainFx01Test extends AbstractMainGUITest {

    @Test
    public void testOpenHelp() {
        step("Test Menu Help", () -> {
            clickOn("#outputView");

            clickOn("#helpMenu");
            sleep(SLEEP_INTERVAL);
            clickOn("#helpMenuItem");
            sleep(SLEEP_INTERVAL);
        });
    }
}