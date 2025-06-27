package com.chiyukiruon.maid_mana_source.network;

import com.chiyukiruon.maid_mana_source.MaidManaSource;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class Network {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MaidManaSource.MODID, "mut_packets"),
            () -> PROTOCOL_VERSION,
            (v) -> true,
            (v) -> true
    );

    private static void registerMessage() {
        Network.INSTANCE.registerMessage(0,
                MaidConfigurePacket.class,
                MaidConfigurePacket::toBytes,
                MaidConfigurePacket::new,
                (msg, context) -> {
                    context.get().enqueueWork(() -> MaidConfigurePacket.handle(msg, context));
                    context.get().setPacketHandled(true);
                }
        );
    }

    @OnlyIn(Dist.CLIENT)
    public static Player getLocalPlayer() {
        return Minecraft.getInstance().player;
    }

    @Mod.EventBusSubscriber(modid = MaidManaSource.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
    public static class Server {
        @SubscribeEvent
        public static void FMLClientSetupEvent(FMLDedicatedServerSetupEvent event) {
            registerMessage();
        }
    }

    @Mod.EventBusSubscriber(modid = MaidManaSource.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Client {
        @SubscribeEvent
        public static void FMLClientSetupEvent(FMLClientSetupEvent event) {
            registerMessage();
        }

    }
}
