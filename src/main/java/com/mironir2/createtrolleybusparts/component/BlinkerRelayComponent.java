package com.mironir2.createtrolleybusparts.component;

import com.google.common.collect.ImmutableCollection;
import org.jetbrains.annotations.NotNull;
import org.patryk3211.powergrid.circuits.circuitboard.ComponentCircuitBuilder;
import org.patryk3211.powergrid.circuits.components.OrientableComponent;
import org.patryk3211.powergrid.circuits.components.properties.BooleanProperty;
import org.patryk3211.powergrid.circuits.components.properties.ComponentProperty;
import org.patryk3211.powergrid.circuits.components.properties.FloatProperty;
import org.patryk3211.powergrid.circuits.components.properties.IntProperty;
import org.patryk3211.powergrid.circuits.schematic.ComponentFootprint;
import org.patryk3211.powergrid.circuits.schematic.PlacedComponent;
import org.patryk3211.powergrid.circuits.thermal.ThermalBuilder;
import org.patryk3211.powergrid.collections.ModdedSoundEvents;
import org.patryk3211.powergrid.electricity.sim.ElectricalNetwork;
import org.patryk3211.powergrid.electricity.sim.SwitchedWire;

public class BlinkerRelayComponent extends OrientableComponent {

    public static final IntProperty BlinkTime = (IntProperty) new IntProperty("createtrolleybusparts", "blinkerrelay_blinktime", 5, 0, 15);

    public static final IntProperty OnRemaining = (IntProperty) new IntProperty("createtrolleybusparts", "blinkerrelay_timeremaining", 0, -15, 15).hidden();

    public BlinkerRelayComponent(ComponentFootprint footprint) {
        super(footprint);
    }

    protected void addProperties(ImmutableCollection.Builder<ComponentProperty<?>> properties) {
        super.addProperties(properties);
        properties.add(OnRemaining);
        properties.add(BlinkTime);
        properties.add(voltage(12));
        properties.add(current(0.5F));
    }

    @Override
    public void bake(@NotNull PlacedComponent placed, @NotNull ComponentCircuitBuilder builder, ThermalBuilder.@NotNull IEmitter thermals) {
        var Switch = builder.connectSwitch(0.05F, builder.terminalNode(0), builder.terminalNode(2), placed.get(OnRemaining) > 0);

        var Coil = builder.connect(35f, builder.terminalNode(1), builder.terminalNode(0));

        placed.add(Switch);
        placed.add(Coil);

        thermals.builder()
                .setMaxPower(100, 125)
                .setThermalMass(0.005f)
                .addHeatSource(Switch)
                .addHeatSource(Coil);
    }

    @Override
    public boolean tick(@NotNull PlacedComponent placed) {
        if (placed.get(OnRemaining) > 0) {
            if (!((SwitchedWire) placed.wires.get(0)).getState()) {
                placed.onServerWorld(() -> world -> ModdedSoundEvents.RELAY_CLICK.playOnServer(world, placed.getPos(), 0.25f, 2.0f));
                placed.onClientWorld(() -> world -> modelChanged(placed.getPos()));
            }
            ((SwitchedWire) placed.wires.get(0)).setState(true);
        } else {
            if (((SwitchedWire) placed.wires.get(0)).getState()) {
                placed.onServerWorld(() -> world -> ModdedSoundEvents.RELAY_CLICK.playOnServer(world, placed.getPos(), 0.25f, 1.8f));
                placed.onClientWorld(() -> world -> modelChanged(placed.getPos()));
            }
            ((SwitchedWire) placed.wires.get(0)).setState(false);
        }
        if (placed.wires.get(1).current() > 0.2f && placed.get(OnRemaining) < -placed.get(BlinkTime)+1) {
            placed.set(OnRemaining, placed.get(BlinkTime));
        } else if (placed.wires.get(1).current() < 0.15f) {
            placed.set(OnRemaining, -placed.get(BlinkTime));
        }
        placed.set(OnRemaining, placed.get(OnRemaining) - 1);
        placed.set(OnRemaining, Math.clamp(placed.get(OnRemaining), -15, 15));
        return true;
    }
}
