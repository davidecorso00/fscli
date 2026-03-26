package ch.supsi.fscli;

import com.sun.javafx.scene.control.ContextMenuContent;
import com.sun.javafx.scene.control.MenuBarButton;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testfx.matcher.control.TextInputControlMatchers;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.*;

public class MainFx00Test extends AbstractMainGUITest {

    @Test
    public void walkThrough() {
        testMain();
        testFileMenu();
        testEditMenu();
        testHelpMenu();
    }


    private void testMain() {
        step("Main...", () -> {
            sleep(SLEEP_INTERVAL);
            verifyThat("#fileMenu", isVisible());
            verifyThat("#editMenu", isVisible());
            verifyThat("#helpMenu", isVisible());
            verifyThat("#commandLine", TextInputControlMatchers.hasText(""));
            verifyThat("#outputView", TextInputControlMatchers.hasText(""));
        });
    }

    private void testFileMenu() {
        step("Test Menu File (Struttura e stati iniziali)", () -> {
            sleep(SLEEP_INTERVAL);
            Menu menu = lookup("#fileMenu").queryAs(MenuBarButton.class).menu;
            Assertions.assertTrue(menu.isVisible());
            Assertions.assertFalse(menu.isDisable());

            sleep(SLEEP_INTERVAL);
            clickOn("#fileMenu");

            MenuItem newMenuItem = lookup("#newMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            Assertions.assertTrue(newMenuItem.isVisible());
            Assertions.assertFalse(newMenuItem.isDisable());

            MenuItem openMenuItem = lookup("#openMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            Assertions.assertTrue(openMenuItem.isVisible());
            Assertions.assertFalse(openMenuItem.isDisable());

            MenuItem saveMenuItem = lookup("#saveMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            Assertions.assertTrue(saveMenuItem.isVisible());
            Assertions.assertTrue(saveMenuItem.isDisable());

            MenuItem saveAsMenuItem = lookup("#saveAsMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            Assertions.assertTrue(saveAsMenuItem.isVisible());
            Assertions.assertTrue(saveAsMenuItem.isDisable());

            MenuItem quitMenuItem = lookup("#quitMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            Assertions.assertTrue(quitMenuItem.isVisible());
            Assertions.assertFalse(quitMenuItem.isDisable());

            sleep(SLEEP_INTERVAL);
            clickOn("#fileMenu");
        });
    }

    private void testEditMenu() {
        step("Test Menu Edit", () -> {
            sleep(SLEEP_INTERVAL);
            Menu editMenu = lookup("#editMenu").queryAs(MenuBarButton.class).menu;
            Assertions.assertTrue(editMenu.isVisible());
            Assertions.assertFalse(editMenu.isDisable());

            sleep(SLEEP_INTERVAL);
            clickOn("#editMenu");

            MenuItem preferencesMenuItem = lookup("#preferencesMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            Assertions.assertTrue(preferencesMenuItem.isVisible());
            Assertions.assertFalse(preferencesMenuItem.isDisable());


            sleep(SLEEP_INTERVAL);
            clickOn("#editMenu");
        });
    }

    private void testHelpMenu() {
        step("Test Menu Help", () -> {
            // 1. Apriamo il menu Help
            sleep(SLEEP_INTERVAL);
            Menu menu = lookup("#helpMenu").queryAs(MenuBarButton.class).menu;
            Assertions.assertTrue(menu.isVisible());
            Assertions.assertFalse(menu.isDisable());

            sleep(SLEEP_INTERVAL);
            clickOn("#helpMenu");

            MenuItem helpMenuItem = lookup("#helpMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            Assertions.assertTrue(helpMenuItem.isVisible());
            Assertions.assertFalse(helpMenuItem.isDisable());

            MenuItem aboutMenuItem = lookup("#aboutMenuItem").queryAs(ContextMenuContent.MenuItemContainer.class).getItem();
            Assertions.assertTrue(aboutMenuItem.isVisible());
            Assertions.assertFalse(aboutMenuItem.isDisable());

            sleep(SLEEP_INTERVAL);
            clickOn("#helpMenu");
        });
    }
}