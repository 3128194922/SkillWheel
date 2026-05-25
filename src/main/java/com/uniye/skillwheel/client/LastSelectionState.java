package com.uniye.skillwheel.client;

import com.uniye.skillwheel.Network;
import com.uniye.skillwheel.util.ItemSources;
import com.uniye.skillwheel.util.SelectableItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class LastSelectionState {
    private static SelectableItem lastEntry;
    private static boolean lastWasSubmenu;
    private static int lastSubmenuIndex = -1;
    private static boolean lastShiftDown;
    private static CompoundTag lastPayload;

    private LastSelectionState() {
    }

    public static void record(SelectableItem entry, boolean isSubmenu, int submenuIndex, boolean shiftDown) {
        if (entry == null) {
            clear();
            return;
        }
        lastEntry = new SelectableItem(entry.stack.copy(), entry.sourceType, entry.slotIndex, entry.slotName);
        lastWasSubmenu = isSubmenu;
        lastSubmenuIndex = submenuIndex;
        lastShiftDown = shiftDown;
        lastPayload = Network.createSelectPayload(lastEntry, isSubmenu, submenuIndex, shiftDown);
    }

    public static void clear() {
        lastEntry = null;
        lastWasSubmenu = false;
        lastSubmenuIndex = -1;
        lastShiftDown = false;
        lastPayload = null;
    }

    public static boolean replay(Player player) {
        if (player == null || lastEntry == null) {
            return false;
        }
        for (SelectableItem current : ItemSources.getDisplayEntries(player)) {
            if (!matches(current, lastEntry)) {
                continue;
            }
            if (lastWasSubmenu && !hasSubmenuOption(current.stack, lastSubmenuIndex)) {
                return false;
            }
            if (lastWasSubmenu && lastPayload != null) {
                Network.send(player, lastPayload.copy());
            } else {
                Network.sendSelect(player, current, lastWasSubmenu, lastSubmenuIndex, lastShiftDown);
            }
            return true;
        }
        return false;
    }

    private static boolean matches(SelectableItem current, SelectableItem saved) {
        if (!saved.sourceType.equals(current.sourceType)) {
            return false;
        }
        if (saved.slotIndex != current.slotIndex) {
            return false;
        }
        if (saved.slotName == null ? current.slotName != null : !saved.slotName.equals(current.slotName)) {
            return false;
        }
        return ItemStack.isSameItemSameTags(saved.stack, current.stack);
    }

    private static boolean hasSubmenuOption(ItemStack stack, int submenuIndex) {
        if (submenuIndex < 1 || submenuIndex > 4) {
            return false;
        }
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("submenu")) {
            return false;
        }
        CompoundTag submenu = tag.getCompound("submenu");
        return submenu.contains(String.valueOf(submenuIndex));
    }
}
