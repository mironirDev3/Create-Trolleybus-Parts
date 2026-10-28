package com.mironir2.createtrolleybusparts.registry;

import com.mironir2.createtrolleybusparts.blockentity.MotorGeneratorBlock;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.registries.DeferredRegister;

import static org.patryk3211.powergrid.utility.DataProviderUtility.alternateDirectionalBlock;


public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("createtrolleybusparts");
    public static final CreateRegistrate BlockCreateRegistry = CreateRegistrate.create("createtrolleybusparts");

    /* Coding for first time here, no hate, ok? :) */

    public static final BlockEntry<MotorGeneratorBlock> GeneratorMotor = BlockCreateRegistry
            .block("generatormotor", MotorGeneratorBlock::new)
            .initialProperties(() -> Blocks.ANDESITE)
            .properties(p -> p.noOcclusion())
            .blockstate(alternateDirectionalBlock(state -> switch(state.getValue(MotorGeneratorBlock.FACING).getAxis()) {
                case X, Z -> "block/electric_motor/block";
                case Y -> "block/electric_motor/block_vertical";
            }))
            // here is where you can adjust how much stress your block scales with (for example rpm x 128 for this block is the amount of su)
            .onRegister(b -> BlockStressValues.IMPACTS.register(b, () -> 128))
            .item()
            .build()
            .register();

    private static ModelFile encasedShaftModel(RegistrateBlockstateProvider prov, String name,
                                               ResourceLocation casing, ResourceLocation cap) {
        return prov.models()
                .withExistingParent(name, ResourceLocation.withDefaultNamespace("block/block"))
                .texture("particle", casing)
                .texture("casing", casing)
                .texture("cap", cap)
                .element()
                // Inset 3px on the top and bottom (the Y axis) so the shaft pokes out.
                .from(0, 3, 0).to(16, 13, 16)
                .face(Direction.NORTH).texture("#casing").end()
                .face(Direction.SOUTH).texture("#casing").end()
                .face(Direction.WEST).texture("#casing").end()
                .face(Direction.EAST).texture("#casing").end()
                .face(Direction.UP).texture("#cap").end()
                .face(Direction.DOWN).texture("#cap").end()
                .end();
    }
}
