package com.natim6.lazyengines.mixin;

import com.natim6.lazyengines.Config;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.fluids.tank.BoilerData;
import com.simibubi.create.content.kinetics.BlockStressValues;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.throwables.MixinException;

import java.util.List;

@Mixin(value = BoilerData.class, remap = false)
public class BoilerDataMixin {

    @ModifyConstant(method = "getMaxHeatLevelForBoilerSize", constant = @Constant(intValue = 4))
    private int lazy_engines$getMaxHeatLevelForBoilerSize(int constant) {
        return Config.TANKS_PER_HEAT.get();
    }

    @ModifyConstant(method = {"getMaxHeatLevelForWaterSupply", "getHeatLevelTextComponent", "barComponent", "tick"}, constant = @Constant(intValue = 18))
    private int lazy_engines$replace18Constant(int constant) {
        return Config.getMaxLevel();
    }

    @ModifyConstant(method = "getMaxHeatLevelForWaterSupply", constant = @Constant(intValue = 10))
    private int lazy_engines$replaceWaterSupplyPerLevel(int constant) {
        return Config.WATER_REQUIRED.get();
    }

    @Inject(method = "getEngineEfficiency", at = @At("HEAD"), cancellable = true)
    public void lazy_engines$getEngineEfficiencyMixin(int boilerSize, CallbackInfoReturnable<Float> cir) {
        double attachedPower = attachedEngines * Config.ENGINE_POWER.get();
        if (isPassive(boilerSize)) {
            cir.setReturnValue((float)(Config.PASSIVE_EFFICIENCY.get() / attachedPower));
            cir.cancel(); // Thanks LiukRast for the correction!
        }
        if (activeHeat == 0) {
            cir.setReturnValue(0.f);
            cir.cancel();
        }
        double actualHeat = getActualHeat(boilerSize);
        cir.setReturnValue(attachedPower <= actualHeat ? 1 : (float) (actualHeat / attachedPower));
        cir.cancel();
    }

    @ModifyVariable(method = "addToGoggleTooltip", at = @At("STORE"), ordinal = 0)
    private double lazy_engines$correctTotalSU(double totalSU, List<Component> tooltip, boolean isPlayerSneaking, int boilerSize) {
        int boilerLevel = Math.min(activeHeat, Math.min(maxHeatForWater, maxHeatForSize));
        return getEngineEfficiency(boilerSize) * 16 * Math.max(boilerLevel, attachedEngines * Config.ENGINE_POWER.get())
                * BlockStressValues.getCapacity(AllBlocks.STEAM_ENGINE.get());
    }

    @Shadow
    public float getEngineEfficiency(int boilerSize) {
        throw new MixinException("Mixin application failed");
    }
    @Shadow
    public boolean isPassive(int boilerSize) {
        throw new MixinException("Mixin application failed");
    }
    @Shadow
    private int getActualHeat(int boilerSize) {
        throw new MixinException("Mixin application failed");
    }

    @Shadow public int activeHeat;
    @Shadow private int maxHeatForWater;
    @Shadow private int maxHeatForSize;
    @Shadow public int attachedEngines;
}
