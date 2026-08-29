package com.mironir2.createtrolleybusparts.component;

import com.google.common.collect.ImmutableCollection;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.patryk3211.powergrid.circuits.circuitboard.CircuitBoardBlockEntity;
import org.patryk3211.powergrid.circuits.circuitboard.ComponentCircuitBuilder;
import org.patryk3211.powergrid.circuits.components.IInteractableComponent;
import org.patryk3211.powergrid.circuits.components.OrientableComponent;
import org.patryk3211.powergrid.circuits.components.properties.BooleanProperty;
import org.patryk3211.powergrid.circuits.components.properties.ComponentProperty;
import org.patryk3211.powergrid.circuits.components.properties.IntProperty;
import org.patryk3211.powergrid.circuits.schematic.ComponentFootprint;
import org.patryk3211.powergrid.circuits.schematic.PlacedComponent;
import org.patryk3211.powergrid.circuits.thermal.ThermalBuilder;
import org.patryk3211.powergrid.collections.ModdedSoundEvents;
import org.patryk3211.powergrid.electricity.sim.SwitchedWire;

import java.util.Collection;
import java.util.List;

public class LinearButtonComponent extends OrientableComponent implements IInteractableComponent {

    public static final IntProperty STATE = (IntProperty) new IntProperty("createtrolleybusparts", "linearbutton_state", 0, -15, 15).hidden();
    public static final BooleanProperty CANPULL = new BooleanProperty("createtrolleybusparts", "linearbutton_canpull");
    public static final BooleanProperty CANPUSH = new BooleanProperty("createtrolleybusparts", "linearbutton_canpush");
    public static final BooleanProperty FIXED = new BooleanProperty("createtrolleybusparts", "linearbutton_fixed");

    public LinearButtonComponent(ComponentFootprint footprint) {
        super(footprint);
    }

    protected void addProperties(ImmutableCollection.Builder<ComponentProperty<?>> properties) {
        super.addProperties(properties);
        properties.add(STATE);
        properties.add(CANPULL);
        properties.add(CANPUSH);
        properties.add(FIXED);
        properties.add(current(6.0F));
    }

    @Override
    public void bake(@NotNull PlacedComponent placed, @NotNull ComponentCircuitBuilder builder, ThermalBuilder.IEmitter thermals) {
        var NOP1 = builder.connectSwitch(0.05F, builder.terminalNode(0), builder.terminalNode(1), placed.get(STATE) > 0);
        var NOL1 = builder.connectSwitch(0.05F, builder.terminalNode(0), builder.terminalNode(2), placed.get(STATE) < 0);
        var NOP2 = builder.connectSwitch(0.05F, builder.terminalNode(3), builder.terminalNode(4), placed.get(STATE) > 0);
        var NOL2 = builder.connectSwitch(0.05F, builder.terminalNode(3), builder.terminalNode(5), placed.get(STATE) < 0);

        placed.add(NOP1);
        placed.add(NOL1);
        placed.add(NOP2);
        placed.add(NOL2);

        thermals.builder()
                .setMaxPower(250, 125)
                .setThermalMass(0.005f)
                .addHeatSource(NOP1)
                .addHeatSource(NOL1)
                .addHeatSource(NOP2)
                .addHeatSource(NOL2);
    }

    @Override
    public VoxelShape getShape(@NotNull PlacedComponent placed) {
        return IInteractableComponent.extrudedFootprint(placed, 2 / 16f);
    }

