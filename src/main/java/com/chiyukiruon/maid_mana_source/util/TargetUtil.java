package com.chiyukiruon.maid_mana_source.util;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class TargetUtil {
    /**
    * 判断是否目标方块
    *
    * @param level 服务器世界
    * @param pos 方块坐标
    * @param targetBlock 目标方块
    * @return boolean
    * @author ChiyukiRuon
    * */
    public static boolean isTargetBlock(ServerLevel level, BlockPos pos, Block targetBlock) {
        return level.getBlockState(pos).is(targetBlock);
    }

    /**
     * 能否充能
     *
     * @param level 服务器世界
     * @param pos 方块坐标
     * @return boolean
     * @author ChiyukiRuon
     * */
    public static boolean canCharge(ServerLevel level, BlockPos pos) {
        try {
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ISourceTile tile) {
                return tile.canAcceptSource();
            }
        } catch (Exception e) {
            System.err.println("canCharge error at " + pos + ": " + e);
        }

        return false;
    }


    /**
     * 获取可充能的魔源数量
     *
     * @param level 服务器世界
     * @param sourceJarPos 魔源坐标列表
     * @return int
     * @author ChiyukiRuon
     * */
    public static int getChargeAmount(ServerLevel level, List<BlockPos> sourceJarPos) {
        int amount = 0;
        for (BlockPos pos : sourceJarPos) {
            if (canCharge(level, pos)) amount ++;
        }

        return amount;
    }
}
