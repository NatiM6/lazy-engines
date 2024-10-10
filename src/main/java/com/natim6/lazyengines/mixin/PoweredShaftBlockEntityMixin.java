package com.natim6.lazyengines.mixin;

import com.natim6.lazyengines.Config;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PoweredShaftBlockEntity.class, remap = false)
public class PoweredShaftBlockEntityMixin extends GeneratingKineticBlockEntity {
    public PoweredShaftBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "getGeneratedSpeed", at = @At("HEAD"), cancellable = true)
    public void lazy_engines$getGeneratedSpeedMixin(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(getCombinedCapacity() > 0 ? movementDirection * Config.ENGINE_SPEED.get() / 4.f * getSpeedModifier() : 0.f);
        cir.cancel(); // Thanks LiukRast for the correction!
    }

    @Inject(method = "calculateAddedStressCapacity", at = @At("HEAD"), cancellable = true)
    public void lazy_engines$calculateAddedStressCapacityMixin(CallbackInfoReturnable<Float> cir) {
        float capacity = getCombinedCapacity() / getSpeedModifier() * 64 / Config.ENGINE_SPEED.get() * Config.ENGINE_POWER.get().floatValue();
        this.lastCapacityProvided = capacity;
        cir.setReturnValue(capacity);
        cir.cancel();
    }

    @Shadow
    private float getCombinedCapacity() {
        return 0.f;
    }
    @Shadow
    private int getSpeedModifier() {
        return 0;
    }

    @Shadow public int movementDirection;
}