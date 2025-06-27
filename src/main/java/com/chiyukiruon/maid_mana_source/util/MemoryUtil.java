package com.chiyukiruon.maid_mana_source.util;

import com.chiyukiruon.maid_mana_source.memory.SourceJarMemory;
import com.chiyukiruon.maid_mana_source.registry.MemoryModuleRegistry;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MemoryUtil {
    public static @Nullable SourceJarMemory getSourceJarMemory(EntityMaid maid) {
        if (MemoryModuleRegistry.SOURCE_JAR_LIST.isPresent()) {
            return maid.getBrain().getMemory(MemoryModuleRegistry.SOURCE_JAR_LIST.get()).orElse(null);
        }
        return null;
    }

    public static void setSourceJarMemory(EntityMaid maid, List<BlockPos> jars) {
        MemoryModuleRegistry.SOURCE_JAR_LIST.ifPresent(module ->
                maid.getBrain().setMemory(module, new SourceJarMemory(jars))
        );
    }

    public static void clearSourceJarMemory(EntityMaid maid) {
        MemoryModuleRegistry.SOURCE_JAR_LIST.ifPresent(module ->
                maid.getBrain().eraseMemory(module)
        );
    }
}
