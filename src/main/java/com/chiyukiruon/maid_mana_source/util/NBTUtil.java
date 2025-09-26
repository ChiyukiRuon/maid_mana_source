package com.chiyukiruon.maid_mana_source.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NBTUtil {

    /**
     * 获取物品堆的 NBT，如果不存在则返回 null
     *
     * @param stack 物品堆
     * @return CompoundTag
     * @author ChiyukiRuon
     */
    public static @Nullable CompoundTag getTag(@NotNull ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            return data.copyTag();
        }
        return null;
    }

    /**
     * 获取或创建物品堆的 NBT，如果没有则新建一个
     *
     * @param stack 物品堆
     * @return CompoundTag
     * @author ChiyukiRuon
     */
    public static @NotNull CompoundTag getOrCreateTag(@NotNull ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            CompoundTag newTag = new CompoundTag();
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(newTag));
            return newTag;
        }
        return data.copyTag();
    }

    /**
     * 设置/覆盖物品堆的 NBT
     *
     * @param stack 物品堆
     * @param tag 要写入的 NBT
     * @author ChiyukiRuon
     */
    public static void setTag(@NotNull ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}
