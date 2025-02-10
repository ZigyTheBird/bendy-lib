package io.github.kosmx.bendylib.neoforge;

import io.github.kosmx.bendylib.impl.Constants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;

@Mod(value = "bendylib", dist = Dist.CLIENT)
public class ForgeModInterface {
    public ForgeModInterface() {
        if (ModList.get().isLoaded("iris")) Constants.IS_IRIS = true;
    }
}
