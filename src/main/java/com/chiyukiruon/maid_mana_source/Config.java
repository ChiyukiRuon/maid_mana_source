package com.chiyukiruon.maid_mana_source;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // 充能行为设置
    private static final ModConfigSpec.IntValue COOLING_TIME = BUILDER
            .comment("The base cooldown time (in ticks) after each mana charging action")
            .defineInRange("coolingTime", 200, 0, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue MAX_PER_CHARGE = BUILDER
            .comment("The base maximum mana amount added to source jar per charge")
            .defineInRange("maxPerCharge", 200, 0, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue SCAN_INTERVAL = BUILDER
            .comment("Interval between source jar searches (in ticks)")
            .defineInRange("scanInterval", 200, 1, Integer.MAX_VALUE);
    private static final ModConfigSpec.BooleanValue ENABLE_FAVOR_EFFECT = BUILDER
            .comment("If true, maid's favorability affects charging amount and cooldown")
            .define("enableFavorEffect", false);
    private static final ModConfigSpec.IntValue FAVOR_CHARGE_BONUS = BUILDER
            .comment("Extra mana per levels (only effective if enableFavorEffect = true)")
            .defineInRange("favorChargeBonus", 100, Integer.MIN_VALUE, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue FAVOR_COOLDOWN_REDUCTION = BUILDER
            .comment("Cooldown reduction (in ticks) per levels (only effective if enableFavorEffect = true)")
            .defineInRange("favorCooldownReduction", 20, Integer.MIN_VALUE, Integer.MAX_VALUE);
    // 显示设置
    private static final ModConfigSpec.BooleanValue ENABLE_HIGHLIGHT_PENETRATION = BUILDER
            .comment("If true, highlights will show through the blocks")
            .define("enableHighlightPenetration", true);
    private static final ModConfigSpec.DoubleValue LIST_OVERLAY_TEXT_Y_OFFSET = BUILDER
            .comment("The Y offset of the charging number display")
            .defineInRange("listOverlayYOffset", 1.3, Double.MIN_VALUE, Double.MAX_VALUE);
    private static final ModConfigSpec.BooleanValue NUMBERS_ONLY = BUILDER
            .comment("If true, only numbers will be displayed")
            .define("numbersOnly", false);
    private static final ModConfigSpec.IntValue CHARGE_PARTICLE_COUNT = BUILDER
            .comment("The number of particles to be displayed when charging")
            .defineInRange("chargeParticleCount", 20, 0, Integer.MAX_VALUE);
    private static final ModConfigSpec.DoubleValue CHARGE_PARTICLE_RADIUS = BUILDER
            .comment("The radius of the particles to be displayed when charging")
            .defineInRange("chargeParticleRadius", 0.5, 0.0, Double.MAX_VALUE);
    // 声音设置
    private static final ModConfigSpec.BooleanValue MAID_TASK_SOUND = BUILDER
            .comment("If true, maid will play sound when starting and ending tasks")
            .define("maidTaskSound", true);
    private static final ModConfigSpec.BooleanValue CHARGING_COMPLETED_SOUND = BUILDER
            .comment("If true, a sound will be played when the Source Jar is fully charged.")
            .define("chargingCompletedSound", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int coolingTime;
    public static int maxPerCharge;
    public static int scanInterval;
    public static boolean enableFavorEffect;
    public static int favorChargeBonus;
    public static int favorCooldownReduction;
    public static boolean enableHighlightPenetration;
    public static double listOverlayYOffset;
    public static boolean numbersOnly;
    public static int chargeParticleCount;
    public static double chargeParticleRadius;
    public static boolean maidTaskSound;
    public static boolean chargingCompletedSound;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        coolingTime = COOLING_TIME.get();
        maxPerCharge = MAX_PER_CHARGE.get();
        scanInterval = SCAN_INTERVAL.get();
        enableFavorEffect = ENABLE_FAVOR_EFFECT.get();
        favorChargeBonus = FAVOR_CHARGE_BONUS.get();
        favorCooldownReduction = FAVOR_COOLDOWN_REDUCTION.get();
        enableHighlightPenetration = ENABLE_HIGHLIGHT_PENETRATION.get();
        listOverlayYOffset = LIST_OVERLAY_TEXT_Y_OFFSET.get();
        numbersOnly = NUMBERS_ONLY.get();
        chargeParticleCount = CHARGE_PARTICLE_COUNT.get();
        chargeParticleRadius = CHARGE_PARTICLE_RADIUS.get();
        maidTaskSound = MAID_TASK_SOUND.get();
        chargingCompletedSound = CHARGING_COMPLETED_SOUND.get();
    }
}
