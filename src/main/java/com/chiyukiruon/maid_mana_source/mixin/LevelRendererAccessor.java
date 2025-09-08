package com.chiyukiruon.maid_mana_source.mixin;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {
    @Invoker("renderShape")
    static void callRenderShape(PoseStack poseStack, VertexConsumer buffer, VoxelShape shape,
                                double x, double y, double z,
                                float red, float green, float blue, float alpha) {
        throw new AssertionError();
    }
}
