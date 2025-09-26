package com.chiyukiruon.maid_mana_source;

import com.chiyukiruon.maid_mana_source.registry.*;
import net.neoforged.fml.common.Mod;

@Mod(MaidManaSource.MODID)
public class MaidManaSource {

    public static final String MODID = "maid_mana_source";

    public MaidManaSource() {
        net.neoforged.bus.api.IEventBus modEventBus = net.neoforged.fml.ModLoadingContext.get().getActiveContainer().getEventBus();
        net.neoforged.fml.ModLoadingContext.get().getActiveContainer().registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON, Config.SPEC);
        MemoryModuleRegistry.register(modEventBus);
        ItemRegistry.register(modEventBus);
        CreativeTabRegistry.register(modEventBus);
        GuiRegistry.init(modEventBus);
    }
}