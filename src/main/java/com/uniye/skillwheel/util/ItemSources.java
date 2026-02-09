package com.uniye.skillwheel.util;

import com.uniye.skillwheel.SkillWheel;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.ArrayList;
import java.util.List;

public class ItemSources {
    public static final TagKey<Item> SKILL_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation("skillwheel", "skills"));

    public static List<ItemStack> getDisplayStacks(Player player) {
        List<ItemStack> result = new ArrayList<>();
        addIfValid(result, player.getMainHandItem());
        addIfValid(result, player.getOffhandItem());
        player.getInventory().armor.forEach(s -> addIfValid(result, s));
        if (ModList.get().isLoaded("curios")) {
            player.getCapability(CuriosCapability.INVENTORY).resolve().ifPresent(curios -> {
                curios.getCurios().forEach((slot, handler) -> {
                    IDynamicStackHandler stacks = handler.getStacks();
                    for (int i = 0; i < stacks.getSlots(); i++) {
                        addIfValid(result, stacks.getStackInSlot(i));
                    }
                });
            });
        }
        return result;
    }

    private static void addIfValid(List<ItemStack> list, ItemStack stack) {
        if (stack == null) return;
        if (stack.isEmpty()) return;
        boolean groupFlag = stack.is(SKILL_ITEMS);
        if (!groupFlag) return;
        list.add(stack.copy());
    }
}
