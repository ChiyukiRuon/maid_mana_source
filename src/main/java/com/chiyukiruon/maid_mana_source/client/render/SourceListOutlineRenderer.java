package com.chiyukiruon.maid_mana_source.client.render;

import com.chiyukiruon.maid_mana_source.Config;
import com.chiyukiruon.maid_mana_source.MaidManaSource;
import com.chiyukiruon.maid_mana_source.mixin.LevelRendererAccessor;
import com.chiyukiruon.maid_mana_source.registry.ItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MaidManaSource.MODID, value = Dist.CLIENT)
public class SourceListOutlineRenderer {
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

        CompoundTag tag = held.getTag();
        if (tag == null || !tag.contains("SourceList")) return;

        PoseStack poseStack = event.getPoseStack();
        Camera camera = mc.gameRenderer.getMainCamera();

        ListTag listTag = tag.getList("SourceList", Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag entry = listTag.getCompound(i);
            BlockPos pos = new BlockPos(entry.getInt("x"), entry.getInt("y"), entry.getInt("z"));
            boolean enabled = entry.getBoolean("enabled");

            renderBlockOutline(poseStack, camera, pos, enabled ? 0xFFFFFFFF : 0xFFFF0000);
            SourceListIndexLabelRenderer.renderIndexLabel(poseStack, camera, pos, i + 1, enabled);
        }
    }

    private static void renderBlockOutline(PoseStack poseStack, Camera camera, BlockPos pos, int color) {
        Minecraft mc = Minecraft.getInstance();
        var level = mc.level;
        if (level == null) return;

        VoxelShape shape = level.getBlockState(pos).getShape(level, pos);
        if (shape.isEmpty()) return;

        double camX = camera.getPosition().x;
        double camY = camera.getPosition().y;
        double camZ = camera.getPosition().z;

        poseStack.pushPose();
        poseStack.translate(pos.getX() - camX, pos.getY() - camY, pos.getZ() - camZ);

        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        boolean highlightPenetration = Config.enableHighlightPenetration;
        VertexConsumer buffer = mc.renderBuffers().bufferSource().getBuffer(highlightPenetration ? OutlineNoDepthRenderType.getOutlineNoDepth() : RenderType.LINES);
        LevelRendererAccessor.callRenderShape(poseStack, buffer, shape, 0, 0, 0, r, g, b, a);

        poseStack.popPose();
    }
}