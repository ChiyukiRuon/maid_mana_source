package com.chiyukiruon.maid_mana_source.network;

import com.chiyukiruon.maid_mana_source.data.MaidChargeConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class Network {
    private static final String PROTOCOL_VERSION = "1";

    public static void sendMaidConfigurePacket(MaidConfigurePacket.Type type, int maidId, Boolean value) {
        PacketDistributor.sendToServer(new MaidConfigurePacket(type, maidId, value));
    }

    public static void sendSortSourcePacket(BlockPos pos, int direction) {
        PacketDistributor.sendToServer(new SortSourcePacket(pos, direction));
    }

    // 注册所有数据包
    private static void registerMessages(PayloadRegistrar registrar) {
        // MaidConfigurePacket
        registrar.playToServer(
                MaidConfigurePacket.TYPE,
                MaidConfigurePacket.STREAM_CODEC,
                (msg, context) -> {
                    if (!(context.player() instanceof ServerPlayer sender)) return;
                    Entity entity = sender.level().getEntity(msg.maidId);
                    if (entity instanceof EntityMaid maid) {
                        if (msg.type == MaidConfigurePacket.Type.chargeMode) {
                            MaidChargeConfig.Data data = maid.getOrCreateData(
                                    MaidChargeConfig.KEY,
                                    MaidChargeConfig.Data.getDefault()
                            );
                            data.setChargeMode(msg.value);
                            maid.setAndSyncData(MaidChargeConfig.KEY, data);
                        }else if (msg.type == MaidConfigurePacket.Type.chargeStrategy) {
                            MaidChargeConfig.Data data = maid.getOrCreateData(
                                    MaidChargeConfig.KEY,
                                    MaidChargeConfig.Data.getDefault()
                            );
                            data.setChargeStrategy(msg.value);
                            maid.setAndSyncData(MaidChargeConfig.KEY, data);
                        }
                    }
                }
        );
        // SortSourcePacket
        registrar.playToServer(
                SortSourcePacket.TYPE,
                SortSourcePacket.STREAM_CODEC,
                (msg, context) -> {
                    if (context.player() instanceof ServerPlayer sender) {
                        context.enqueueWork(() -> SortSourcePacket.handle(msg, sender));
                    }
                }
        );
    }

    @EventBusSubscriber
    public static class ModEvents {
        @SubscribeEvent
        public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
            registerMessages(event.registrar(PROTOCOL_VERSION));
        }
    }
}