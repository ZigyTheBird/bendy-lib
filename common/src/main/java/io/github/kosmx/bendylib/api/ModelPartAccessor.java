package io.github.kosmx.bendylib.api;

import io.github.kosmx.bendylib.WorkaroundEnum;
import io.github.kosmx.bendylib.impl.accessors.IModelPartAccessor;
import net.minecraft.client.model.geom.ModelPart;

import java.util.*;

/**
 * Access to children and cuboids in {@link ModelPart}
 * Don't have to reinterpret the object...
 */
public final class ModelPartAccessor {

    public static Map<String, ModelPart> getChildren(ModelPart modelPart){
        return ((IModelPartAccessor)modelPart).getChildren();
    }

    public static void setWorkaround(ModelPart modelPart, WorkaroundEnum workaroundEnum) {
        if (modelPart != null) ((IModelPartAccessor)modelPart).setWorkaround(workaroundEnum);
    }

    /**
     * Get a cuboid, and cast it to {@link MutableCuboid}
     */
    public static Optional<MutableCuboid> optionalGetCuboid(ModelPart modelPart, int index){
        if(modelPart == null || getCuboids(modelPart) == null || getCuboids(modelPart).size() <= index) return Optional.empty();
        return Optional.of((MutableCuboid)getCuboids(modelPart).get(index));
    }

    public static List<ModelPart.Cube> getCuboids(ModelPart modelPart){
        return ((IModelPartAccessor)modelPart).getCuboids();
    }
}
