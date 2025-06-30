package com.chiyukiruon.maid_mana_source.behavior;

import com.chiyukiruon.maid_mana_source.Config;
import com.chiyukiruon.maid_mana_source.util.MemoryUtil;
import com.chiyukiruon.maid_mana_source.util.TargetUtil;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;

public class ScanBehavior extends Behavior<EntityMaid> {
    private final Block sourceJarBlock
            = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("ars_nouveau", "source_jar"));

    public ScanBehavior() {
        super(Map.of());
    }

    @Override
    protected void start(ServerLevel level, EntityMaid maid, long gameTime) {
        BlockPos center = maid.isHomeModeEnable() ? maid.getRestrictCenter() : maid.blockPosition();
        int radius = (int) maid.getRestrictRadius();
        int scanInterval = maid.isHomeModeEnable() ? Config.scanInterval : Math.max(1, Config.scanInterval / 2);

        if (gameTime % scanInterval == 0) {
            int radiusSq = radius * radius;
            List<BlockPos> nearbyJars = BlockPos.betweenClosedStream(
                            center.offset(-radius, 0, -radius),
                            center.offset(radius, radius, radius)
                    )
                    .map(BlockPos::immutable)
                    .filter(pos -> pos.getY() >= center.getY()
                            && pos.distSqr(center) <= radiusSq
                            && TargetUtil.canCharge(level, pos))
                    .toList();

            MemoryUtil.setSourceJarMemory(maid, nearbyJars);
        }
    }
}
