package com.natim6.lazyengines.mixin;

import com.natim6.lazyengines.Config;
import com.simibubi.create.api.boiler.BoilerHeater;
import com.simibubi.create.content.fluids.tank.BoilerHeaters;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BoilerHeaters.class)
public class BoilerHeatersMixin {
    @Inject(method = "blazeBurner", at = @At("HEAD"), cancellable = true, remap = false)
    private static void lazyengines$blazeBurner$head(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Integer> cir) {
        BlazeBurnerBlock.HeatLevel value = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);

        if (value == BlazeBurnerBlock.HeatLevel.NONE) {
            cir.setReturnValue(BoilerHeater.NO_HEAT);
        } else if (value == BlazeBurnerBlock.HeatLevel.SEETHING) {
            cir.setReturnValue(Config.SEETHING_BURNER.get());
        } else if (value.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING)) {
            cir.setReturnValue(Config.KINDLED_BURNER.get());
        } else {
            cir.setReturnValue(Config.PASSIVE_BURNER.get().value());
        }
        cir.cancel();
    }

    @Inject(method = "passive", at = @At("RETURN"), cancellable = true, remap = false)
    private static void lazyengines$passive$return(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Integer> cir) {
        if(cir.getReturnValue() == BoilerHeater.PASSIVE_HEAT)
            cir.setReturnValue(Config.NON_BURNER.get().value());
    }
}
