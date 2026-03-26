package ch.supsi.fscli.utils;

import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

public class NativeFileChooserTest{

    @Test
    public void testStructure() {
        // 1. Verifichiamo che possiamo istanziare la classe
        NativeFileChooser chooser = new NativeFileChooser();

        // 2. Verifichiamo che sia del tipo giusto (implementi l'interfaccia)
        Assertions.assertInstanceOf(FileChooserProvider.class, chooser);

        Assertions.assertNotNull(chooser);
    }

}