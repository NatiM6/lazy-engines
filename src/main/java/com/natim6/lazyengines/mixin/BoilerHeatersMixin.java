package com.natim6.lazyengines.mixin;

import com.natim6.lazyengines.Config;
import com.simibubi.create.content.fluids.tank.BoilerHeaters;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BoilerHeaters.class, remap = false)
public class BoilerHeatersMixin {

    @Inject(method = "lambda$registerDefaults$0", at = @At("HEAD"), cancellable = true)
    private static void lazy_engines$registerHeater(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Float> cir) {
        HeatLevel value = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
        if (value == HeatLevel.SEETHING) cir.setReturnValue((float)Config.SEETHING_BURNER.get());
        else if (value.isAtLeast(HeatLevel.FADING)) cir.setReturnValue((float)Config.KINDLED_BURNER.get());
        else cir.setReturnValue((float)Config.PASSIVE_BURNER.get().value());
        cir.cancel();
    }

    @Inject(method = "lambda$registerDefaults$1", at = @At("RETURN"), cancellable = true)
    private static void lazy_engines$registerHeaterProvider$return(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<BoilerHeaters.Heater> cir) {
        if(cir.getReturnValue() != null)
            cir.setReturnValue((a,b,c) -> Config.NON_BURNER.get().value());
    }
}