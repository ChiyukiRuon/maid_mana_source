package com.chiyukiruon.maid_mana_source.util;

import net.minecraft.network.chat.Component;

public class TranslateUtil {
    public static Component getBooleanTranslate(boolean b) {
        return (b ? Component.translatable("gui.maid_mana_source.yes") : Component.translatable("gui.maid_mana_source.no"));
    }

    public static Component getBooleanTranslate(boolean b, String  key) {
        return switch (key) {
            case "chargeMode" ->
                    (b ? Component.translatable("gui.maid_mana_source.charge_mode.single") : Component.translatable("gui.maid_mana_source.charge_mode.multi"));
            case "chargeStrategy" ->
                    (b ? Component.translatable("gui.maid_mana_source.charge_strategy.polling") : Component.translatable("gui.maid_mana_source.charge_strategy.sequential"));
            default ->
                    (b ? Component.translatable("gui.maid_mana_source.yes") : Component.translatable("gui.maid_mana_source.no"));
        };
    }
}
