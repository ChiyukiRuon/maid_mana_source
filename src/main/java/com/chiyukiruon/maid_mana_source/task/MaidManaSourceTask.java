package com.chiyukiruon.maid_mana_source.task;

import com.chiyukiruon.maid_mana_source.MaidManaSource;
import com.chiyukiruon.maid_mana_source.behavior.ChargeBehavior;
import com.chiyukiruon.maid_mana_source.behavior.ScanBehavior;
import com.chiyukiruon.maid_mana_source.behavior.SortBehavior;
import com.chiyukiruon.maid_mana_source.behavior.TaskSoundBehavior;
import com.chiyukiruon.maid_mana_source.menu.MaidChargeConfigGui;
import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MaidManaSourceTask implements IMaidTask {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MaidManaSource.MODID, "mana_source");

    @Override
    public @NotNull ResourceLocation getUid() {
        return UID;
    }

    @Override
    public @NotNull ItemStack getIcon() {
        return BlockRegistry.CREATIVE_SOURCE_JAR.asItem().getDefaultInstance();
    }

    @Nullable
    @Override
    public SoundEvent getAmbientSound(@NotNull EntityMaid entityMaid) {
        return null;
    }

    @Override
    public @NotNull MenuProvider getTaskConfigGuiProvider(@NotNull EntityMaid maid) {
        return new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return Component.literal("");
            }

            @Override
            public AbstractContainerMenu createMenu(int index, @NotNull Inventory playerInventory, @NotNull Player player) {
                return new MaidChargeConfigGui.Container(index, playerInventory, maid.getId());
            }
        };
    }

    @Override
    public @NotNull List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(@NotNull EntityMaid entityMaid) {
        List<Pair<Integer, BehaviorControl<? super EntityMaid>>> list = new ArrayList<>();

        list.add(Pair.of(0, new ScanBehavior()));
        list.add(Pair.of(1, new SortBehavior()));
        list.add(Pair.of(2, new ChargeBehavior()));
        list.add(Pair.of(3, new TaskSoundBehavior()));
        return list;
    }
}
