package com.chiyukiruon.maid_mana_source.behavior;

import com.chiyukiruon.maid_mana_source.Config;
import com.chiyukiruon.maid_mana_source.advancement.AdvancementTypes;
import com.chiyukiruon.maid_mana_source.data.MaidChargeConfig;
import com.chiyukiruon.maid_mana_source.memory.ChargeSourceListMemory;
import com.chiyukiruon.maid_mana_source.registry.MemoryModuleRegistry;
import com.chiyukiruon.maid_mana_source.util.MaidAiUtil;
import com.chiyukiruon.maid_mana_source.util.TargetUtil;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ChargeBehavior extends Behavior<EntityMaid> {
    private static final Map<Integer, Long> COOLDOWNS = new HashMap<>();

    public ChargeBehavior() {
        super(Map.of());
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, EntityMaid maid) {
        return COOLDOWNS.getOrDefault(maid.getId(), 0L) < level.getGameTime();
    }

    @Override
    protected void start(@NotNull ServerLevel level, @NotNull EntityMaid maid, long gameTime) {
        List<BlockPos> knownJars = Optional.ofNullable(ChargeSourceListMemory.getMemory(maid))
                .map(ChargeSourceListMemory::getJars)
                .orElse(List.of())
                .stream()
                .toList();

        if (knownJars.isEmpty()) return;

        long currentTime = level.getGameTime();

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
                    doCharge(maid, level, pos, chargeThisTime);
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
                doCharge(maid, level, pos, chargeThisTime);
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
                    doCharge(maid, level, pos, chargeThisTime);
                    COOLDOWNS.put(maid.getId(), currentTime + coolingTime);
                    break;
                }
            }
        }

    }

    private void doCharge(EntityMaid maid, @NotNull ServerLevel level, BlockPos pos, int charge) {
        BlockEntity block = level.getBlockEntity(pos);
        BlockState state = level.getBlockState(pos);

        if (block instanceof ISourceTile sourceTile) {
            if (!sourceTile.canAcceptSource()) return;
            sourceTile.addSource(charge);
            level.sendParticles(
                    ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    Config.chargeParticleCount,
                    Config.chargeParticleRadius,
                    Config.chargeParticleRadius,
                    Config.chargeParticleRadius,
                    0.01
            );
            if (ModList.get().isLoaded("botania") && ModList.get().isLoaded("ars_botania")) {
                if (TargetUtil.isBlockFromMod(state, "botania")) {
                    AdvancementTypes.triggerForMaid(maid, AdvancementTypes.CHARGE_MANA_POOL);
                }
            }
            AdvancementTypes.triggerForMaid(maid, AdvancementTypes.MAID_CHARGE);
            if (!sourceTile.canAcceptSource() && Config.chargingCompletedSound) {
                level.playSound(
                        null,
                        pos,
                        SoundEvents.EXPERIENCE_ORB_PICKUP,
                        SoundSource.BLOCKS,
                        1.0f,
                        1.0f
                );
            }
        }
    }
}
