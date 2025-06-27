package com.chiyukiruon.maid_mana_source;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = MaidManaSource.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue COOLING_TIME = BUILDER
            .comment("The base cooldown time (in ticks) after each mana charging action")
            .defineInRange("coolingTime", 200, 0, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue MAX_PER_CHARGE = BUILDER
            .comment("The base maximum mana amount added to source jar per charge")
            .defineInRange("maxPerCharge", 500, 0, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue SCAN_INTERVAL = BUILDER
            .comment("Interval between source jar searches (in ticks)")
            .defineInRange("scanInterval", 200, 1, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.BooleanValue ENABLE_FAVOR_EFFECT = BUILDER
            .comment("If true, maid's favorability affects charging amount and cooldown")
            .define("enableFavorEffect", false);
    private static final ForgeConfigSpec.IntValue FAVOR_CHARGE_BONUS = BUILDER
            .comment("Extra mana per levels (only effective if enableFavorEffect = true)")
            .defineInRange("favorChargeBonus", 100, Integer.MIN_VALUE, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue FAVOR_COOLDOWN_REDUCTION = BUILDER
            .comment("Cooldown reduction (in ticks) per levels (only effective if enableFavorEffect = true)")
            .defineInRange("favorCooldownReduction", 10, Integer.MIN_VALUE, Integer.MAX_VALUE);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int coolingTime;
    public static int maxPerCharge;
    public static int scanInterval;
    public static boolean enableFavorEffect;
    public static int favorChargeBonus;
    public static int favorCooldownReduction;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        coolingTime = COOLING_TIME.get();
        maxPerCharge = MAX_PER_CHARGE.get();
        scanInterval = SCAN_INTERVAL.get();
        enableFavorEffect = ENABLE_FAVOR_EFFECT.get();
        favorChargeBonus = FAVOR_CHARGE_BONUS.get();
        favorCooldownReduction = FAVOR_COOLDOWN_REDUCTION.get();
    }
}
