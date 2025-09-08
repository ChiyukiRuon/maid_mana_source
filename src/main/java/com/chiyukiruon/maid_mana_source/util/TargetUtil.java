package com.chiyukiruon.maid_mana_source.util;

import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class TargetUtil {
    /**
     * 判断方块是否来自指定模组
     *
     * @param state 方块状态
     * @param modid 模组 ID
     * @return true 如果该方块属于此模组
     */
    public static boolean isBlockFromMod(BlockState state, String modid) {
        if (modid == null || modid.isEmpty()) return false;
        Block block = state.getBlock();
        ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(block);

        if (registryName != null) {
            return registryName.getNamespace().equals(modid);
        }

        return false;
    }

    /**
     * 判断坐标是否在列表中
     *
     * @param pos 坐标
     * @param list 列表
     * @return boolean
     * @author ChiyukiRuon
     * */
    public static boolean isTargetInList(BlockPos pos, ListTag  list) {
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            int x = entry.getInt("x");
            int y = entry.getInt("y");
            int z = entry.getInt("z");

            if (x == pos.getX() && y == pos.getY() && z == pos.getZ()) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取方块是否有魔力缓冲区
     *
     * @param level 服务器世界
     * @param pos 方块坐标
     * @return boolean
     * @author ChiyukiRuon
     * */
    public static boolean isTargetISourceTile(ServerLevel level, BlockPos pos) {
        try {
            var blockEntity = level.getBlockEntity(pos);
            return blockEntity instanceof ISourceTile;
        } catch (Exception e) {
            System.err.println("isTargetISourceTile error at " + pos + ": " + e);
        }

        return false;
    }

    /**
     * 能否充能
     *
     * @param level 服务器世界
     * @param pos 方块坐标
     * @return boolean
     * @author ChiyukiRuon
     * */
    public static boolean canTargetCharge(ServerLevel level, BlockPos pos) {
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
            if (canTargetCharge(level, pos)) amount ++;
        }

        return amount;
    }
}
