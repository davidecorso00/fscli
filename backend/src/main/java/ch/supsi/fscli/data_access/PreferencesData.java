package ch.supsi.fscli.data_access;

import java.io.*;
import java.util.Properties;

public class PreferencesData {

    private static PreferencesData instance;
    private static final int DEFAULT_NUM_COMMAND_LINE_COLUMNS = 80;
    private static final int DEFAULT_NUM_VISIBLE_LINE_CL = 5;
    private static final int DEFAULT_NUM_VISIBLE_LINE_OUTPUT_AREA = 20;
    private static final String DEFAULT_LANGUAGE = "it";
    private static final String DEFAULT_APPLICATION_FONT = "Monospaced-PLAIN-12";

    // File di configurazione DIRETTAMENTE nella home directory dell'utente
    private static String CONFIG_FILE = System.getProperty("user.home")
            + File.separator
            + "fscli-preferences.txt";

    // Nome del file di default in resources
    private static final String DEFAULT_CONFIG_RESOURCE = "/default-preferences.properties";

    private int numCommandLineColumns;
    private int numVisibleLineCL;
    private int numVisibleLineOutputArea;
    private String language;
    private String commandLineFont;
    private String outputAreaFont;
    private String logAreaFont;

    public PreferencesData() {
        // 1. Inizializzazione con valori hardcoded di base
        this.numCommandLineColumns = DEFAULT_NUM_COMMAND_LINE_COLUMNS;
        this.numVisibleLineCL = DEFAULT_NUM_VISIBLE_LINE_CL;
        this.numVisibleLineOutputArea = DEFAULT_NUM_VISIBLE_LINE_OUTPUT_AREA;
        this.language = DEFAULT_LANGUAGE;
        this.commandLineFont = DEFAULT_APPLICATION_FONT;
        this.outputAreaFont = DEFAULT_APPLICATION_FONT;
        this.logAreaFont = DEFAULT_APPLICATION_FONT;

        // 2. Caricamento dei valori di default da Resources
        loadDefaultsFromResources();

        // 3. Caricamento e sovrascrittura con le preferenze utente salvate (se esistono)
        loadFromFile();
    }

    public static PreferencesData getInstance() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private void loadDefaultsFromResources() {
        // 4. Caricamento valori di fallback da un file interno al JAR
        try (InputStream is = getClass().getResourceAsStream(DEFAULT_CONFIG_RESOURCE)) {
            if (is != null) {
                Properties defaults = new Properties();
                defaults.load(is);

                // Parsing e assegnazione dei valori di default
                this.numCommandLineColumns = Integer.parseInt(
                        defaults.getProperty("numCommandLineColumns", "80")
                );
                this.numVisibleLineCL = Integer.parseInt(
                        defaults.getProperty("numVisibleLineCL", "5")
                );
                this.numVisibleLineOutputArea = Integer.parseInt(
                        defaults.getProperty("numVisibleLineOutputArea", "20")
                );
                this.language = defaults.getProperty("language", "it");
                this.commandLineFont = defaults.getProperty("command line font", "Monospaced-PLAIN-12");
                this.logAreaFont = defaults.getProperty("log area font", "Monospaced-PLAIN-12");
                this.outputAreaFont = defaults.getProperty("output area font", "Monospaced-PLAIN-12");

                System.out.println("Valori di default caricati da resources");
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Errore nel caricamento dei default da resources: " + e.getMessage());
        }
    }

    private static PreferencesData load() {
        return new PreferencesData();
    }

    public boolean save(){
        // 5. Metodo pubblico per salvare le preferenze
        try {
            saveToFile();
            return true;
        } catch (Exception e) {
            System.err.println("Errore durante il salvataggio delle preferenze: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public int getNumCommandLineColumns() {
        return numCommandLineColumns;
    }

    public void setNumCommandLineColumns(int numCommandLineColumns) {
        this.numCommandLineColumns = numCommandLineColumns;
    }

    public int getNumVisibleLineCL() {
        return numVisibleLineCL;
    }

    public void setNumVisibleLineCL(int numVisibleLineCL) {
        this.numVisibleLineCL = numVisibleLineCL;
    }

    public int getNumVisibleLineOutputArea() {
        return numVisibleLineOutputArea;
    }

    public void setNumVisibleLineOutputArea(int numVisibleLineOutputArea) {
        this.numVisibleLineOutputArea = numVisibleLineOutputArea;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCommandLineFont() {
        return commandLineFont;
    }

    public void setCommandLineFont(String commandLineFont) {
        this.commandLineFont = commandLineFont;
    }

    public String getOutputAreaFont() {
        return outputAreaFont;
    }

    public void setOutputAreaFont(String outputAreaFont) {
        this.outputAreaFont = outputAreaFont;
    }

    public String getLogAreaFont() {
        return logAreaFont;
    }

    public void setLogAreaFont(String logAreaFont) {
        this.logAreaFont = logAreaFont;
    }

    private void loadFromFile() {
        // 6. Caricamento dei valori utente dal file CONFIG_FILE nella home
        Properties props = new Properties();
        File configFile = new File(CONFIG_FILE);

        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                props.load(fis);

                // Parsing e sovrascrittura dei valori con quelli utente (mantenendo i default se mancanti)
                this.numCommandLineColumns = Integer.parseInt(
                        props.getProperty("numCommandLineColumns", String.valueOf(this.numCommandLineColumns))
                );
                this.numVisibleLineCL = Integer.parseInt(
                        props.getProperty("numVisibleLineCL", String.valueOf(this.numVisibleLineCL))
                );
                this.numVisibleLineOutputArea = Integer.parseInt(
                        props.getProperty("numVisibleLineOutputArea", String.valueOf(this.numVisibleLineOutputArea))
                );
                this.language = props.getProperty("language", this.language);
                this.commandLineFont = props.getProperty("commandLineFont", this.commandLineFont);
                this.outputAreaFont = props.getProperty("outputAreaFont", this.outputAreaFont);
                this.logAreaFont = props.getProperty("logAreaFont", this.logAreaFont);

                System.out.println("Preferenze utente caricate da: " + CONFIG_FILE);
            } catch (IOException | NumberFormatException e) {
                System.err.println("Errore nel caricamento delle preferenze utente: " + e.getMessage());
            }
        } else {
            // Se il file non esiste, lo crea salvando i valori correnti (default o resources)
            saveToFile();
        }
    }

    private void saveToFile() {
        // 7. Scrittura delle preferenze attuali sul disco (CONFIG_FILE)
        Properties props = new Properties();
        props.setProperty("numCommandLineColumns", String.valueOf(numCommandLineColumns));
        props.setProperty("numVisibleLineCL", String.valueOf(numVisibleLineCL));
        props.setProperty("numVisibleLineOutputArea", String.valueOf(numVisibleLineOutputArea));
        props.setProperty("language", language);
        props.setProperty("commandLineFont", commandLineFont);
        props.setProperty("outputAreaFont", outputAreaFont);
        props.setProperty("logAreaFont", logAreaFont);

        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "FSCLI User Preferences");
            System.out.println("Preferenze salvate in: " + CONFIG_FILE);
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio delle preferenze: " + e.getMessage());
            e.printStackTrace();
        }
    }
}