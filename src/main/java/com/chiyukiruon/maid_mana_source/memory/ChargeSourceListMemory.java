package com.chiyukiruon.maid_mana_source.memory;

import com.chiyukiruon.maid_mana_source.registry.MemoryModuleRegistry;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class ChargeSourceListMemory {
    private final List<BlockPos> sourceJars;

    public ChargeSourceListMemory(List<BlockPos> sourceJars) {
        this.sourceJars = new ArrayList<>(sourceJars);
    }

    public List<BlockPos> getJars() {
        return sourceJars;
    }

    public boolean isEmpty() {
        return sourceJars.isEmpty();
    }

    public static final Codec<ChargeSourceListMemory> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BlockPos.CODEC.listOf().fieldOf("charge_source_list").forGetter(ChargeSourceListMemory::getJars)
            ).apply(instance, ChargeSourceListMemory::new)
    );

    public static ChargeSourceListMemory getMemory(EntityMaid maid) {
        if (MemoryModuleRegistry.CHARGE_SOURCE_LIST.isPresent()) {
            return maid.getBrain().getMemory(MemoryModuleRegistry.CHARGE_SOURCE_LIST.get()).orElse(null);
        }
        return null;
    }

    public static void setMemory(EntityMaid maid, List<BlockPos> jars) {
        MemoryModuleRegistry.CHARGE_SOURCE_LIST.ifPresent(module ->
                maid.getBrain().setMemory(module, new ChargeSourceListMemory(jars))
        );
    }

    public static void clearMemory(EntityMaid maid) {
        MemoryModuleRegistry.CHARGE_SOURCE_LIST.ifPresent(module ->
                maid.getBrain().eraseMemory(module)
        );
    }
}


