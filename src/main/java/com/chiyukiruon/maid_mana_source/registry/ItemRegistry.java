package com.chiyukiruon.maid_mana_source.registry;

import com.chiyukiruon.maid_mana_source.MaidManaSource;
import com.chiyukiruon.maid_mana_source.item.ItemSourceList;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS
            = DeferredRegister.create(Registries.ITEM, MaidManaSource.MODID);

    protected static RegistryObject<Item> item(String name, Supplier<Item> properties) {
        return ITEMS.register(name, properties);
    }

    protected static RegistryObject<Item> item(String name) {
        return item(name, () -> new Item(new Item.Properties()));
    }

    protected static RegistryObject<Item> item(RegistryObject<Block> block) {
        return item(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static final RegistryObject<Item> SOURCE_LIST = item("source_list", ItemSourceList::new);
}
