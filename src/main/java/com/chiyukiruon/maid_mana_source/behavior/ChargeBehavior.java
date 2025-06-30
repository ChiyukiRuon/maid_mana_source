package com.chiyukiruon.maid_mana_source.behavior;

import com.chiyukiruon.maid_mana_source.Config;
import com.chiyukiruon.maid_mana_source.data.MaidChargeConfig;
import com.chiyukiruon.maid_mana_source.memory.SourceJarMemory;
import com.chiyukiruon.maid_mana_source.registry.MemoryModuleRegistry;
import com.chiyukiruon.maid_mana_source.util.MaidAiUtil;
import com.chiyukiruon.maid_mana_source.util.MemoryUtil;
import com.chiyukiruon.maid_mana_source.util.TargetUtil;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ChargeBehavior extends Behavior<EntityMaid> {
    private final Block sourceJarBlock
            = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("ars_nouveau", "source_jar"));
    private static final Map<Integer, Long> COOLDOWNS = new HashMap<>();

    public ChargeBehavior() {
        super(Map.of());
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, EntityMaid maid) {
        return !maid.isSleeping() && sourceJarBlock != null;
    }

    @Override
    protected void start(ServerLevel level, EntityMaid maid, long gameTime) {
        List<BlockPos> knownJars = Optional.ofNullable(MemoryUtil.getSourceJarMemory(maid))
                .map(SourceJarMemory::getJars)
                .orElse(List.of())
                .stream()
                .toList();

        if (knownJars.isEmpty()) return;

        long currentTime = level.getGameTime();
        if (COOLDOWNS.getOrDefault(maid.getId(), 0L) > currentTime) return;

        MaidChargeConfig.Data config = maid.getOrCreateData(MaidChargeConfig.KEY, MaidChargeConfig.Data.getDefault());
        boolean chargeMode = config.chargeMode();
        boolean chargeStrategy = config.chargeStrategy();

        int chargeThisTime = Config.maxPerCharge;
        int chargeAmount = TargetUtil.getChargeAmount(level, knownJars);
        int coolingTime = Config.coolingTime;

        if (Config.enableFavorEffect) {
            int maidFavorLevel = maid.getFavorabilityManager().getLevel();
            chargeThisTime += maidFavorLevel * Config.favorChargeBonus;
            coolingTime -= maidFavorLevel * Config.favorCooldownReduction;

            chargeThisTime = Math.max(chargeThisTime, 0);
            coolingTime = Math.max(coolingTime, 0);
        }

        // 批量充能
        if (!chargeMode && knownJars.size() > 1 && chargeAmount != 0) {
            chargeThisTime /= chargeAmount;

            for (BlockPos pos : knownJars) {
                ISourceTile jar = (ISourceTile) level.getBlockEntity(pos);
                if (jar == null) continue;
                if (jar.canAcceptSource()) {
                    MaidAiUtil.setWalkAndLookTargetMemories(maid, pos, 0.5);
                    doCharge(level, pos, chargeThisTime);
                }
            }

            COOLDOWNS.put(maid.getId(), currentTime + coolingTime);
            return;
        }

        // 单个充能
        if (chargeStrategy) {
            // 轮询模式
            int index = maid.getBrain().getMemory(MemoryModuleRegistry.CHARGE_INDEX.get()).orElse(0);
            BlockPos pos = knownJars.get(index % knownJars.size());
            ISourceTile jar = (ISourceTile) level.getBlockEntity(pos);

            if (jar == null) {
                maid.getBrain().setMemory(MemoryModuleRegistry.CHARGE_INDEX.get(), (index + 1) % knownJars.size());
                return;
            }
            if (jar.canAcceptSource()) {
                MaidAiUtil.setWalkAndLookTargetMemories(maid, pos, 0.5);
                doCharge(level, pos, chargeThisTime);
                COOLDOWNS.put(maid.getId(), currentTime + coolingTime);
            }

            maid.getBrain().setMemory(MemoryModuleRegistry.CHARGE_INDEX.get(), (index + 1) % knownJars.size());
        } else {
            // 顺序模式
            for (BlockPos pos : knownJars) {
                ISourceTile jar = (ISourceTile) level.getBlockEntity(pos);
                if (jar ==  null) continue;
                if (jar.canAcceptSource()) {
                    MaidAiUtil.setWalkAndLookTargetMemories(maid, pos, 0.5);
                    doCharge(level, pos, chargeThisTime);
                    COOLDOWNS.put(maid.getId(), currentTime + coolingTime);
                    break;
                }
            }
        }

    }

    private void doCharge(ServerLevel level, BlockPos pos, int charge) {
        BlockEntity block = level.getBlockEntity(pos);

        if (block instanceof ISourceTile sourceTile) {
            sourceTile.addSource(charge);
            level.levelEvent(2005, pos, 0); // 播放粒子
        }
    }
}
