package com.chiyukiruon.maid_mana_source.network;

import com.chiyukiruon.maid_mana_source.MaidManaSource;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class MaidConfigurePacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MaidConfigurePacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(MaidManaSource.MODID, "maid_configure")
    );

    @Override
    public CustomPacketPayload.@NotNull Type<MaidConfigurePacket> type() {
        return TYPE;
    }

    public static StreamCodec<ByteBuf, MaidConfigurePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            t -> t.type.name(),
            ByteBufCodecs.INT,
            t -> t.maidId,
            ByteBufCodecs.BOOL,
            t -> t.value,
            MaidConfigurePacket::new
    );

    public enum Type {
        chargeMode,
        chargeStrategy,
    }

    public final Type type;
    public final int maidId;
    public final Boolean value;

    public MaidConfigurePacket(Type type, int maidId, Boolean value) {
        this.type = type;
        this.maidId = maidId;
        this.value = value;
    }

    public MaidConfigurePacket(String type, int maidId, Boolean value) {
        this.type = Type.valueOf(type);
        this.maidId = maidId;
        this.value = value;
    }
}
