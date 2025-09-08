package com.chiyukiruon.maid_mana_source.registry;

import com.chiyukiruon.maid_mana_source.memory.ChargeIndexMemory;
import com.chiyukiruon.maid_mana_source.memory.ScannedSourceListMemory;
import net.minecraft.core.registries.Registries;
import com.chiyukiruon.maid_mana_source.MaidManaSource;
import com.chiyukiruon.maid_mana_source.memory.ChargeSourceListMemory;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

public class MemoryModuleRegistry {
    public static final DeferredRegister<MemoryModuleType<?>> REGISTER
            = DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, MaidManaSource.MODID);

    public static final RegistryObject<MemoryModuleType<ScannedSourceListMemory>> SCANNED_SOURCE_LIST =
            REGISTER.register("scanned_source_list", () -> new MemoryModuleType<>(Optional.of(ScannedSourceListMemory.CODEC)));
    public static final RegistryObject<MemoryModuleType<ChargeSourceListMemory>> CHARGE_SOURCE_LIST =
            REGISTER.register("charge_source_list", () -> new MemoryModuleType<>(Optional.of(ChargeSourceListMemory.CODEC)));
    public static final RegistryObject<MemoryModuleType<Integer>> CHARGE_INDEX =
            REGISTER.register("charge_index", () -> new MemoryModuleType<>(Optional.of(ChargeIndexMemory.CODEC)));

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
