package com.mironir2.createtrolleybusparts.registry;

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

}

