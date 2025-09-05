package tw.iehow.data;

import tw.iehow.ProxyPortal;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.lang.reflect.Type;

public class PortalManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String DATA_FOLDER = "config";
    private static final String PORTALS_DATA_FILE = ProxyPortal.MOD_ID + "_data.json";

    private static final Map<String, PortalData> portals = new HashMap<>();

    public static boolean createPortal(PortalData portalData) {
        try {
            if (portals.containsKey(portalData.portalName)) {
                return false;
            }

            portals.put(portalData.portalName, portalData);
            savePortals();
            return true;

        } catch (Exception e) {
            ProxyPortal.LOGGER.error("Failed to create portal: {}", portalData.portalName, e);
            return false;
        }
    }

    public static boolean deletePortal(String portalName) {
        try {
            if (!portals.containsKey(portalName)) {
                return false;
            }

            portals.remove(portalName);
            savePortals();
            return true;

        } catch (Exception e) {
            ProxyPortal.LOGGER.error("Failed to delete portal: {}", portalName, e);
            return false;
        }
    }

    public static List<PortalData> getAllPortals() {
        return new ArrayList<>(portals.values());
    }

    public static void reloadPortals() throws IOException {
        portals.clear();
        loadPortals();
    }

    public static void initializePortals() {
        try {
            loadPortals();
        } catch (Exception e) {
            ProxyPortal.LOGGER.error("Failed to initialize portals", e);
        }
    }

    private static void savePortals() throws IOException {
        Path dataPath = Paths.get(DATA_FOLDER);
        if (!Files.exists(dataPath)) {
            Files.createDirectories(dataPath);
        }

        File file = new File(DATA_FOLDER, PORTALS_DATA_FILE);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(portals, writer);
        }
    }

    private static void loadPortals() throws IOException {
        File file = new File(DATA_FOLDER, PORTALS_DATA_FILE);
        if (!file.exists()) {
            ProxyPortal.LOGGER.info("Portal data file not found. Creating default configuration with example portals.");
            createDefaultPortalConfig();
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, PortalData>>(){}.getType();
            Map<String, PortalData> loadedPortals = gson.fromJson(reader, type);
            if (loadedPortals != null) {
                portals.putAll(loadedPortals);
            }
        }
    }

    private static void createDefaultPortalConfig() throws IOException {
        PortalData examplePortal = new PortalData(
            "example_portal",
            new PortalData.PositionGroup(
                "example:dimension",
                new PortalData.Position(0, 64, 0),
                new PortalData.Position(5, 70, 5)
            ),
            "lobby"
        );
        portals.put(examplePortal.portalName, examplePortal);
        savePortals();
    }

    public static int getPortalCount() {
        return portals.size();
    }
}
