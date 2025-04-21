package com.natim6.lazyengines;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod("lazyengines")
public class LazyEngines
{
    public LazyEngines(ModContainer container)
    {
        container.registerConfig(ModConfig.Type.SERVER, Config.SPEC);
    }
}