    @Override
    public InteractionResult use(CircuitBoardBlockEntity circuitBoardBlockEntity, PlacedComponent placed, Player player) {
        if (placed.get(FIXED) == false) {
            if((placed.get(STATE) == 0 || placed.get(STATE) < 5) && !player.isCrouching() && placed.get(CANPUSH) == true) {
                if (placed.get(STATE) == 0) {
                    placed.onServerWorld(() -> world -> ModdedSoundEvents.MICROBUTTON_ON.playOnServer(world, placed.getPos()));
                }
                placed.onClientWorld(() -> world -> modelChanged(placed.getPos()));
                placed.set(STATE, 10);
            } else if((placed.get(STATE) == 0 || placed.get(STATE) > -5) && player.isCrouching() && placed.get(CANPULL) == true) {
                if (placed.get(STATE) == 0) {
                    placed.onServerWorld(() -> world -> ModdedSoundEvents.MICROBUTTON_ON.playOnServer(world, placed.getPos()));
                }
                placed.onClientWorld(() -> world -> modelChanged(placed.getPos()));
                placed.set(STATE, -10);
            }
        }
        if(placed.get(FIXED) == true) {
            var state = placed.get(STATE);
            if (!player.isCrouching() && state < 1) {
                if (placed.get(CANPUSH) || state < 0) {
                    placed.onServerWorld(() -> world -> ModdedSoundEvents.MICROBUTTON_ON.playOnServer(world, placed.getPos()));
                    placed.onClientWorld(() -> world -> modelChanged(placed.getPos()));
                    placed.set(STATE, state + 1);
                }
            } else if (player.isCrouching() && state > -1) {
                if (placed.get(CANPULL) || state > 0) {
                    placed.onServerWorld(() -> world -> ModdedSoundEvents.MICROBUTTON_ON.playOnServer(world, placed.getPos()));
                    placed.onClientWorld(() -> world -> modelChanged(placed.getPos()));
                    placed.set(STATE, state - 1);
                }
            }
        }
        placed.notifyClients(STATE);
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean tick(@NotNull PlacedComponent placed) {
        var state = placed.get(STATE);
        if(state !=0) {
            if (placed.get(FIXED) == false) {
                if (state > 0) {
                    state--;
                } else
                if (state < 0) {
                    state++;
                }
            }
            placed.set(STATE, state);
            if(state == 0) {
                placed.onServerWorld(() -> world -> ModdedSoundEvents.MICROBUTTON_OFF.playOnServer(world, placed.getPos()));
                placed.onClientWorld(() -> world -> modelChanged(placed.getPos()));
            }
        }
        if (!placed.wires.isEmpty()) {
            if (placed.get(STATE).intValue() < 0) {
                ((SwitchedWire)placed.wires.get(0)).setState(false);
                ((SwitchedWire)placed.wires.get(1)).setState(true);
                ((SwitchedWire)placed.wires.get(2)).setState(false);
                ((SwitchedWire)placed.wires.get(3)).setState(true);
            } else if (placed.get(STATE).intValue() == 0) {
                ((SwitchedWire)placed.wires.get(0)).setState(false);
                ((SwitchedWire)placed.wires.get(1)).setState(false);
                ((SwitchedWire)placed.wires.get(2)).setState(false);
                ((SwitchedWire)placed.wires.get(3)).setState(false);
            } else if (placed.get(STATE).intValue() > 0) {
                ((SwitchedWire)placed.wires.get(0)).setState(true);
                ((SwitchedWire)placed.wires.get(1)).setState(false);
                ((SwitchedWire)placed.wires.get(2)).setState(true);
                ((SwitchedWire)placed.wires.get(3)).setState(false);
            }
        }
        return true;
    }

    @Override
    public void stateUpdated(@NotNull PlacedComponent placed) {
        super.stateUpdated(placed);
        placed.onClientWorld(() -> (world) -> modelChanged(placed.getPos()));
    }

    @NotNull
    public ResourceLocation getModelId(@NotNull PlacedComponent component) {
        if (component.get(STATE).intValue() < 0) {
            return ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "linearbutton_l");
        } else if (component.get(STATE).intValue() == 0) {
            return ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "linearbutton_n");
        } else if (component.get(STATE).intValue() > 0) {
            return ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "linearbutton_p");
        };
        return ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "linearbutton");
    }

    @NotNull
    public Collection<ResourceLocation> requestedModels() {
        return List.of(
                ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "linearbutton_n"),
                ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "linearbutton_p"),
                ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", "linearbutton_l"));
    }
}
