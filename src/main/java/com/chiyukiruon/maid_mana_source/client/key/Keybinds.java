package com.chiyukiruon.maid_mana_source.client.key;

import com.chiyukiruon.maid_mana_source.MaidManaSource;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = MaidManaSource.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Keybinds {
    public static KeyMapping SORT_KEY;

    @SubscribeEvent
    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        SORT_KEY = new KeyMapping(
                "key.maid_mana_source.sort_source",
                GLFW.GLFW_KEY_LEFT_ALT,
                "key.categories.maid_mana_source"
        );
        event.register(SORT_KEY);
    }
}
