package com.uniye.skillwheel;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = SkillWheel.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SWConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.BooleanValue HUD_ENABLED = BUILDER.define("hudEnabled", true);
    static final ForgeConfigSpec SPEC = BUILDER.build();
    public static boolean hudEnabled = true;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        hudEnabled = HUD_ENABLED.get();
    }
}
