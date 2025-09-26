package com.chiyukiruon.maid_mana_source.registry;

import com.chiyukiruon.maid_mana_source.MaidManaSource;
import com.chiyukiruon.maid_mana_source.menu.MaidChargeConfigGui;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class GuiRegistry {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, MaidManaSource.MODID);
    public static final DeferredHolder<MenuType<?>, MenuType<MaidChargeConfigGui.Container>> MAID_CHARGE_CONFIG_GUI = MENU_TYPES.register("maid_charge_config_gui",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new MaidChargeConfigGui.Container(windowId, inv, data.readInt())));

    public static void init(IEventBus modEventBus) {
        MENU_TYPES.register(modEventBus);
    }
}
