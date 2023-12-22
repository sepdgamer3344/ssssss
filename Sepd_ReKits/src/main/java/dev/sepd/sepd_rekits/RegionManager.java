package dev.sepd.sepd_rekits;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegionManager {

    private final Sepd_ReKits plugin; // Reference to the main plugin class
    private Map<String, Location> regions;
    private Map<String, Map<String, KitData>> kits; // Updated to store KitData

    public RegionManager(Sepd_ReKits plugin) {
        this.plugin = plugin;
        this.regions = new HashMap<>();
        this.kits = new HashMap<>();
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

    public void setKit(String regionName, String kitName, ItemStack[] kitContents, ItemStack[] armorContents, ItemStack offHandItem) {
        if (kits.containsKey(regionName)) {
            kits.get(regionName).put(kitName, new KitData(kitContents, armorContents, offHandItem));
        }
    }

    public void deleteKit(String regionName, String kitName) {
        if (kits.containsKey(regionName)) {
            kits.get(regionName).remove(kitName);
        }
    }

    public KitData getKit(String regionName, String kitName) {
        return kits.getOrDefault(regionName, new HashMap<>()).get(kitName);
    }

    public Map<String, KitData> getKits(String regionName) {
        return kits.get(regionName);
    }

    public void loadKits() {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection kitsSection = config.getConfigurationSection("kits");

        if (kitsSection != null) {
            for (String regionName : kitsSection.getKeys(false)) {
                ConfigurationSection regionKitsSection = kitsSection.getConfigurationSection(regionName);

                if (regionKitsSection != null) {
                    for (String kitName : regionKitsSection.getKeys(false)) {
                        ConfigurationSection kitDataSection = regionKitsSection.getConfigurationSection(kitName);
                        if (kitDataSection != null) {
                            ItemStack[] kitContents = ((List<?>) kitDataSection.getList("kitContents")).toArray(new ItemStack[0]);
                            ItemStack[] armorContents = ((List<?>) kitDataSection.getList("armorContents")).toArray(new ItemStack[0]);
                            ItemStack offHandItem = kitDataSection.getItemStack("offHandItem");
                            setKit(regionName, kitName, kitContents, armorContents, offHandItem);
                        }
                    }
                }
            }
        }
    }

    public void saveKits() {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection kitsSection = config.createSection("kits");

        for (Map.Entry<String, Map<String, KitData>> regionEntry : kits.entrySet()) {
            String regionName = regionEntry.getKey();
            Map<String, KitData> regionKits = regionEntry.getValue();

            ConfigurationSection regionKitsSection = kitsSection.createSection(regionName);

            for (Map.Entry<String, KitData> kitEntry : regionKits.entrySet()) {
                String kitName = kitEntry.getKey();
                KitData kitData = kitEntry.getValue();

                // Save the kit data to the config
                ConfigurationSection kitDataSection = regionKitsSection.createSection(kitName);
                kitDataSection.set("kitContents", Arrays.asList(kitData.getKitContents()));
                kitDataSection.set("armorContents", Arrays.asList(kitData.getArmorContents()));
                kitDataSection.set("offHandItem", kitData.getOffHandItem());
            }
        }

        // Save the config to file
        plugin.saveConfig();
    }

    // Additional methods and logic related to region management can be added here

    public void selectKit(Player player, String regionName, String kitName) {
        KitData selectedKit = getKit(regionName, kitName);
        if (selectedKit != null) {
            applyKit(player, selectedKit);
        }
    }

    public void applyKit(Player player, KitData kitData) {
        PlayerInventory playerInventory = player.getInventory();

        // Clear existing inventory
        playerInventory.clear();
        playerInventory.setArmorContents(kitData.getArmorContents());

        // Apply kit contents
        for (ItemStack item : kitData.getKitContents()) {
            if (item != null) {
                playerInventory.addItem(item.clone());
            }
        }

        // Apply off-hand item
        ItemStack offHandItem = kitData.getOffHandItem();
        if (offHandItem != null && !offHandItem.getType().equals(Material.AIR)) {
            playerInventory.setItemInOffHand(offHandItem);
        }
    }
}
