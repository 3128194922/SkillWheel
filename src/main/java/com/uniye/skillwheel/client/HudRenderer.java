package com.uniye.skillwheel.client;

import com.uniye.skillwheel.SkillWheel;
import com.uniye.skillwheel.util.ItemSources;
import com.uniye.skillwheel.SWConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.CameraType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = SkillWheel.MODID, value = Dist.CLIENT)
public class HudRenderer {
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (!SWConfig.hudEnabled) return;
        if (mc.screen != null) return;
        if (mc.options.getCameraType() != CameraType.FIRST_PERSON) return;
        Player p = mc.player;
        if (p == null) return;
        List<ItemStack> items = ItemSources.getUiEntries(p);
        if (items.isEmpty()) return;
        GuiGraphics g = event.getGuiGraphics();
        int y = mc.getWindow().getGuiScaledHeight() - 24;
        int x = 6;
        int step = 20;
        for (ItemStack s : items) {
            g.renderItem(s, x, y);
            float pct = p.getCooldowns().getCooldownPercent(s.getItem(), event.getPartialTick());
            if (pct > 0f) {
                int h = Mth.floor(16f * pct);
                int ty = y + (16 - h);
                g.fill(x, ty, x + 16, ty + h, 0x80FFFFFF);
            }
            x += step;
        }
    }
}
