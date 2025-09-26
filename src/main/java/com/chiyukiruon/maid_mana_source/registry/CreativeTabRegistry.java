package com.chiyukiruon.maid_mana_source.registry;

import com.chiyukiruon.maid_mana_source.MaidManaSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CreativeTabRegistry {
    public static final String TAB_NAME = "maid_mana_source_tab_main";
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MaidManaSource.MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CRAFT_TAB =
            CREATIVE_MODE_TABS.register("maid_mana_source", () ->
                    CreativeModeTab.builder().icon(() -> new ItemStack(ItemRegistry.SOURCE_LIST.get()))
                            .title(Component.translatable(TAB_NAME))
                            .displayItems((pParameter, pOutput) -> {
                                ItemRegistry.ITEMS
                                        .getEntries()
                                        .stream()
                                        .map(DeferredHolder::get)
                                        .forEach(pOutput::accept);
                            }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
