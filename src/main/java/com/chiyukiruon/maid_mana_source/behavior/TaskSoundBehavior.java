package com.chiyukiruon.maid_mana_source.behavior;

import com.chiyukiruon.maid_mana_source.Config;
import com.chiyukiruon.maid_mana_source.memory.ChargeSourceListMemory;
import com.chiyukiruon.maid_mana_source.util.I18nUtil;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TaskSoundBehavior extends Behavior<EntityMaid> {
    private boolean wasWorking = false;
    private long chatBubbleKey = 0;

    public TaskSoundBehavior() {
        super(Map.of());
    }

    @Override
    protected boolean checkExtraStartConditions(@NotNull ServerLevel level, @NotNull EntityMaid maid) {
        return Config.maidTaskSound;
    }

    @Override
    protected void start(@NotNull ServerLevel level, @NotNull EntityMaid maid, long gameTime) {
        List<BlockPos> jars = Optional.ofNullable(ChargeSourceListMemory.getMemory(maid))
                .map(ChargeSourceListMemory::getJars)
                .orElse(List.of());

        // 判断是否还有可充能的魔源罐
        boolean isWorking = jars.stream()
                .map(level::getBlockEntity)
                .filter(e -> e instanceof ISourceTile)
                .map(e -> (ISourceTile) e)
                .anyMatch(ISourceTile::canAcceptSource);

        if (!wasWorking && isWorking) {
            // 有新的魔源罐可充能
            maid.getChatBubbleManager().removeChatBubble(chatBubbleKey);
            chatBubbleKey = maid.getChatBubbleManager().addTextChatBubble(
                    I18nUtil.getRandomLangKey("chat_bubbles.maid_mana_source.working")
            );
            maid.playSound(
                    InitSounds.MAID_FIND_TARGET.get(),
                    1.0f,
                    1.0f
            );
        } else if (wasWorking && !isWorking) {
            // 空闲
            maid.getChatBubbleManager().removeChatBubble(chatBubbleKey);
            chatBubbleKey = maid.getChatBubbleManager().addTextChatBubble(
                    I18nUtil.getRandomLangKey("chat_bubbles.maid_mana_source.idle")
            );
            maid.playSound(
                    InitSounds.MAID_IDLE.get(),
                    1.0f,
                    1.0f
            );
        }

        wasWorking = isWorking;
    }
}

