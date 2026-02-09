package com.uniye.skillwheel.util;

import net.minecraft.world.item.ItemStack;

public class SelectableItem {
    public final ItemStack stack;
    public final String sourceType;
    public final int slotIndex;
    public final String slotName;

    public SelectableItem(ItemStack stack, String sourceType, int slotIndex, String slotName) {
        this.stack = stack;
        this.sourceType = sourceType;
        this.slotIndex = slotIndex;
        this.slotName = slotName;
    }
}
