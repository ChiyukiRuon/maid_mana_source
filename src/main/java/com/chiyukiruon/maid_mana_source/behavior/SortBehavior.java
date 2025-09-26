package com.chiyukiruon.maid_mana_source.behavior;

import com.chiyukiruon.maid_mana_source.advancement.AdvancementTypes;
import com.chiyukiruon.maid_mana_source.entry.SortedSourceJarEntry;
import com.chiyukiruon.maid_mana_source.item.ItemSourceList;
import com.chiyukiruon.maid_mana_source.memory.ChargeSourceListMemory;
import com.chiyukiruon.maid_mana_source.memory.ScannedSourceListMemory;
import com.chiyukiruon.maid_mana_source.util.NBTUtil;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SortBehavior extends Behavior<EntityMaid> {
    public SortBehavior() {
        super(Map.of());
    }

    @Override
    protected boolean checkExtraStartConditions(@NotNull ServerLevel level, @NotNull EntityMaid maid) {
        return ChargeSourceListMemory.getMemory(maid) != null;
    }

    @Override
    protected void start(@NotNull ServerLevel level, @NotNull EntityMaid maid, long gameTime) {
        ScannedSourceListMemory scanned = ScannedSourceListMemory.getMemory(maid);
        if (scanned == null) return;
        List<BlockPos> scannedJars = new ArrayList<>(scanned.getJars());

        ItemStack sourceListItem = ItemSourceList.getSourceList(maid);
        if (sourceListItem.isEmpty()) return;

        CompoundTag tag = NBTUtil.getOrCreateTag(sourceListItem);
        ListTag listTag = tag.getList("SourceList", Tag.TAG_COMPOUND);

        List<SortedSourceJarEntry> stored = new ArrayList<>();
        int disabledCount = 0;
        for (Tag entryTag : listTag) {
            CompoundTag entry = (CompoundTag) entryTag;
            BlockPos pos = new BlockPos(entry.getInt("x"), entry.getInt("y"), entry.getInt("z"));
            boolean enabled = entry.getBoolean("enabled");
            if (!enabled) disabledCount++;
            stored.add(new SortedSourceJarEntry(pos, enabled));
        }
        if (disabledCount == listTag.size()) AdvancementTypes.triggerForMaid(maid, AdvancementTypes.DISABLE_ALL_SOURCE);

        Set<BlockPos> scannedSet = new HashSet<>(scannedJars);
        Set<BlockPos> storedSet = new HashSet<>();
        for (SortedSourceJarEntry e : stored) storedSet.add(e.getPos());

        for (BlockPos pos : scannedSet) {
            if (!storedSet.contains(pos)) {
                stored.add(new SortedSourceJarEntry(pos, true));
            }
        }

        stored.removeIf(e -> !scannedSet.contains(e.getPos()));

        List<BlockPos> finalList = new ArrayList<>();
        for (SortedSourceJarEntry e : stored) {
            if (e.isEnabled()) {
                finalList.add(e.getPos());
            }
        }

        ListTag newListTag = new ListTag();
        for (SortedSourceJarEntry e : stored) {
            CompoundTag entry = new CompoundTag();
            entry.putInt("x", e.getPos().getX());
            entry.putInt("y", e.getPos().getY());
            entry.putInt("z", e.getPos().getZ());
            entry.putBoolean("enabled", e.isEnabled());
            newListTag.add(entry);
        }
        tag.put("SourceList", newListTag);
        NBTUtil.setTag(sourceListItem, tag);

        ChargeSourceListMemory.setMemory(maid, finalList);
    }
}
