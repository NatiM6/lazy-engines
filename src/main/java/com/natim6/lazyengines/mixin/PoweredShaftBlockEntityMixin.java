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
        cir.setReturnValue(cir.getReturnValueF() * Config.ENGINE_SPEED.get() / 64);
    }

    @ModifyVariable(method = "calculateAddedStressCapacity", at = @At("STORE"), ordinal = 0)
    private float lazy_engines$calculateAddedStressCapacity(float original) {
        return original * 64 / Config.ENGINE_SPEED.get() * Config.ENGINE_POWER.get().floatValue();
    }
}