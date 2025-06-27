package com.chiyukiruon.maid_mana_source.memory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SourceJarMemory {
    private final List<BlockPos> sourceJars;

    public SourceJarMemory(List<BlockPos> sourceJars) {
        this.sourceJars = new ArrayList<>(sourceJars);
    }

    public List<BlockPos> getJars() {
        return sourceJars;
    }

    public boolean isEmpty() {
        return sourceJars.isEmpty();
    }

    public static final Codec<SourceJarMemory> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BlockPos.CODEC.listOf().fieldOf("source_jars").forGetter(SourceJarMemory::getJars)
            ).apply(instance, SourceJarMemory::new)
    );
}


