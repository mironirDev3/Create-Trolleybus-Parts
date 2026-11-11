package com.mironir2.createtrolleybusparts.component;

import com.google.common.collect.ImmutableCollection;
import org.jetbrains.annotations.NotNull;
import org.patryk3211.powergrid.PowerGrid;
import org.patryk3211.powergrid.circuits.circuitboard.ComponentCircuitBuilder;
import org.patryk3211.powergrid.circuits.components.OrientableComponent;
import org.patryk3211.powergrid.circuits.components.properties.ComponentProperty;
import org.patryk3211.powergrid.circuits.components.properties.IntProperty;
import org.patryk3211.powergrid.circuits.schematic.ComponentFootprint;
import org.patryk3211.powergrid.circuits.schematic.PlacedComponent;
import org.patryk3211.powergrid.circuits.thermal.ThermalBuilder;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;

public class RheostatComponent extends OrientableComponent
{
    public static final IntProperty RESISTANCE = new IntProperty("createtrolleybusparts", "rheostat_resistance", 10000, 100, 100000);

    protected void addProperties(ImmutableCollection.Builder<ComponentProperty<?>> properties) {
        super.addProperties(properties);
        properties.add(RESISTANCE);
        properties.add(power(600.0F));
    }

    public RheostatComponent(ComponentFootprint footprint) {
        super(footprint);
    }

    @Override
    public void bake(@NotNull PlacedComponent placed, @NotNull ComponentCircuitBuilder builder, ThermalBuilder.@NotNull IEmitter thermals) {
        var wire1 = builder.connect(placed.get(RESISTANCE), builder.terminalNode(0), builder.terminalNode(1));
        var wire2 = builder.connect(5F, builder.terminalNode(2), builder.terminalNode(3));

        placed.add(wire1);
        placed.add(wire2);

        thermals.builder()
                .setMaxPower(600, 175)
                .setThermalMass(0.05f)
                .addHeatSource(wire1)
                .addHeatSource(wire2);
    }

    @Override
    public boolean tick(@NotNull PlacedComponent placed) {
        var wire1 = (ElectricWire) placed.wires.get(0);
        var wire2 = (ElectricWire) placed.wires.get(1);
        double R = (1.01-Math.clamp(Math.abs(wire2.current()), 0, 1))*placed.get(RESISTANCE);
        wire1.setResistance(R);
        return true;
    }
}
