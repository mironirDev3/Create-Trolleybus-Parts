package com.mironir2.createtrolleybusparts.registry;

import com.mironir2.createtrolleybusparts.component.LVToggleComponent;
import com.mironir2.createtrolleybusparts.component.LinearButtonComponent;
import com.mironir2.createtrolleybusparts.component.RotarySwitchComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.patryk3211.powergrid.circuits.components.ComponentRegistry;
import org.patryk3211.powergrid.circuits.schematic.ComponentFootprint;

@EventBusSubscriber(modid = "createtrolleybusparts")
public class ModComponents {
    private static RotarySwitchComponent buildRotarySwitch() {
        ComponentFootprint footprint = (new ComponentFootprint.Builder(5, 5, "component.createtrolleybusparts.rotaryswitch", null))
                .addPad(0, 4, 0, "Common", "COM")
                .addPad(0, 2, 1, "Position Off", "POff")
                .addPad(2, 0, 2, "Position 1", "P1")
                .addPad(4, 2, 3, "Position 2", "P2")
                .addPad(4, 4, 4, "Position 3", "P3")
                .withItem().withOutline().build();
        return new RotarySwitchComponent(footprint);
    }
    private static LinearButtonComponent buildLinearButton() {
        ComponentFootprint footprint = (new ComponentFootprint.Builder(5, 4, "component.createtrolleybusparts.linearbutton", null))
                .addPad(2, 3, 0, "Common 1", "COM1")
                .addPad(0, 3, 1, "Normally Open P", "NOP")
                .addPad(4, 3, 2, "Normally Open L", "NOL")
                .addPad(2, 0, 3, "Common 2", "COM2")
                .addPad(0, 0, 4, "Normally Open P", "NOP")
                .addPad(4, 0, 5, "Normally Open L", "NOL")
                .withItem().withOutline().build();
        return new LinearButtonComponent(footprint);
    }

    private static LVToggleComponent buildLVToggle() {
        ComponentFootprint footprint = (new ComponentFootprint.Builder(3, 2, "component.createtrolleybusparts.lvtoggle", null))
                .addPad(1, 0, 0, "Common 1", "COM")
                .addPad(0, 1, 1, "Normally Open", "NO")
                .addPad(2, 1, 2, "Normally Open", "NO")
                .withItem().withOutline().build();
        return  new LVToggleComponent(footprint);
    }

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        if (!event.getRegistryKey().equals(ComponentRegistry.REGISTRY_KEY)) {
            return;
        }
        register(event, "rotaryswitch", buildRotarySwitch());
        register(event, "linearbutton", buildLinearButton());
        register(event, "lvtoggle", buildLVToggle());
    }

    private static void register(RegisterEvent event, String id, org.patryk3211.powergrid.circuits.components.Component component) {
        event.register(
                ComponentRegistry.REGISTRY_KEY,
                ResourceLocation.fromNamespaceAndPath("createtrolleybusparts", id),
                () -> component
        );
    }
}
