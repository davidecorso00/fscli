package ch.supsi.fscli.controller;

import ch.supsi.fscli.model.FSModel;
import ch.supsi.fscli.utils.FileChooserProvider;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class FSControllerTest {

    @Mock
    private FSModel mockFsModel;

    @Mock
    private FileChooserProvider mockFileChooser;

    @Mock
    private Stage mockStage;

    private FSController controller;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = FSController.getInstance();

        // 1. Iniettiamo il mock del FSModel (campo 'fsConfiguration')
        Field fsConfigField = FSController.class.getDeclaredField("fsConfiguration");
        fsConfigField.setAccessible(true);
        // Poiché il campo è final, dobbiamo forzarlo (potrebbe dare warning su Java moderni, ma spesso funziona nei test)
        fsConfigField.set(controller, mockFsModel);

        // 2. Impostiamo il provider per i file (usando il setter pubblico che hai aggiunto)
        controller.setFileChooserProvider(mockFileChooser);
    }

    @Test
    void testNewFileSystem_WhenRootIsNull() {
        // Se isRootNull è true, newFileSystem() viene chiamato direttamente senza Alert
        when(mockFsModel.isRootNull()).thenReturn(true);

        controller.newFileSystem();

        verify(mockFsModel, times(1)).newFileSystem();
    }

    @Test
    void testSaveFileSystemAs() {
        File fakeFile = new File("test_fs.json");
        when(mockFileChooser.showSaveDialog(any())).thenReturn(fakeFile);

        controller.SaveFileSystemAs(mockStage);

        // Verifichiamo che il modello salvi su quel percorso
        verify(mockFsModel).saveAs(eq(""), eq(fakeFile.toPath()));
    }

    @Test
    void testOpenFileSystem() {
        File fakeFile = new File("existing_fs.json");
        when(mockFileChooser.showOpenDialog(any())).thenReturn(fakeFile);
        when(mockFsModel.open(any(Path.class), anyString())).thenReturn(true);

        controller.openFileSystem(mockStage);

        verify(mockFsModel).open(fakeFile.toPath(), "existing_fs.json");
    }

    @Test
    void testExecuteCommand() {
        String cmd = "ls -l";
        controller.executeCommand(cmd);
        verify(mockFsModel).executeCommand(cmd);
    }
}