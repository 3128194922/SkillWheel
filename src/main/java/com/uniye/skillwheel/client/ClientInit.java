package com.uniye.skillwheel.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.uniye.skillwheel.SkillWheel;
import com.uniye.skillwheel.client.RadialMenuScreen;

@Mod.EventBusSubscriber(modid = SkillWheel.MODID, value = Dist.CLIENT)
public class ClientInit {
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (KeyBindings.consumeOpenClick()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen instanceof RadialMenuScreen) mc.setScreen(null);
            else mc.setScreen(new RadialMenuScreen());
        }
    }
}
