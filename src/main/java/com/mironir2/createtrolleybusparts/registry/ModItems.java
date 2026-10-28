package com.mironir2.createtrolleybusparts.registry;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("createtrolleybusparts");

    public static final DeferredHolder<Item, Item> ROTARY_SWITCH = ITEMS
            .registerSimpleItem("rotaryswitch", new Item.Properties());

    public static final DeferredHolder<Item, Item> LINEAR_BUTTON = ITEMS
            .registerSimpleItem("linearbutton", new Item.Properties());

    public static final DeferredHolder<Item, Item> LV_TOGGLE = ITEMS
            .registerSimpleItem("lvtoggle", new Item.Properties());

    public static final DeferredHolder<Item, Item> BLINKER_RELAY = ITEMS
            .registerSimpleItem("blinkerrelay", new Item.Properties());

    public static final ItemEntry<Item> GENERATOR_MOTOR = ModBlocks.BlockCreateRegistry
            .item("generator_motor", Item::new)
            .model((c, p) -> p.generated(c::getEntry, ResourceLocation.withDefaultNamespace("item/amethyst_shard")))
            .register();

}

