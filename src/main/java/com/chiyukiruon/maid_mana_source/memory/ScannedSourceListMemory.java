package com.chiyukiruon.maid_mana_source.memory;

import com.chiyukiruon.maid_mana_source.registry.MemoryModuleRegistry;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ScannedSourceListMemory {
    private final List<BlockPos> scannedSourceList;

    public ScannedSourceListMemory(List<BlockPos> sourceJars) {
        this.scannedSourceList = new ArrayList<>(sourceJars);
    }

    public List<BlockPos> getJars() {
        return scannedSourceList;
    }

    public boolean isEmpty() {
        return scannedSourceList.isEmpty();
    }

    public static final Codec<ScannedSourceListMemory> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BlockPos.CODEC.listOf().fieldOf("scanned_source_list").forGetter(ScannedSourceListMemory::getJars)
            ).apply(instance, ScannedSourceListMemory::new)
    );

    public static ScannedSourceListMemory getMemory(EntityMaid maid) {
        return maid.getBrain().getMemory(MemoryModuleRegistry.SCANNED_SOURCE_LIST.get()).orElse(null);
    }

    public static void setMemory(EntityMaid maid, List<BlockPos> jars) {
        maid.getBrain().setMemory(MemoryModuleRegistry.SCANNED_SOURCE_LIST.get(), new ScannedSourceListMemory(jars));
    }

    public static @NotNull ListTag initializeSourceListNBT(EntityMaid maid) {
        ScannedSourceListMemory scanned = ScannedSourceListMemory.getMemory(maid);
        if (scanned != null) {
            List<BlockPos> scannedJars = new ArrayList<>(scanned.getJars());
            ListTag listTag = new ListTag();

            for (BlockPos pos : scannedJars) {
                CompoundTag entry = new CompoundTag();
                entry.putInt("x", pos.getX());
                entry.putInt("y", pos.getY());
                entry.putInt("z", pos.getZ());
                entry.putBoolean("enabled", true); // 默认启用所有位置
                listTag.add(entry);
            }

            return listTag;
        }
        return new ListTag();
    }
}
