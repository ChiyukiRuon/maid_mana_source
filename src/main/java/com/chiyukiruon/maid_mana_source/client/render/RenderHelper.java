package com.chiyukiruon.maid_mana_source.client.render;

import com.chiyukiruon.maid_mana_source.Config;
import com.chiyukiruon.maid_mana_source.mixin.LevelRendererAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public class RenderHelper {
    public static boolean highlightPenetration = Config.enableHighlightPenetration;
    public static boolean numbersOnly = Config.numbersOnly;
    public static double listOverlayYOffset = Config.listOverlayYOffset;

    /**
     * 渲染 SourceList 的序号标签
     *
     * @param event     渲染事件
     * @param pos       方块坐标
     * @param index     序号（1-based）
     * @param enabled   是否启用（决定文字颜色）
     */
    public static void renderIndexLabel(RenderLevelStageEvent event, BlockPos pos, int index, boolean enabled) {
        Minecraft mc = Minecraft.getInstance();
        PoseStack poseStack = event.getPoseStack();

        Vec3 cameraPos = event.getCamera().getPosition();
        Vec3 targetWorldPos = new Vec3(pos.getX() + 0.5, pos.getY() + listOverlayYOffset, pos.getZ() + 0.5);
        Vec3 renderPos = targetWorldPos.subtract(cameraPos);

        poseStack.pushPose();
        poseStack.translate(renderPos.x, renderPos.y, renderPos.z);

        poseStack.mulPose(Axis.YP.rotationDegrees(-event.getCamera().getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(event.getCamera().getXRot()));

        float scale = -0.025f;
        poseStack.scale(scale, scale, scale);

        Component text = Component.translatable("overlay.maid_mana_source.source_list_number", String.valueOf(index));
        if (numbersOnly) {
            text = Component.nullToEmpty(String.valueOf(index));
        }
        float xOffset = -mc.font.width(text) / 2.0f; // 文字居中
        int color = enabled ? 0xFFFFFFFF : 0xFFFF0000;

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        mc.font.drawInBatch(
                text,
                xOffset,
                0,
                color,
                false, // shadow
                poseStack.last().pose(),
                bufferSource,
                highlightPenetration ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL,
                0,
                15728880
        );

        bufferSource.endBatch();
        poseStack.popPose();
    }

    public static void renderBlockOutline(PoseStack poseStack, Camera camera, BlockPos pos, int color) {
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

        VertexConsumer buffer = mc.renderBuffers().bufferSource().getBuffer(
                highlightPenetration ? OutlineNoDepthRenderType.getOutlineNoDepth() : RenderType.LINES
        );
        LevelRendererAccessor.callRenderShape(poseStack, buffer, shape, 0, 0, 0, r, g, b, a);

        poseStack.popPose();
    }
}
