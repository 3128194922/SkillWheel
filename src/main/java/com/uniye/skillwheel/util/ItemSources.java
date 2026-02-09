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

    public static List<SelectableItem> getDisplayEntries(Player player) {
        List<SelectableItem> result = new ArrayList<>();
        addIfValid(result, player.getMainHandItem(), "vanilla_mainhand", 0, null);
        addIfValid(result, player.getOffhandItem(), "vanilla_offhand", 0, null);
        for (int i = 0; i < player.getInventory().armor.size(); i++) {
            addIfValid(result, player.getInventory().armor.get(i), "vanilla_armor", i, null);
        }
        if (ModList.get().isLoaded("curios")) {
            player.getCapability(CuriosCapability.INVENTORY).resolve().ifPresent(curios -> {
                curios.getCurios().forEach((slot, handler) -> {
                    IDynamicStackHandler stacks = handler.getStacks();
                    for (int i = 0; i < stacks.getSlots(); i++) {
                        addIfValid(result, stacks.getStackInSlot(i), "curios", i, slot);
                    }
                });
            });
        }
        return result;
    }

    private static void addIfValid(List<SelectableItem> list, ItemStack stack, String source, int idx, String slotName) {
        if (stack == null) return;
        if (stack.isEmpty()) return;
        boolean groupFlag = stack.is(SKILL_ITEMS);
        if (!groupFlag) return;
        list.add(new SelectableItem(stack.copy(), source, idx, slotName));
    }
}
