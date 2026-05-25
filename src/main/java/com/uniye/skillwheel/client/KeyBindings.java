package com.uniye.skillwheel.client;

import com.mojang.blaze3d.platform.InputConstants;
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
    public static KeyMapping REPEAT_LAST;
    public static KeyMapping SHIFT_STATE;

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        OPEN = new KeyMapping("key.skillwheel.open", GLFW.GLFW_KEY_R, "key.categories.skillwheel");
        REPEAT_LAST = new KeyMapping("key.skillwheel.repeat_last", InputConstants.UNKNOWN.getValue(), "key.categories.skillwheel");
        SHIFT_STATE = new KeyMapping("key.skillwheel.shift_state", GLFW.GLFW_KEY_LEFT_SHIFT, "key.categories.skillwheel");
        event.register(OPEN);
        event.register(REPEAT_LAST);
        event.register(SHIFT_STATE);
    }

    public static boolean isOpenPressed() {
        return OPEN != null && OPEN.consumeClick() && Minecraft.getInstance().screen == null;
    }

    public static boolean consumeOpenClick() {
        return OPEN != null && OPEN.consumeClick();
    }

    public static boolean consumeRepeatLastClick() {
        return REPEAT_LAST != null && REPEAT_LAST.consumeClick() && Minecraft.getInstance().screen == null;
    }

    public static boolean isShiftStateDown() {
        if (SHIFT_STATE == null) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.getWindow() == null) {
            return false;
        }
        InputConstants.Key key = SHIFT_STATE.getKey();
        if (key.getType() == InputConstants.Type.KEYSYM) {
            return InputConstants.isKeyDown(mc.getWindow().getWindow(), key.getValue());
        }
        if (key.getType() == InputConstants.Type.MOUSE) {
            return org.lwjgl.glfw.GLFW.glfwGetMouseButton(mc.getWindow().getWindow(), key.getValue()) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
        }
        return false;
    }
}
