package com.uniye.skillwheel;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.Minecraft;
import java.lang.reflect.Method;

public class Network {
    public static void sendSelect(Player player, ItemStack stack) {
        String ch = "skillwheel";
        try {
            Player p = Minecraft.getInstance().player;
            if (p != null) {
                Method m = p.getClass().getMethod("kjs$sendData", String.class, CompoundTag.class);
                CompoundTag data = new CompoundTag();
                CompoundTag itemTag = new CompoundTag();
                stack.save(itemTag);
                data.put("item", itemTag);
                m.invoke(p, ch, data);
            }
        } catch (Throwable ignored) {
        }
    }
}
