package com.chiyukiruon.maid_mana_source.registry;

import com.chiyukiruon.maid_mana_source.MaidManaSource;
import com.chiyukiruon.maid_mana_source.menu.MaidChargeConfigGui;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = MaidManaSource.MODID, value = Dist.CLIENT)
public class ClientGuiRegistry {
    @SubscribeEvent
    public static void init(RegisterMenuScreensEvent event) {
        event.register(GuiRegistry.MAID_CHARGE_CONFIG_GUI.get(), MaidChargeConfigGui::new);
    }
}
