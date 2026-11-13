package com.mironir2.createtrolleybusparts.blockentity;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.patryk3211.powergrid.PowerGrid;
import org.patryk3211.powergrid.electricity.base.IElectricEntity;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;
import org.patryk3211.powergrid.electricity.sim.node.ProvidedVoltageSourceCoupling;
import org.patryk3211.powergrid.electricity.wire.BlockWireRenderer;
import org.patryk3211.powergrid.electricity.wire.HangingWireRenderer;
import org.patryk3211.powergrid.electricity.wire.WireConnection;
import org.patryk3211.powergrid.electricity.wire.registry.WireRegistry;
import org.patryk3211.powergrid.kinetics.base.ElectricKineticBlockEntity;


public class MotorGeneratorBlockEntity extends ElectricKineticBlockEntity {

    public static final float STRESS_IMPACT = 4.0f;
    private ProvidedVoltageSourceCoupling outputSource;
    public float RPM = 0;

    public MotorGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void buildCircuit(IElectricEntity.CircuitBuilder builder) {
        builder.setTerminalCount(2);
        var positive = builder.terminalNode(0);
        var negative = builder.terminalNode(1);
        builder.add(positive);
        builder.add(negative);
        outputSource = new ProvidedVoltageSourceCoupling(builder.terminalNode(0), builder.terminalNode(1), 1);
        outputSource.setVoltageProvider(this::outputVoltage);
        outputSource.setVoltageProvider(this::outputResistance);
        builder.connect(100000, positive, negative);
        builder.add(outputSource);
        this.outputResistance();
    }

        protected float outputVoltage() {
            if (Math.abs(RPM) > 0.5f) {
                return Math.clamp(RPM, -99999, 99999)/2.5f;
            } else {
                return 0.001;
            }
        }

    protected float outputResistance() {
        float V = outputVoltage();
        float W = 250;
        float R = V * V / (2 * W);
        if(W <= 0 || R <= 0)
            return 0;
        return R;
    }

    @Override
    public void electricalTick() {
        super.electricalTick();
        RPM = getSpeed();
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);

        int draw = (int) (Math.abs(getSpeed()) * STRESS_IMPACT);
        tooltip.add(Component.literal("RPM: "+getSpeed()).withStyle(ChatFormatting.GRAY));
        var voltage = (outputSource.getPositive().getVoltage() - outputSource.getNegative().getVoltage());
        var voltageText = String.format("%.2f", voltage);
        tooltip.add(Component.literal("Volts: "+voltageText).withStyle(ChatFormatting.AQUA));
        return true;
    }
}
