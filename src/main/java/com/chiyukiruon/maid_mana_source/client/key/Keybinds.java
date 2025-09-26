package com.chiyukiruon.maid_mana_source.client.key;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber
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
