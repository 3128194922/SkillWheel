package com.uniye.skillwheel;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;
import java.lang.reflect.Method;
import com.uniye.skillwheel.util.SelectableItem;

public class Network {
    public static void sendSelect(Player player, SelectableItem entry) {
        String ch = "skillwheel";
        try {
            Player p = Minecraft.getInstance().player;
            if (p != null) {
                Method m = p.getClass().getMethod("kjs$sendData", String.class, CompoundTag.class);
                CompoundTag data = new CompoundTag();
                CompoundTag itemTag = new CompoundTag();
                entry.stack.save(itemTag);
                data.put("item", itemTag);
                data.putString("sourceType", entry.sourceType);
                data.putInt("slotIndex", entry.slotIndex);
                if (entry.slotName != null) data.putString("slotName", entry.slotName);
                m.invoke(p, ch, data);
            }
        } catch (Throwable ignored) {
        }
    }
}
