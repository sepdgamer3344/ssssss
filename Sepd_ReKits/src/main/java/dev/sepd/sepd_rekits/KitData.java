package dev.sepd.sepd_rekits;

import org.bukkit.inventory.ItemStack;

public class KitData {
    private final ItemStack[] kitContents;
    private final ItemStack[] armorContents;
    private final ItemStack offHandItem;

    public KitData(ItemStack[] kitContents, ItemStack[] armorContents, ItemStack offHandItem) {
        this.kitContents = kitContents;
        this.armorContents = armorContents;
        this.offHandItem = offHandItem;
    }

    public ItemStack[] getKitContents() {
        return kitContents;
    }

    public ItemStack[] getArmorContents() {
        return armorContents;
    }

    public ItemStack getOffHandItem() {
        return offHandItem;
    }
}
