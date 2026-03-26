package ch.supsi.fscli.controller;

import ch.supsi.fscli.model.PreferencesModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PreferencesControllerTest {

    @Mock
    private PreferencesModel mockModel;

    private PreferencesController controller;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = PreferencesController.getInstance();

        Field modelField = PreferencesController.class.getDeclaredField("model");
        modelField.setAccessible(true);
        modelField.set(controller, mockModel);
    }

    @Test
    void testSetNumCommandLineColumns() {
        controller.setNumCommandLineColumns(10);
        verify(mockModel, times(1)).setNumCommandLineColumns(10);
    }

    @Test
    void testGetNumCommandLineColumns() {
        when(mockModel.getNumCommandLineColumns()).thenReturn(5);
        int result = controller.getNumCommandLineColumns();
        assertEquals(5, result);
        verify(mockModel, times(1)).getNumCommandLineColumns();
    }

    @Test
    void testSetLanguage() {
        controller.setLanguage("it-IT");
        verify(mockModel).setLanguage("it-IT");
    }

    @Test
    void testGetLanguage() {
        when(mockModel.getLanguage()).thenReturn("en-US");
        assertEquals("en-US", controller.getLanguage());
    }

    @Test
    void testFonts() {
        controller.setCommandLineFont("Arial");
        verify(mockModel).setCommandLineFont("Arial");

        when(mockModel.getLogAreaFont()).thenReturn("Courier");
        assertEquals("Courier", controller.getLogAreaFont());
    }

    @Test
    void testSave() {
        controller.save();
        verify(mockModel).save();
    }
}