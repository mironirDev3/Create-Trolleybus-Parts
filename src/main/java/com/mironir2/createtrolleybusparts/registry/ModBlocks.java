package com.mironir2.createtrolleybusparts.registry;

import com.jcraft.jorbis.Block;
import com.mironir2.createtrolleybusparts.blockentity.MotorGeneratorBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.mironir2.createtrolleybusparts.registry.ModItems.ITEMS;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("createtrolleybusparts");

    /* Coding for first time here, no hate, ok? :) */

    public static final DeferredBlock<MotorGeneratorBlockEntity> GeneratorMotor = registerBlock("generatormotor", () -> new MotorGeneratorBlockEntity(BlockBehaviour.Properties.of().noOcclusion()));

    private static <T extends net.minecraft.world.level.block.Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        RegisterBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends net.minecraft.world.level.block.Block> void RegisterBlockItem(String name, DeferredBlock<T> block) {
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
