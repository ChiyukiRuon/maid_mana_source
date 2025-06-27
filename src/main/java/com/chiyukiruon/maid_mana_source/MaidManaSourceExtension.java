package com.chiyukiruon.maid_mana_source;

import com.chiyukiruon.maid_mana_source.data.MaidChargeConfig;
import com.chiyukiruon.maid_mana_source.data.MaidConfigKeys;
import com.chiyukiruon.maid_mana_source.registry.MemoryModuleRegistry;
import com.chiyukiruon.maid_mana_source.task.MaidManaSourceTask;
import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.api.entity.ai.IExtraMaidBrain;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.ExtraMaidBrainManager;
import com.github.tartaricacid.touhoulittlemaid.entity.data.TaskDataRegister;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.List;

@LittleMaidExtension
public class MaidManaSourceExtension implements ILittleMaid {

    @Override
    public void addMaidTask(TaskManager taskManager) {
        ILittleMaid.super.addMaidTask(taskManager);
        taskManager.add(new MaidManaSourceTask());
    }

    @Override
    public void addExtraMaidBrain(ExtraMaidBrainManager taskManager) {
        taskManager.addExtraMaidBrain(new IExtraMaidBrain() {
            @Override
            public List<MemoryModuleType<?>> getExtraMemoryTypes() {
                return List.of(
                        MemoryModuleRegistry.SOURCE_JAR_LIST.get(),
                        MemoryModuleRegistry.CHARGE_INDEX.get()
                );
            }
        });
    }

    @Override
    public void registerTaskData(TaskDataRegister register) {
        MaidConfigKeys.addKey(MaidChargeConfig.LOCATION,
                MaidChargeConfig.KEY = register.register(new MaidChargeConfig()),
                MaidChargeConfig.Data::getDefault);
    }
}