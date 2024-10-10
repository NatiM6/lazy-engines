package com.natim6.lazyengines.mixin;

import com.natim6.lazyengines.Config;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PoweredShaftBlockEntity.class, remap = false)
public class PoweredShaftBlockEntityMixin extends GeneratingKineticBlockEntity {
    public PoweredShaftBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "getGeneratedSpeed", at = @At("RETURN"), cancellable = true)
    private void lazy_engines$getGeneratedSpeed(CallbackInfoReturnable<Float> cir) {
        //We want to avoid rewriting the original code, and so we will instead modify the return value based on the original return value.
        //See that we also changed @At("HEAD") to @At("RETURN") so theres no need to cancel the event
        //cir.setReturnValue(getCombinedCapacity() > 0 ? movementDirection * Config.ENGINE_SPEED.get() / 4.f * getSpeedModifier() : 0.f);
        cir.setReturnValue(cir.getReturnValueF() * Config.ENGINE_SPEED.get() / 64);
        //You might be wondering why Config.ENGINE_SPEED.get() / 64
        // if original code had * 16, so we first want to remove that by dividing by 16.
        // Then your code adds a division by 4, so that is where the 64 comes from
    }

    @ModifyVariable(method = "calculateAddedStressCapacity", at = @At("STORE"), ordinal = 0)
    private float lazy_engines$calculateAddedStressCapacity(float original) {
        return original * 64 / Config.ENGINE_SPEED.get() * Config.ENGINE_POWER.get().floatValue();
    }
}