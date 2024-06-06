package com.natim6.lazyengines.mixin;

import com.simibubi.create.content.fluids.tank.BoilerHeaters;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BoilerHeaters.class, remap = false)
public class BoilerHeatersMixin {
    @Inject(method = "registerDefaults", at = @At("HEAD"), cancellable = true)
    private static void registerDefaultsMixin(CallbackInfo ci) {
        registerHeater(com.simibubi.create.AllBlocks.BLAZE_BURNER.get(), (level, pos, state) -> {
            HeatLevel value = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
            if (value == HeatLevel.SEETHING) return 4;
            if (value == HeatLevel.KINDLED) return 1;
            if (value == HeatLevel.FADING) return 0;
            return -1;
        });
        ci.cancel();
    }

    @Shadow public static void registerHeater(Block block, BoilerHeaters.Heater heater) {}
}