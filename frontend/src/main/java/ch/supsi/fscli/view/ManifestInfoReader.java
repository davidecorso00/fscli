package ch.supsi.fscli.view;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class ManifestInfoReader {

    private List<String> developers = Collections.emptyList();
    private String date;
    private String version;

    public ManifestInfoReader() {
        readManifestAttributes();
    }

    private void readManifestAttributes() {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF")) {
            if (stream != null) {
                Manifest manifest = new Manifest(stream);
                Attributes attr = manifest.getMainAttributes();

                // Version
                String ver = attr.getValue("Implementation-Version");
                if (ver != null) {
                    version = ver;
                }

                // Build date
                String buildDate = attr.getValue("Build-Date");
                if (buildDate != null) {
                    date = buildDate;
                }

                // Developers
                String devs = attr.getValue("Developers");
                if (devs != null && !devs.isBlank()) {
                    // Split by comma e rimuovi spazi
                    developers = Arrays.stream(devs.split(","))
                            .map(String::trim)
                            .toList();
                }
            }
        } catch (Exception e) {
            System.err.println("Error while reading the manifest: " + e.getMessage());
        }
    }

    public List<String> getDevelopers() {
        return developers;
    }

    public String getVersion() {
        return version;
    }

    public String getDate() {
        return date;
    }
}