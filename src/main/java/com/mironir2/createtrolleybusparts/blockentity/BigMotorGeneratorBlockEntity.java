package com.mironir2.createtrolleybusparts.blockentity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.patryk3211.powergrid.electricity.base.IElectric;
import org.patryk3211.powergrid.electricity.base.IElectricEntity;
import org.patryk3211.powergrid.electricity.base.ThermalBehaviour;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;
import org.patryk3211.powergrid.electricity.sim.node.ProvidedVoltageSourceCoupling;
import org.patryk3211.powergrid.kinetics.base.ElectricKineticBlockEntity;

import javax.annotation.Nullable;
import java.util.List;


public class BigMotorGeneratorBlockEntity extends ElectricKineticBlockEntity {

    public static final float STRESS_IMPACT = 5.0f;
    public ElectricWire shuntwire;
    private ProvidedVoltageSourceCoupling outputSource;
    public float RPM = 0;
    @Nullable
    protected ThermalBehaviour thermalBehaviour;

    public BigMotorGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void buildCircuit(CircuitBuilder builder) {
        builder.setTerminalCount(3);
        var positive = builder.terminalNode(0);
        var negative = builder.terminalNode(1);
        var excitation = builder.terminalNode(2);
        builder.add(positive);
        builder.add(negative);
        builder.add(excitation);
        shuntwire = builder.connect(50, excitation, negative);
        builder.add(shuntwire);
        outputSource = new ProvidedVoltageSourceCoupling(builder.terminalNode(0), builder.terminalNode(1), 1);
        outputSource.setVoltageProvider(this::outputVoltage);
        outputSource.setVoltageProvider(this::outputResistance);
        builder.connect(100000, positive, negative);
        builder.add(outputSource);
        this.outputVoltage();
    }

    public void addBehaviours(List<com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        var maxPower = 195;
        var baseFactor = ThermalBehaviour.dissipationFactor(maxPower, 150);
        thermalBehaviour = ThermalBehaviour.simple(this, 3.5f, baseFactor);
        if(thermalBehaviour != null) {
            behaviours.add(thermalBehaviour);
        }
    }

    protected float outputVoltage() {
        if (Math.abs(RPM) > 0.5f) {
            return (float) Math.clamp((Math.clamp(RPM, -99999, 99999) *(Math.clamp(Math.abs(shuntwire.current()), 0.001, 100)*1.5)), -950, 950);
        } else {
            return 0.001;
        }
    }

    protected float outputResistance() {
        float V = outputVoltage();
        float W = 750;
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
