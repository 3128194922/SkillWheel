package com.uniye.skillwheel;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import java.lang.reflect.Method;
import com.uniye.skillwheel.util.SelectableItem;

public class Network {
    public static CompoundTag createSelectPayload(SelectableItem entry, boolean isSubmenu, int submenuIndex, boolean shiftDown) {
        CompoundTag data = new CompoundTag();
        CompoundTag itemTag = new CompoundTag();
        entry.stack.save(itemTag);
        data.put("item", itemTag);
        data.putString("sourceType", entry.sourceType);
        data.putBoolean("isSubmenu", isSubmenu);
        data.putBoolean("shiftDown", shiftDown);
        if (submenuIndex != -1) {
            data.putInt("submenuIndex", submenuIndex);
        }
        data.putInt("slotIndex", entry.slotIndex);
        if (entry.slotName != null) {
            data.putString("slotName", entry.slotName);
        }
        return data;
    }

    public static void send(Player player, CompoundTag data) {
        String ch = "skillwheel";
        try {
            if (player != null) {
                Method m = player.getClass().getMethod("kjs$sendData", String.class, CompoundTag.class);
                m.invoke(player, ch, data);
            }
        } catch (Throwable ignored) {
        }
    }

    public static void sendSelect(Player player, SelectableItem entry, boolean isSubmenu, int submenuIndex, boolean shiftDown) {
        send(player, createSelectPayload(entry, isSubmenu, submenuIndex, shiftDown));
    }

    public static void sendSelect(Player player, SelectableItem entry) {
        sendSelect(player, entry, false, -1, false);
    }
}
