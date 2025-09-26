package com.chiyukiruon.maid_mana_source.registry;

import com.chiyukiruon.maid_mana_source.MaidManaSource;
import com.chiyukiruon.maid_mana_source.item.ItemSourceList;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS
            = DeferredRegister.create(Registries.ITEM, MaidManaSource.MODID);

    protected static <T extends Item> DeferredHolder<Item, T> item(String name, Supplier<T> properties) {
        return ITEMS.register(name, properties);
    }

    protected static DeferredHolder<Item, Item> item(String name) {
        return item(name, () -> new Item(new Item.Properties()));
    }

    protected static DeferredHolder<Item, Item> item(DeferredHolder<Block, Block> block) {
        return item(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static final DeferredHolder<Item, ItemSourceList> SOURCE_LIST = item("source_list", ItemSourceList::new);
}
