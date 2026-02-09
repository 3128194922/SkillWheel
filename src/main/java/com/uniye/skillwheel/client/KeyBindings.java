package com.uniye.skillwheel.client;

import com.uniye.skillwheel.SkillWheel;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = SkillWheel.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyBindings {
    public static KeyMapping OPEN;

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        OPEN = new KeyMapping("key.skillwheel.open", GLFW.GLFW_KEY_R, "key.categories.skillwheel");
        event.register(OPEN);
    }

    public static boolean isOpenPressed() {
        return OPEN != null && OPEN.consumeClick() && Minecraft.getInstance().screen == null;
    }

    public static boolean consumeOpenClick() {
        return OPEN != null && OPEN.consumeClick();
    }
}
