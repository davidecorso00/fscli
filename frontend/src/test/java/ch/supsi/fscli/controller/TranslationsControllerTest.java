package ch.supsi.fscli.controller;

import ch.supsi.fscli.model.TranslationsModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TranslationsControllerTest {

    @Mock
    private TranslationsModel mockModel;

    private TranslationsController controller;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = TranslationsController.getInstance();

        Field modelField = TranslationsController.class.getDeclaredField("model");
        modelField.setAccessible(true);
        modelField.set(controller, mockModel);
    }

    @Test
    void testTranslate() {
        when(mockModel.translate("label.hello")).thenReturn("Ciao");
        String result = controller.translate("label.hello");
        assertEquals("Ciao", result);
    }

    @Test
    void testChangeLanguage() {
        // Test con normalizzazione: "en" -> "en-US"
        controller.changeLanguage("en");
        verify(mockModel).changeLanguage("en-US");

        // Test con normalizzazione: "it" -> "it-IT"
        controller.changeLanguage("it");
        verify(mockModel).changeLanguage("it-IT");
    }
}