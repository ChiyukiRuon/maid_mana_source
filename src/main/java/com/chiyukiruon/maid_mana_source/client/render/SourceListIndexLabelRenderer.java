package com.chiyukiruon.maid_mana_source.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class SourceListIndexLabelRenderer {

    /**
     * 渲染 SourceList 的序号标签
     *
     * @param poseStack 渲染矩阵
     * @param camera    玩家相机
     * @param pos       方块坐标
     * @param index     序号（1-based）
     * @param enabled   是否启用（决定文字颜色）
     */
    public static void renderIndexLabel(PoseStack poseStack, Camera camera, BlockPos pos, int index, boolean enabled) {
        Minecraft mc = Minecraft.getInstance();
        double camX = camera.getPosition().x;
        double camY = camera.getPosition().y;
        double camZ = camera.getPosition().z;

        // 文本在方块正上方 0.5 格
        double x = pos.getX() + 0.5 - camX;
        double y = pos.getY() + 1.2 - camY;
        double z = pos.getZ() + 0.5 - camZ;

        poseStack.pushPose();
        poseStack.translate(x, y, z);

        poseStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());

        float scale = 0.02f;
        poseStack.scale(-scale, -scale, scale);

        Component text = Component.translatable("overlay.maid_mana_source.source_list_number", String.valueOf(index));
        FormattedCharSequence sequence = text.getVisualOrderText();
        float xOffset = -mc.font.width(sequence) / 2f;

        int color = enabled ? 0xFFFFFFFF : 0xFFFF0000;

        mc.font.drawInBatch8xOutline(
                sequence,
                xOffset, 0,
                color,
                0xFF000000,
                poseStack.last().pose(),
                mc.renderBuffers().bufferSource(),
                15728880
        );

        poseStack.popPose();
    }
}
