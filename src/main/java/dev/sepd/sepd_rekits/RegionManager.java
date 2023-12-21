package dev.sepd.sepd_rekits;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegionManager {

    private final Sepd_ReKits plugin; // Reference to the main plugin class
    private Map<String, Location> regions;
    private Map<String, Map<String, KitContents>> kits;

    public RegionManager(Sepd_ReKits plugin) {
        this.plugin = plugin;
        this.regions = new HashMap<>();
        this.kits = new HashMap<>();
        loadRegions(); // Load regions from the configuration file on initialization
    }

    public void createRegion(String regionName, Location location) {
        regions.put(regionName, location);
        kits.put(regionName, new HashMap<>());
    }

    public void createRegion(String regionName, Location firstPoint, Location secondPoint) {
        // Implement region creation based on two points
        // For example, you can store both points and use them as boundaries
        // This would depend on the specifics of your region handling
        // regions.put(regionName, createRegionWithTwoPoints(firstPoint, secondPoint));
        // kits.put(regionName, new HashMap<>());
    }

    public void deleteRegion(String regionName) {
        regions.remove(regionName);
        kits.remove(regionName);
    }

    public boolean isInSpecificRegion(Player player) {
        Location location = player.getLocation();
        String regionName = getRegionName(location);
        return regionName != null;
    }

    public String getRegionName(Location location) {
        for (Map.Entry<String, Location> entry : regions.entrySet()) {
            String regionName = entry.getKey();
            Location regionLocation = entry.getValue();
            if (isLocationWithinRegion(location, regionLocation)) {
                return regionName;
            }
        }
        return null;
    }

    private boolean isLocationWithinRegion(Location location, Location regionLocation) {
        return location.distance(regionLocation) < 10; // Placeholder distance check
    }

    public void setKit(String regionName, String kitName, KitContents kitContents) {
        if (kits.containsKey(regionName)) {
            kits.get(regionName).put(kitName, kitContents);
        }
    }

    public void deleteKit(String regionName, String kitName) {
        if (kits.containsKey(regionName)) {
            kits.get(regionName).remove(kitName);
        }
    }

    public KitContents getKit(String regionName, String kitName) {
        return kits.getOrDefault(regionName, new HashMap<>()).get(kitName);
    }

    public Map<String, KitContents> getKits(String regionName) {
        return kits.get(regionName);
    }

    public void loadRegions() {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection regionsSection = config.getConfigurationSection("regions");

        if (regionsSection != null) {
            for (String regionName : regionsSection.getKeys(false)) {
                Location regionLocation = (Location) regionsSection.get(regionName);
                regions.put(regionName, regionLocation);
            }
        }
    }

    public void saveRegions() {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection regionsSection = config.createSection("regions");

        for (Map.Entry<String, Location> regionEntry : regions.entrySet()) {
            String regionName = regionEntry.getKey();
            Location regionLocation = regionEntry.getValue();
            regionsSection.set(regionName, regionLocation);
        }

        // Save the config to file
        plugin.saveConfig();
    }

    public void loadKits() {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection kitsSection = config.getConfigurationSection("kits");

        if (kitsSection != null) {
            for (String regionName : kitsSection.getKeys(false)) {
                ConfigurationSection regionKitsSection = kitsSection.getConfigurationSection(regionName);

                if (regionKitsSection != null) {
                    for (String kitName : regionKitsSection.getKeys(false)) {
                        ConfigurationSection kitContentsSection = regionKitsSection.getConfigurationSection(kitName);

                        if (kitContentsSection != null) {
                            ItemStack[] mainInventory = kitContentsSection.getList("mainInventory").toArray(new ItemStack[0]);
                            ItemStack offhandItem = (ItemStack) kitContentsSection.get("offhandItem");
                            ItemStack[] armor = ((List<?>) kitContentsSection.getList("armor")).toArray(new ItemStack[0]);

                            KitContents kitContents = new KitContents(mainInventory, offhandItem, armor);
                            setKit(regionName, kitName, kitContents);
                        }
                    }
                }
            }
        }
    }

    public void saveKits() {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection kitsSection = config.createSection("kits");

        for (Map.Entry<String, Map<String, KitContents>> regionEntry : kits.entrySet()) {
            String regionName = regionEntry.getKey();
            Map<String, KitContents> regionKits = regionEntry.getValue();

            ConfigurationSection regionKitsSection = kitsSection.createSection(regionName);

            for (Map.Entry<String, KitContents> kitEntry : regionKits.entrySet()) {
                String kitName = kitEntry.getKey();
                KitContents kitContents = kitEntry.getValue();

                // Save the kit items to the config
                ConfigurationSection kitContentsSection = regionKitsSection.createSection(kitName);
                kitContentsSection.set("mainInventory", Arrays.asList(kitContents.getMainInventory()));
                kitContentsSection.set("offhandItem", kitContents.getOffhandItem());
                kitContentsSection.set("armor", Arrays.asList(kitContents.getArmor()));
            }
        }

        // Save the config to file
        plugin.saveConfig();
    }

    // Additional methods and logic related to region management can be added here

    public void selectKit(Player player, String regionName, String kitName) {
        KitContents selectedKit = getKit(regionName, kitName);
        if (selectedKit != null) {
            player.getInventory().setContents(selectedKit.getMainInventory());
            player.getInventory().setItemInOffHand(selectedKit.getOffhandItem());
            player.getInventory().setArmorContents(selectedKit.getArmor());
        }
    }
}
