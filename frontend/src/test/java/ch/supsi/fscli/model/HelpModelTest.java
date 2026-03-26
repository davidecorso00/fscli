package ch.supsi.fscli.model;

import ch.supsi.fscli.application.help.HelpApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HelpModelTest {

    @Mock
    private HelpApp mockHelpApp;

    private HelpModel model;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        model = HelpModel.getInstance();

        // Reflection: Sostituiamo il campo 'helpApp' con il mock
        Field appField = HelpModel.class.getDeclaredField("helpApp");
        appField.setAccessible(true);
        appField.set(model, mockHelpApp);
    }

    @Test
    void testGetHelp() {
        // Simuliamo la risposta dell'app
        String expectedHelp = "Lista comandi:\n- ls\n- pwd";
        when(mockHelpApp.getHelp()).thenReturn(expectedHelp);

        // Chiamiamo il metodo del modello
        String result = model.getHelp();

        // Verifichiamo il risultato e la delega
        assertEquals(expectedHelp, result);
        verify(mockHelpApp).getHelp();
    }
}