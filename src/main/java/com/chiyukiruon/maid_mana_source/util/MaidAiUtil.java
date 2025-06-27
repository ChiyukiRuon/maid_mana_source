package com.chiyukiruon.maid_mana_source.util;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

import java.util.Comparator;
import java.util.List;

public class MaidAiUtil {
    public static boolean isWithinReach(EntityMaid maid, BlockPos pos, double distance) {
        return maid.blockPosition().distSqr(pos) <= distance * distance;
    }

    public static BlockPos getNearest(BlockPos origin, List<BlockPos> positions) {
        return positions.stream()
                .min(Comparator.comparingDouble(p -> p.distSqr(origin)))
                .orElse(null);
    }

    public static void setWalkAndLookTargetMemories(EntityMaid maid, BlockPos pos, double speed) {
        maid.getBrain().setMemory(MemoryModuleType.WALK_TARGET,
                new WalkTarget(pos, (float) speed, 1));
        maid.getBrain().setMemory(MemoryModuleType.LOOK_TARGET,
                new BlockPosTracker(pos));
    }
}

