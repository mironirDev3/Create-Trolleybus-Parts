package com.mironir2.createtrolleybusparts.blockentity;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.patryk3211.powergrid.electricity.base.IElectricEntity;
import org.patryk3211.powergrid.kinetics.base.ElectricKineticBlockEntity;


public class MotorGeneratorBlockEntity extends ElectricKineticBlockEntity {

    /** Stress Units this block draws per RPM. Registered as its impact in ExampleMod. */
    public static final float STRESS_IMPACT = 8.0f;

    public MotorGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void buildCircuit(IElectricEntity.CircuitBuilder builder) {
        builder.setTerminalCount(2);
        var positive = builder.terminalNode(0);
        var negative = builder.terminalNode(1);

    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);

        int draw = (int) (Math.abs(getSpeed()) * STRESS_IMPACT);
        tooltip.add(Component.literal("RPM: "+getSpeed()).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Volts: 0").withStyle(ChatFormatting.AQUA));
        return true;
    }
}
