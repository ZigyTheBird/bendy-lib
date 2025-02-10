package io.github.kosmx.bendylib.fabric;

import io.github.kosmx.bendylib.impl.Constants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class Init implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("iris")) Constants.IS_IRIS = true;
    }
}
