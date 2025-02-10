package io.github.kosmx.bendylib.impl.compatibility;

import net.irisshaders.iris.Iris;

public class IrisCompat {
    public static boolean isShaderEnabled() {
        return Iris.getIrisConfig().areShadersEnabled();
    }
}
