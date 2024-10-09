package com.natim6.lazyengines.mixin;

import com.natim6.lazyengines.Config;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.fluids.tank.BoilerData;
import com.simibubi.create.content.kinetics.BlockStressValues;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = BoilerData.class, remap = false)
public class BoilerDataMixin {
    @Shadow public int attachedWhistles;

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/fluids/tank/BoilerData;getActualHeat(I)I"))
    private int getNotActualHeatValue(BoilerData instance, int boilerSize) {
        int forBoilerSize = getMaxHeatLevelForBoilerSize(boilerSize);
        int forWaterSupply = getMaxHeatLevelForWaterSupply();
        int actualHeat = Math.min(activeHeat, Math.min(forWaterSupply, forBoilerSize));
        return Math.floorDiv(actualHeat * 18, Config.getMaxLevel()); // It needs to equal 18 in the code
    }

    @Inject(method = "getMaxHeatLevelForBoilerSize", at = @At("HEAD"), cancellable = true)
    private void getMaxHeatLevelForBoilerSizeMixin(int boilerSize, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(Math.min(Config.getMaxLevel(), boilerSize / Config.TANKS_PER_HEAT.get()));
        cir.cancel();
    }

    @Inject(method = "getMaxHeatLevelForWaterSupply", at = @At("HEAD"), cancellable = true)
    private void getMaxHeatLevelForWaterSupplyMixin(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(Math.min(Config.getMaxLevel(), Mth.ceil(waterSupply) / Config.WATER_REQUIRED.get()));
        cir.cancel();
    }

    @Inject(method = "getHeatLevelTextComponent", at = @At("HEAD"), cancellable = true)
    public void getHeatLevelTextComponentMixin(CallbackInfoReturnable<MutableComponent> cir) {
        int boilerLevel = Math.min(activeHeat, Math.min(maxHeatForWater, maxHeatForSize));

        cir.setReturnValue(isPassive() ? Lang.translateDirect("boiler.passive")
                : (boilerLevel == 0 ? Lang.translateDirect("boiler.idle")
                : boilerLevel == Config.getMaxLevel() ? Lang.translateDirect("boiler.max_lvl")
                : Lang.translateDirect("boiler.lvl", String.valueOf(boilerLevel))));
        cir.cancel();
    }

    @Inject(method = "barComponent", at = @At("HEAD"), cancellable = true)
    private void barComponentMixin(int level, CallbackInfoReturnable<MutableComponent> cir) {
        cir.setReturnValue(Components.empty()
                .append(bars(Math.max(0, minValue - 1), ChatFormatting.DARK_GREEN))
                .append(bars(minValue > 0 ? 1 : 0, ChatFormatting.GREEN))
                .append(bars(Math.max(0, level - minValue), ChatFormatting.DARK_GREEN))
                .append(bars(Math.max(0, maxValue - level), ChatFormatting.DARK_RED))
                .append(bars(Math.max(0, Math.min(Config.SEETHING_BURNER.get()*9 - maxValue, ((maxValue / 5 + 1) * 5) - maxValue)),
                        ChatFormatting.DARK_GRAY)));
        cir.cancel();
    }

    @Inject(method = "getEngineEfficiency", at = @At("HEAD"), cancellable = true)
    public void getEngineEfficiencyMixin(int boilerSize, CallbackInfoReturnable<Float> cir) {
        if (isPassive(boilerSize)) {
            cir.setReturnValue(Config.PASSIVE_EFFICIENCY.get().floatValue() / attachedEngines);
            cir.cancel(); // Thanks LiukRast for the correction!
        }
        if (activeHeat == 0) {
            cir.setReturnValue(0.f);
            cir.cancel();
        }
        double actualHeat = getActualHeat(boilerSize);
        double attachedPower = attachedEngines * Config.ENGINE_POWER.get();
        cir.setReturnValue(attachedPower <= actualHeat ? 1 : (float) (actualHeat / attachedPower));
        cir.cancel();
    }

    @ModifyVariable(method = "addToGoggleTooltip", at = @At("STORE"), ordinal = 0)
    private double correctTotalSU(double totalSU, List<Component> tooltip, boolean isPlayerSneaking, int boilerSize) {
        int boilerLevel = Math.min(activeHeat, Math.min(maxHeatForWater, maxHeatForSize));
        return getEngineEfficiency(boilerSize) * 16 * Math.max(boilerLevel, attachedEngines * Config.ENGINE_POWER.get())
                * BlockStressValues.getCapacity(AllBlocks.STEAM_ENGINE.get());
    }

    @Shadow
    public float getEngineEfficiency(int boilerSize) { return 0; }
    @Shadow
    public int getMaxHeatLevelForWaterSupply() {
        return 0;
    }
    @Shadow
    public int getMaxHeatLevelForBoilerSize(int boilerSize) {
        return 0;
    }
    @Shadow
    public boolean isPassive() {
        return true;
    }
    @Shadow
    public boolean isPassive(int boilerSize) {
        return true;
    }
    @Shadow
    private MutableComponent bars(int level, ChatFormatting format) {
        return Components.empty();
    }
    @Shadow
    private int getActualHeat(int boilerSize) {
        return 0;
    }

    @Shadow public float waterSupply;
    @Final @Shadow private static int waterSupplyPerLevel;
    @Shadow public int activeHeat;
    @Shadow private int maxHeatForWater;
    @Shadow private int maxHeatForSize;
    @Shadow private int minValue;
    @Shadow private int maxValue;
    @Shadow public int attachedEngines;
}
