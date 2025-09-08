package com.chiyukiruon.maid_mana_source.behavior;

import com.chiyukiruon.maid_mana_source.Config;
import com.chiyukiruon.maid_mana_source.advancement.AdvancementTypes;
import com.chiyukiruon.maid_mana_source.memory.ChargeSourceListMemory;
import com.chiyukiruon.maid_mana_source.memory.ScannedSourceListMemory;
import com.chiyukiruon.maid_mana_source.util.TargetUtil;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class ScanBehavior extends Behavior<EntityMaid> {
    public ScanBehavior() {
        super(Map.of());
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, @NotNull EntityMaid maid) {
        AdvancementTypes.triggerForMaid(maid, AdvancementTypes.MANA_SOURCE);
        int scanInterval = maid.isHomeModeEnable() ? Config.scanInterval : Math.max(1, Config.scanInterval / 2);
        return level.getGameTime() % scanInterval == 0;
    }

    @Override
    protected void start(@NotNull ServerLevel level, EntityMaid maid, long gameTime) {
        BlockPos center = maid.isHomeModeEnable() ? maid.getRestrictCenter() : maid.blockPosition();
        int radius = (int) maid.getRestrictRadius();

        int radiusSq = radius * radius;
        List<BlockPos> nearbyJars = BlockPos.betweenClosedStream(
                        center.offset(-radius, 0, -radius),
                        center.offset(radius, radius, radius)
                )
                .map(BlockPos::immutable)
                .filter(pos -> pos.getY() >= center.getY()
                        && pos.distSqr(center) <= radiusSq
                        && TargetUtil.isTargetISourceTile(level, pos))
                .toList();

        ChargeSourceListMemory.setMemory(maid, nearbyJars);
        ScannedSourceListMemory.setMemory(maid, nearbyJars);
    }
}
