package com.mironir2.createtrolleybusparts.component;

import com.google.common.collect.ImmutableCollection;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import net.minecraft.network.chat.Component;
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
import org.patryk3211.powergrid.circuits.components.properties.ComponentProperty;
import org.patryk3211.powergrid.circuits.components.properties.IntProperty;
import org.patryk3211.powergrid.circuits.schematic.ComponentFootprint;
import org.patryk3211.powergrid.circuits.schematic.PlacedComponent;
import org.patryk3211.powergrid.circuits.thermal.ThermalBuilder;
import org.patryk3211.powergrid.collections.ModdedPackets;
import org.patryk3211.powergrid.electricity.sim.SwitchedWire;
import org.patryk3211.powergrid.network.packets.UpdateComponentBiPacket;
import org.patryk3211.powergrid.utility.CustomValueSettingsScreen;

import java.util.Collection;
import java.util.List;

public class LVToggleComponent extends OrientableComponent implements IInteractableComponent {

    public static final IntProperty STATE = new IntProperty("createtrolleybusparts", "lvtoggle_state", 0, -1, 1);

    public LVToggleComponent(ComponentFootprint footprint) {
        super(footprint);
    }

    @OnlyIn(Dist.CLIENT)
    private static ValueSettingsBoard BOARD;

    protected void addProperties(ImmutableCollection.Builder<ComponentProperty<?>> properties) {
        super.addProperties(properties);
        properties.add(STATE);
        properties.add(current(6.0F));
    }

    public void bake(@NotNull PlacedComponent placed, @NotNull ComponentCircuitBuilder builder, ThermalBuilder.IEmitter thermals) {
        var wire1 = builder.connectSwitch(0.5F, builder.terminalNode(0), builder.terminalNode(1), placed.get(STATE) == 1);
        var wire2 = builder.connectSwitch(0.5F, builder.terminalNode(0), builder.terminalNode(2), placed.get(STATE) == -1);

        placed.add(wire1);
        placed.add(wire2);

        thermals.builder()
                .setMaxPower(250, 175)
                .setThermalMass(0.005f)
                .addHeatSource(wire1)
                .addHeatSource(wire2);
    }

    public VoxelShape getShape(@NotNull PlacedComponent placed) {
        return IInteractableComponent.extrudedFootprint(placed, 0.025F);
    }

    @OnlyIn(Dist.CLIENT)
    public InteractionResult use(CircuitBoardBlockEntity be, PlacedComponent placed, Player player) {
        placed.onClientWorld(() -> world -> {
            var value = placed.get(STATE);
            if (BOARD == null) {
                BOARD = CustomValueSettingsScreen.makeBoard(
                        Component.literal("Position"),
                        2, 10,
                        List.of(Component.literal("value")));
            }
            CustomValueSettingsScreen.beginInteraction(() -> new CustomValueSettingsScreen(be.getBlockPos(),
                    BOARD, new ValueSettingsBehaviour.ValueSettings(0, value+1), setting -> {
                    placed.set(STATE, setting.value()-1);
                    ModdedPackets.sendToServer(new UpdateComponentBiPacket(be, placed, STATE));
                }) {
            });
        });
        be.setChanged();
        return InteractionResult.SUCCESS;
    }

    @Override
    public void stateUpdated(@NotNull PlacedComponent placed) {
        super.stateUpdated(placed);
        if(placed.wires.isEmpty()) {
            return;
        }
        if (placed.get(STATE) == -1) {
            ((SwitchedWire)placed.wires.get(0)).setState(false);
            ((SwitchedWire)placed.wires.get(1)).setState(true);
        } else if (placed.get(STATE) == 1) {
            ((SwitchedWire) placed.wires.get(0)).setState(true);
            ((SwitchedWire) placed.wires.get(1)).setState(false);
        } else if (placed.get(STATE) == 0) {
            ((SwitchedWire) placed.wires.get(0)).setState(false);
            ((SwitchedWire) placed.wires.get(1)).setState(false);
        }
        placed.onClientWorld(() -> (world) -> modelChanged(placed.getPos()));
    }

    @NotNull
    public ResourceLocation getModelId(@NotNull PlacedComponent component) {
        if (component.get(STATE) == 1) {
            return ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "lvtoggle_l");
        } else if (component.get(STATE) == 0) {
            return ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "lvtoggle_n");
        } else if (component.get(STATE) == -1) {
            return ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "lvtoggle_r");
        }
        return ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "lvtoggle");
    }

    @NotNull
    public Collection<ResourceLocation> requestedModels() {
        return List.of(
                ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "lvtoggle_l"),
                ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "lvtoggle_n"),
                ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "lvtoggle_r"));
    }
}
