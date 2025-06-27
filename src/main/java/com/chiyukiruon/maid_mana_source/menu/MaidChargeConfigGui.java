package com.chiyukiruon.maid_mana_source.menu;

import com.chiyukiruon.maid_mana_source.data.MaidChargeConfig;
import com.chiyukiruon.maid_mana_source.network.MaidConfigurePacket;
import com.chiyukiruon.maid_mana_source.registry.GuiRegistry;
import com.chiyukiruon.maid_mana_source.util.TranslateUtil;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.task.MaidTaskConfigGui;
import com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button.MaidConfigButton;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.task.TaskConfigContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MaidChargeConfigGui extends MaidTaskConfigGui<MaidChargeConfigGui.Container> {
    private MaidChargeConfig.Data currentData;

    public MaidChargeConfigGui(Container screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    public static class Container extends TaskConfigContainer {
        public Container(int id, Inventory inventory, int entityId) {
            super(GuiRegistry.MAID_CHARGE_CONFIG_GUI.get(), id, inventory, entityId);
        }
    }

    @Override
    protected void initAdditionData() {
        this.currentData = this.maid.getOrCreateData(MaidChargeConfig.KEY, MaidChargeConfig.Data.getDefault());
        System.out.println();
        System.out.println(this.currentData);
        System.out.println();
    }

    @Override
    protected void initAdditionWidgets() {
        super.initAdditionWidgets();

        int startLeft = leftPos + 87;
        int startTop = topPos + 36;
        this.addRenderableWidget(new MaidConfigButton(startLeft, startTop,
                Component.translatable("gui.maid_mana_source.charge_mode"),
                TranslateUtil.getBooleanTranslate(this.currentData.chargeMode(), "chargeMode"),
                button -> {
                    this.currentData.setChargeMode(false);
                    button.setValue(TranslateUtil.getBooleanTranslate(false, "chargeMode"));
                    MaidConfigurePacket.send(this.maid, MaidChargeConfig.LOCATION, "chargeMode", "false");
                },
                button -> {
                    this.currentData.setChargeMode(true);
                    button.setValue(TranslateUtil.getBooleanTranslate(true, "chargeMode"));
                    MaidConfigurePacket.send(this.maid, MaidChargeConfig.LOCATION, "chargeMode", "true");
                }
        ));
        this.addRenderableWidget(new MaidConfigButton(startLeft, startTop + 13,
                Component.translatable("gui.maid_mana_source.charge_strategy"),
                TranslateUtil.getBooleanTranslate(this.currentData.chargeStrategy(), "chargeStrategy"),
                button -> {
                    this.currentData.setChargeStrategy(false);
                    button.setValue(TranslateUtil.getBooleanTranslate(false, "chargeStrategy"));
                    MaidConfigurePacket.send(this.maid, MaidChargeConfig.LOCATION, "chargeStrategy", "false");
                },
                button -> {
                    this.currentData.setChargeStrategy(true);
                    button.setValue(TranslateUtil.getBooleanTranslate(true, "chargeStrategy"));
                    MaidConfigurePacket.send(this.maid, MaidChargeConfig.LOCATION, "chargeStrategy", "true");
                }
        ));
    }
}
