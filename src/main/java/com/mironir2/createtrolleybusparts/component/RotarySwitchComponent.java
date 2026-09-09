package com.mironir2.createtrolleybusparts.component;

import com.google.common.collect.ImmutableCollection;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.patryk3211.powergrid.circuits.circuitboard.CircuitBoardBlockEntity;
import org.patryk3211.powergrid.circuits.circuitboard.ComponentCircuitBuilder;
import org.patryk3211.powergrid.circuits.components.IInteractableComponent;
import org.patryk3211.powergrid.circuits.components.OrientableComponent;
import org.patryk3211.powergrid.circuits.components.properties.IntProperty;
import org.patryk3211.powergrid.circuits.components.properties.ComponentProperty;
import org.patryk3211.powergrid.circuits.schematic.ComponentFootprint;
import org.patryk3211.powergrid.circuits.schematic.PlacedComponent;
import org.patryk3211.powergrid.circuits.thermal.ThermalBuilder;
import org.patryk3211.powergrid.collections.ModdedPackets;
import org.patryk3211.powergrid.electricity.sim.SwitchedWire;
import org.patryk3211.powergrid.network.packets.UpdateComponentBiPacket;
import org.patryk3211.powergrid.utility.CustomValueSettingsScreen;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.List;

public class RotarySwitchComponent extends OrientableComponent implements IInteractableComponent {

    public static final IntProperty STATE = new IntProperty("createtrolleybusparts", "rotaryswitch_state", 1, 0, 3);
    public static final IntProperty POSITIONS = new IntProperty("createtrolleybusparts", "rotaryswitch_positions", 2, 1, 3);

    @OnlyIn(Dist.CLIENT)
    private static ValueSettingsBoard BOARD;

    public RotarySwitchComponent(ComponentFootprint footprint) {
        super(footprint);
    }


    protected void addProperties(ImmutableCollection.Builder<ComponentProperty<?>> properties) {
        super.addProperties(properties);
        properties.add(STATE);
        properties.add(POSITIONS);
        properties.add(current(6.0F));
    }

    public void bake(@NotNull PlacedComponent placed, @NotNull ComponentCircuitBuilder builder, ThermalBuilder.IEmitter thermals) {
        var wire1 = builder.connectSwitch(0.5F, builder.terminalNode(0), builder.terminalNode(1), placed.get(STATE) == 0);
        var wire2 = builder.connectSwitch(0.5F, builder.terminalNode(0), builder.terminalNode(2), placed.get(STATE) == 1);
        var wire3 = builder.connectSwitch(0.5F, builder.terminalNode(0), builder.terminalNode(3), placed.get(STATE) == 2);
        var wire4 = builder.connectSwitch(0.5F, builder.terminalNode(0), builder.terminalNode(4), placed.get(STATE) == 3);

        placed.add(wire1);
        placed.add(wire2);
        placed.add(wire3);
        placed.add(wire4);

        thermals.builder()
                .setMaxPower(250, 175)
                .setThermalMass(0.005f)
                .addHeatSource(wire1)
                .addHeatSource(wire2)
                .addHeatSource(wire3)
                .addHeatSource(wire4);
    }

    public VoxelShape getShape(@NotNull PlacedComponent placed) {
        return IInteractableComponent.extrudedFootprint(placed, 0.025F);
    }

    @OnlyIn(Dist.CLIENT)
    public InteractionResult use(CircuitBoardBlockEntity be, PlacedComponent placed, Player player) {
        assert be.getLevel() != null;
        if (be.getLevel().isClientSide) {
            placed.onClientWorld(() -> world -> {
                var value = placed.get(STATE);
                if (BOARD == null) {
                    BOARD = CustomValueSettingsScreen.makeBoard(
                            Component.literal("Position"),
                            placed.get(POSITIONS), 10,
                            List.of(Component.literal("value")));
                } else {
                    if (BOARD.maxValue() != placed.get(POSITIONS)) {
                        BOARD = CustomValueSettingsScreen.makeBoard(
                                Component.literal("Position"),
                                placed.get(POSITIONS), 10,
                                List.of(Component.literal("value")));
                    }
                }
                CustomValueSettingsScreen.beginInteraction(() -> new CustomValueSettingsScreen(be.getBlockPos(),
                        BOARD, new ValueSettingsBehaviour.ValueSettings(0, value), setting -> {
                    placed.set(STATE, setting.value());
                    ModdedPackets.sendToServer(new UpdateComponentBiPacket(be, placed, STATE));
                }) {
                });
            });
        }
        be.setChanged();
        return InteractionResult.SUCCESS;
    }

    @Override
    public void stateUpdated(@NotNull PlacedComponent placed) {
        super.stateUpdated(placed);
        if(placed.wires.isEmpty()) {
            return;
        }
        if (placed.get(STATE) == 0) {
            ((SwitchedWire)placed.wires.get(0)).setState(true);
            ((SwitchedWire)placed.wires.get(1)).setState(false);
            ((SwitchedWire)placed.wires.get(2)).setState(false);
            ((SwitchedWire) placed.wires.get(3)).setState(false);
        } else if (placed.get(STATE) == 1) {
            ((SwitchedWire) placed.wires.get(0)).setState(false);
            ((SwitchedWire) placed.wires.get(1)).setState(true);
            ((SwitchedWire) placed.wires.get(2)).setState(false);
            ((SwitchedWire) placed.wires.get(3)).setState(false);
        } else if (placed.get(STATE) == 2) {
            ((SwitchedWire) placed.wires.get(0)).setState(false);
            ((SwitchedWire) placed.wires.get(1)).setState(false);
            ((SwitchedWire) placed.wires.get(2)).setState(true);
            ((SwitchedWire) placed.wires.get(3)).setState(false);
        } else if (placed.get(STATE) == 3) {
            ((SwitchedWire) placed.wires.get(0)).setState(false);
            ((SwitchedWire) placed.wires.get(1)).setState(false);
            ((SwitchedWire) placed.wires.get(2)).setState(false);
            ((SwitchedWire) placed.wires.get(3)).setState(true);
        }
        placed.onClientWorld(() -> (world) -> modelChanged(placed.getPos()));
    }

    @NotNull
    public ResourceLocation getModelId(@NotNull PlacedComponent component) {
        if (component.get(STATE) == 0) {
            return ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "rotaryswitch_0");
        } else if (component.get(STATE) == 1) {
            return ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "rotaryswitch_1");
        } else if (component.get(STATE) == 2) {
            return ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "rotaryswitch_2");
        } else if (component.get(STATE) == 3) {
            return ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "rotaryswitch_3");
        }
        return ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "rotaryswitch");
    }

    @NotNull
    public Collection<ResourceLocation> requestedModels() {
        return List.of(
                ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "rotaryswitch_0"),
                ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "rotaryswitch_1"),
                ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "rotaryswitch_2"),
                ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "rotaryswitch_3"));
    }
}
