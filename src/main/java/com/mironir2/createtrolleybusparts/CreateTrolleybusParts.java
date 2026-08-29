package com.mironir2.createtrolleybusparts;

import com.mironir2.createtrolleybusparts.registry.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(CreateTrolleybusParts.MODID)
public class CreateTrolleybusParts {
    public static final String MODID = "createtrolleybusparts";

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_MODE_TAB = CREATIVE_MODE_TABS.register("createtrolleybusparts", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.createtrolleybusparts"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.ROTARY_SWITCH.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.ROTARY_SWITCH.get());
                output.accept(ModItems.LINEAR_BUTTON.get());
                output.accept(ModItems.LV_TOGGLE.get());
            }).build());

    public CreateTrolleybusParts(IEventBus modBus) {
        ModItems.ITEMS.register(modBus);
        CREATIVE_MODE_TABS.register(modBus);
    }

}
