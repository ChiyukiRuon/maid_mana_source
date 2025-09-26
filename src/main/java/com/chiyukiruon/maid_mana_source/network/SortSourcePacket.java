package com.chiyukiruon.maid_mana_source.network;

import com.chiyukiruon.maid_mana_source.MaidManaSource;
import com.chiyukiruon.maid_mana_source.item.ItemSourceList;
import com.chiyukiruon.maid_mana_source.registry.ItemRegistry;
import com.chiyukiruon.maid_mana_source.util.NBTUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record SortSourcePacket(BlockPos pos, int direction) implements CustomPacketPayload {
    public static final Type<SortSourcePacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MaidManaSource.MODID, "sort_source")
    );

    public static final StreamCodec<ByteBuf, SortSourcePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SortSourcePacket::pos,
            ByteBufCodecs.INT, SortSourcePacket::direction,
            SortSourcePacket::new
    );

    @Override
    public @NotNull Type<SortSourcePacket> type() {
        return TYPE;
    }

    /**
     * 处理逻辑：移动 SourceList 内的条目
     */
    public static void handle(SortSourcePacket msg, ServerPlayer player) {
        if (player == null) return;

        ItemStack held = player.getMainHandItem();
        if (!held.is(ItemRegistry.SOURCE_LIST.get())) {
            held = player.getOffhandItem();
            if (!held.is(ItemRegistry.SOURCE_LIST.get())) return;
        }

        CompoundTag tag = NBTUtil.getOrCreateTag(held);
        if (!tag.contains("SourceList")) return;

        ListTag list = tag.getList("SourceList", Tag.TAG_COMPOUND);
        ItemSourceList.moveEntry(list, msg.pos(), msg.direction());
        tag.put("SourceList", list);
        NBTUtil.setTag(held, tag);
    }
}
