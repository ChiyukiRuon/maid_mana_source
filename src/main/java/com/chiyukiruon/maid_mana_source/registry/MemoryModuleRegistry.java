package com.chiyukiruon.maid_mana_source.registry;

import com.chiyukiruon.maid_mana_source.memory.ChargeIndexMemory;
import net.minecraft.core.registries.Registries;
import com.chiyukiruon.maid_mana_source.MaidManaSource;
import com.chiyukiruon.maid_mana_source.memory.SourceJarMemory;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

public class MemoryModuleRegistry {
    public static final DeferredRegister<MemoryModuleType<?>> REGISTER
            = DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, MaidManaSource.MODID);

    public static final RegistryObject<MemoryModuleType<SourceJarMemory>> SOURCE_JAR_LIST =
            REGISTER.register("source_jar_list", () -> new MemoryModuleType<>(Optional.of(SourceJarMemory.CODEC)));
    public static final RegistryObject<MemoryModuleType<Integer>> CHARGE_INDEX =
            REGISTER.register("charge_index", () -> new MemoryModuleType<>(Optional.of(ChargeIndexMemory.CODEC)));

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
