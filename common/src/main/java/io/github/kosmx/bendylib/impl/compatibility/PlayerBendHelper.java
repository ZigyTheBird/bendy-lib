package io.github.kosmx.bendylib.impl.compatibility;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.kosmx.playerAnim.core.util.Pair;
import io.github.kosmx.bendylib.api.ModelPartAccessor;
import io.github.kosmx.bendylib.impl.BendableCuboidBuilder;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PlayerBendHelper {
    public static void bend(ModelPart modelPart, float axis, float rotation){
        // Don't enable bend until rotation is bigger than epsilon. This should avoid unnecessary heavy calculations.
        if (Math.abs(rotation) >= 0.0001f) {
            ModelPartAccessor.optionalGetCuboid(modelPart, 0).ifPresent(mutableCuboid -> mutableCuboid.getAndActivateMutator("bend").applyBend(axis, rotation));
        } else {
            ModelPartAccessor.optionalGetCuboid(modelPart, 0).ifPresent(mutableCuboid -> mutableCuboid.getAndActivateMutator(null));
        }
    }

    public static void bend(ModelPart modelPart, @Nullable Pair<Float, Float> pair){
        if(pair != null) {
            bend(modelPart, pair.getLeft(), pair.getRight());
        }
        else {
            ModelPartAccessor.optionalGetCuboid(modelPart, 0).ifPresent(mutableCuboid -> mutableCuboid.getAndActivateMutator(null));
        }
    }

    public static void initBend(ModelPart modelPart, Direction direction) {
        ModelPartAccessor.optionalGetCuboid(modelPart, 0).ifPresent(mutableModelPart -> mutableModelPart.registerMutator("bend", data -> new BendableCuboidBuilder().setDirection(direction).build(data)));
    }

    public static void initCapeBend(ModelPart modelPart) {
        ModelPartAccessor.optionalGetCuboid(modelPart, 0).ifPresent(mutableModelPart -> mutableModelPart.registerMutator("bend", data -> {
            data.pivot = 6;

            return new BendableCuboidBuilder().setDirection(Direction.UP).build(data);
        }));
    }

    public static void rotateMatrixStack(PoseStack matrices, Pair<Float, Float> pair){
        float offset = 0.375f;
        matrices.translate(0, offset, 0);
        float bend = pair.getRight();
        float axisf = - pair.getLeft();
        Vector3f axis = new Vector3f((float) Math.cos(axisf), 0, (float) Math.sin(axisf));
        matrices.mulPose(new Quaternionf().rotateAxis(bend, axis));
        matrices.translate(0, - offset, 0);
    }
}
