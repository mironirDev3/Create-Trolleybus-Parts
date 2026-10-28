package com.mironir2.createtrolleybusparts.registry;

import com.mironir2.createtrolleybusparts.blockentity.MotorGeneratorBlockEntity;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class ModBlockEntityTypes {
    public static final BlockEntityEntry<MotorGeneratorBlockEntity> GeneratorMotor = ModBlocks.BlockCreateRegistry
            .blockEntity("generatormotor_kinetic", MotorGeneratorBlockEntity::new)
            // visual for flywheel renderer
            .visual(() -> ShaftVisual::new)
            .validBlock(ModBlocks.GeneratorMotor)
            // fallback renderer if flywheel is not available
            .renderer(() -> ShaftRenderer::new)
            .register();

    public static void register() {
        // Force class loading to trigger Registrate calls
    }
}
