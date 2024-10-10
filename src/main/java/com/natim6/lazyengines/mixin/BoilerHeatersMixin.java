package com.natim6.lazyengines.mixin;

import com.natim6.lazyengines.Config;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.fluids.tank.BoilerHeaters;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BoilerHeaters.class, remap = false)
public class BoilerHeatersMixin {
    @Inject(method = "registerDefaults", at = @At("HEAD"), cancellable = true)
    private static void lazy_engines$registerDefaultsMixin(CallbackInfo ci) {
        registerHeater(com.simibubi.create.AllBlocks.BLAZE_BURNER.get(), (level, pos, state) -> {
            HeatLevel value = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
            if (value == HeatLevel.SEETHING) return Config.SEETHING_BURNER.get();
            if (value.isAtLeast(HeatLevel.FADING)) return Config.KINDLED_BURNER.get();
            return Config.PASSIVE_BURNER.get().value();
        });

        registerHeaterProvider((level, pos, state) -> {
            if (AllTags.AllBlockTags.PASSIVE_BOILER_HEATERS.matches(state) && BlockHelper.isNotUnheated(state)) {
                return (level1, pos1, state1) -> Config.NON_BURNER.get().value();
            }
            return null;
        });

        ci.cancel();
    }

    @Shadow public static void registerHeater(Block block, BoilerHeaters.Heater heater) {}
    @Shadow public static void registerHeaterProvider(BoilerHeaters.HeaterProvider provider) {}
}