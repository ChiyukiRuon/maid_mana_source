package com.chiyukiruon.maid_mana_source.client.render;

import com.chiyukiruon.maid_mana_source.MaidManaSource;
import com.chiyukiruon.maid_mana_source.registry.ItemRegistry;
import com.chiyukiruon.maid_mana_source.util.NBTUtil;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = MaidManaSource.MODID, value = Dist.CLIENT)
public class SourceListRenderer {
    @SubscribeEvent
    public static void onRenderLevelLast(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ItemStack held = player.getMainHandItem();
        if (!held.is(ItemRegistry.SOURCE_LIST.get())) {
            held = player.getOffhandItem();
            if (!held.is(ItemRegistry.SOURCE_LIST.get())) return;
        }

        CompoundTag tag = NBTUtil.getTag(held);
        if (tag == null || !tag.contains("SourceList")) return;

        PoseStack poseStack = event.getPoseStack();
        Camera camera = mc.gameRenderer.getMainCamera();

        ListTag listTag = tag.getList("SourceList", Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag entry = listTag.getCompound(i);
            BlockPos pos = new BlockPos(entry.getInt("x"), entry.getInt("y"), entry.getInt("z"));
            boolean enabled = entry.getBoolean("enabled");

            if (!(player.level().getBlockEntity(pos) instanceof ISourceTile)) continue;

            // 渲染方块轮廓
            RenderHelper.renderBlockOutline(poseStack, camera, pos, enabled ? 0xFFFFFFFF : 0xFFFF0000);

            // 渲染序号标签
            RenderHelper.renderIndexLabel(event, pos, i + 1, enabled);
        }

        mc.renderBuffers().bufferSource().endBatch();
    }
}