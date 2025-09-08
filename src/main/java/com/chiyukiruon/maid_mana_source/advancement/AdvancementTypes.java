package com.chiyukiruon.maid_mana_source.advancement;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class AdvancementTypes {
    public static final String MANA_SOURCE = "mana_source";
    public static final String MAID_CHARGE = "maid_charge";
    public static final String CHARGE_MANA_POOL = "charge_mana_pool";
    public static final String DISABLE_ALL_SOURCE = "disable_all_source";


    public static void triggerForMaid(EntityMaid maid, String key) {
        LivingEntity player = maid.getOwner();
        if (player instanceof ServerPlayer sp) {
            InitTrigger.MAID_EVENT.trigger(sp, key);
        }
    }
}
