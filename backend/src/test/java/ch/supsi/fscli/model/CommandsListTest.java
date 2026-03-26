package ch.supsi.fscli.model;

import ch.supsi.fscli.business.translations.TranslationsLogic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class CommandsListTest {

    @Test
    void getInstance_returnsSingleton() {
        CommandsList instance1 = CommandsList.getInstance();
        CommandsList instance2 = CommandsList.getInstance();
        Assertions.assertSame(instance1, instance2);
    }

    @Test
    void getAllCommands_formatsAndTranslatesCorrectly() {
        // Setup Static Mock per TranslationsLogic
        try (MockedStatic<TranslationsLogic> mockedTranslations = Mockito.mockStatic(TranslationsLogic.class)) {

            // Mock dell'istanza di TranslationsLogic
            TranslationsLogic mockTranslator = Mockito.mock(TranslationsLogic.class);
            mockedTranslations.when(TranslationsLogic::getInstance).thenReturn(mockTranslator);

            // Stubbing del metodo translate
            Mockito.when(mockTranslator.translate(anyString())).thenAnswer(invocation -> {
                String key = invocation.getArgument(0);
                return "TRANS_" + key; // Simula una traduzione prefissando TRANS_
            });

            // Act
            CommandsList list = CommandsList.getInstance();
            String output = list.getAllCommands();

            // Assert
            Assertions.assertNotNull(output);
            Assertions.assertTrue(output.contains("Commands:")); // Header hardcoded

            // Verifica che vengano usate le chiavi tradotte
            Assertions.assertTrue(output.contains("TRANS_command.ls.signature"));
            Assertions.assertTrue(output.contains("ls:"));
            Assertions.assertTrue(output.contains("TRANS_command.ls.description"));

            // Verifica che il translator sia stato chiamato
            Mockito.verify(mockTranslator, Mockito.atLeastOnce()).translate(anyString());
        }
    }
}