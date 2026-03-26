package ch.supsi.fscli.model;

import ch.supsi.fscli.application.preferences.PreferencesApp;
import ch.supsi.fscli.model.inode.PreferencesChanges;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PreferencesModelTest {

    @Mock
    private PreferencesApp mockApp;

    private PreferencesModel model;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        model = PreferencesModel.getInstance();

        model.getPreferencesChanges().clear();

        // Reflection: Iniettiamo il mock nel campo 'app'
        // (Assumendo che tu abbia fatto la piccola modifica di aggiungere 'private PreferencesApp app...' nel Model)
        Field appField = PreferencesModel.class.getDeclaredField("app");
        appField.setAccessible(true);
        appField.set(model, mockApp);
    }

    @Test
    void testSetNumCommandLineColumns_TracksChange() {
        when(mockApp.getNumCommandLineColumns()).thenReturn(5);

        model.setNumCommandLineColumns(10);

        // Verifica delega
        verify(mockApp).setNumCommandLineColumns(10);

        // Verifica che il cambiamento sia stato tracciato
        assertTrue(model.getPreferencesChanges().contains(PreferencesChanges.NUM_COLUMNS_COMMAND_LINE));
    }

    @Test
    void testSetNumCommandLineColumns_NoChange_DoesNotTrack() {
        // Simuliamo che il valore sia già 10
        when(mockApp.getNumCommandLineColumns()).thenReturn(10);

        // Reimpostiamo 10
        model.setNumCommandLineColumns(10);

        // Non dovrebbe aver tracciato cambiamenti
        assertFalse(model.getPreferencesChanges().contains(PreferencesChanges.NUM_COLUMNS_COMMAND_LINE));
    }

    @Test
    void testSetLanguage() {
        when(mockApp.getLanguage()).thenReturn("en-US");

        model.setLanguage("it-IT");

        verify(mockApp).setLanguage("it-IT");
        assertTrue(model.getPreferencesChanges().contains(PreferencesChanges.LANGUAGE));
    }

    @Test
    void testObserverNotification() {
        PreferencesObserver mockObserver = mock(PreferencesObserver.class);
        model.addObserver(mockObserver);

        model.notifyObservers();

        verify(mockObserver, times(1)).updatePreferences();
    }
}