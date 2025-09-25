package com.chiyukiruon.maid_mana_source.data;

import com.chiyukiruon.maid_mana_source.MaidManaSource;
import com.github.tartaricacid.touhoulittlemaid.api.entity.data.TaskDataKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class MaidChargeConfig implements TaskDataKey<MaidChargeConfig.Data> {
    public static TaskDataKey<Data> KEY = null;
    public static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath(MaidManaSource.MODID, "charge_config");

    @Override
    public ResourceLocation getKey() {
        return LOCATION;
    }

    @Override
    public CompoundTag writeSaveData(Data data) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("chargeMode", data.chargeMode);
        tag.putBoolean("chargeStrategy", data.chargeStrategy);
        return tag;
    }

    @Override
    public Data readSaveData(CompoundTag tag) {
        boolean mode = tag.getBoolean("chargeMode");
        boolean strategy = tag.getBoolean("chargeStrategy");
        return new Data(mode, strategy);
    }

    public static class Data implements IConfigSetter {
        private boolean chargeMode = true;
        private boolean chargeStrategy = true;

        public Data() {}

        public Data(boolean chargeMode, boolean chargeStrategy) {
            this.chargeMode = chargeMode;
            this.chargeStrategy = chargeStrategy;
        }

        public static Data getDefault() {
            return new Data(true, true);
        }

        public boolean chargeMode() {
            return chargeMode;
        }

        public boolean chargeStrategy() {
            return chargeStrategy;
        }

        public void setChargeMode(boolean chargeMode) {
            this.chargeMode = chargeMode;
        }

        public void setChargeStrategy(boolean chargeStrategy) {
            this.chargeStrategy = chargeStrategy;
        }

        @Override
        public void setConfigValue(String name, String value) {
            switch (name) {
                case "chargeMode" -> chargeMode = Boolean.parseBoolean(value);
                case "chargeStrategy" -> chargeStrategy = Boolean.parseBoolean(value);
            }
        }
    }
}
