package dev.sepd.sepd_rekits;

import org.bukkit.inventory.ItemStack;

public class KitContents {

    private ItemStack[] mainInventory;
    private ItemStack offhandItem;
    private ItemStack[] armor;

    public KitContents(ItemStack[] mainInventory, ItemStack offhandItem, ItemStack[] armor) {
        this.mainInventory = mainInventory;
        this.offhandItem = offhandItem;
        this.armor = armor;
    }

    public ItemStack[] getMainInventory() {
        return mainInventory;
    }

    public ItemStack getOffhandItem() {
        return offhandItem;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public void setMainInventory(ItemStack[] mainInventory) {
        this.mainInventory = mainInventory;
    }

    public void setOffhandItem(ItemStack offhandItem) {
        this.offhandItem = offhandItem;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }
}
