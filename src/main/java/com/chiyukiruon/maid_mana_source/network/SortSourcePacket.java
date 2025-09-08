package com.chiyukiruon.maid_mana_source.network;

import com.chiyukiruon.maid_mana_source.item.ItemSourceList;
import com.chiyukiruon.maid_mana_source.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SortSourcePacket(BlockPos pos, int direction) {
    public static void toBytes(SortSourcePacket msg, net.minecraft.network.FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeInt(msg.direction);
    }

    public SortSourcePacket(net.minecraft.network.FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readInt());
    }

    public static void handle(SortSourcePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ItemStack held = player.getMainHandItem();
            if (!held.is(ItemRegistry.SOURCE_LIST.get())) {
                held = player.getOffhandItem();
                if (!held.is(ItemRegistry.SOURCE_LIST.get())) return;
            }

            CompoundTag tag = held.getOrCreateTag();
            if (!tag.contains("SourceList")) return;

            ListTag list = tag.getList("SourceList", Tag.TAG_COMPOUND);
            ItemSourceList.moveEntry(list, msg.pos(), msg.direction());
            tag.put("SourceList", list);
            held.setTag(tag);
        });
        ctx.get().setPacketHandled(true);
    }
}

