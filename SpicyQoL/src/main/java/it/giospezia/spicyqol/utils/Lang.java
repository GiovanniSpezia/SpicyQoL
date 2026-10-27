package it.giospezia.spicyqol.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Lightweight translation layer.
 *
 * Loads a language file (e.g. lang/zh_CN.yml) containing a "translations"
 * list of {from: original, to: translated} entries. Messages are translated
 * with a longest-first substring replacement, so messages built by
 * concatenating dynamic values (player names, coordinates, ...) are still
 * translated piece by piece. If a language file is missing or a string has
 * no translation, the original text is returned untouched.
 *
 * To add a new language, just drop a new YAML file under lang/ and set
 * "language" in config.yml.
 */
public final class Lang {

    // Pairs [original, translated] sorted by original length (longest first)
    private static List<String[]> pairs = new ArrayList<>();

    private Lang() {
    }

    public static void init(JavaPlugin plugin) {
        pairs = new ArrayList<>();
        String lang = plugin.getConfig().getString("language", "zh_CN");
        if (lang == null || lang.trim().isEmpty() || lang.trim().equalsIgnoreCase("it")) return;

        String resourcePath = "lang/" + lang.trim() + ".yml";
        try {
            if (plugin.getResource(resourcePath) == null) {
                plugin.getLogger().warning("Language file not found in jar: " + resourcePath);
                return;
            }
            File outFile = new File(plugin.getDataFolder(), resourcePath);
            // If a language file from an older plugin version is already on
            // disk, replace it so the shipped translations always apply.
            // (User edits should go into the freshly saved file afterwards.)
            int shippedVersion = 0;
            try (java.io.InputStreamReader reader = new java.io.InputStreamReader(
                    plugin.getResource(resourcePath), java.nio.charset.StandardCharsets.UTF_8)) {
                shippedVersion = YamlConfiguration.loadConfiguration(reader).getInt("version", 0);
            }
            if (!outFile.exists()) {
                plugin.saveResource(resourcePath, false);
            } else {
                // An old/broken copy on disk may not even be parseable, in
                // which case treat it as outdated so it gets replaced.
                int currentVersion = -1;
                try {
                    currentVersion = YamlConfiguration.loadConfiguration(outFile).getInt("version", -1);
                } catch (Exception ignored) {
                }
                if (currentVersion != shippedVersion) {
                    plugin.saveResource(resourcePath, true);
                    plugin.getLogger().info("Updated outdated language file: " + resourcePath);
                }
            }

            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(outFile);
            for (Map<?, ?> entry : cfg.getMapList("translations")) {
                Object from = entry.get("from");
                Object to = entry.get("to");
                if (from == null || to == null) continue;
                String key = String.valueOf(from);
                String value = String.valueOf(to);
                if (key.isEmpty() || value.isEmpty()) continue;
                pairs.add(new String[]{key, value});
                // also register the §-code variant so directly-§-formatted strings match too
                String secKey = key.replace('&', '§');
                if (!secKey.equals(key)) pairs.add(new String[]{secKey, value.replace('&', '§')});
            }
            pairs.sort((a, b) -> Integer.compare(b[0].length(), a[0].length()));

            if (!pairs.isEmpty()) {
                plugin.getLogger().info("Loaded language " + lang + " (" + pairs.size() + " entries).");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load language " + lang + ": " + e.getMessage());
            pairs = new ArrayList<>();
        }
    }

    /**
     * Translates a raw (uncolored) string using the loaded language file.
     * Returns the input unchanged when no translation applies.
     */
    public static String t(String s) {
        if (s == null || s.isEmpty() || pairs.isEmpty()) return s;
        for (String[] pair : pairs) {
            if (s.contains(pair[0])) s = s.replace(pair[0], pair[1]);
        }
        return s;
    }
}
