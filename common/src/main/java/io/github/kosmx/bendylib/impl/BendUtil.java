package io.github.kosmx.bendylib.impl;

import net.minecraft.core.Direction;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.function.Consumer;

public class BendUtil {
    public static BendApplier getBend(BendableCuboid cuboid, float bendAxis, float bendValue) {
        return getBend(cuboid.getBendDirection(), cuboid.getBendX(), cuboid.getBendY(), cuboid.getBendZ(),
                cuboid.basePlane, cuboid.otherPlane, cuboid.isBendInverted(), cuboid.bendHeight(), bendAxis, bendValue);
    }

    /**
     * Applies the transformation to every position in posSupplier
     * @param bendAxis axis for the bend
     * @param bendValue bend value
     */
    public static BendApplier getBend(Direction bendDirection, float bendX, float bendY, float bendZ, Plane basePlane, Plane otherPlane,
                                      boolean isBendInverted, float bendHeight, float bendAxis, float bendValue){
        Vector3f axis = new Vector3f((float) Math.cos(bendAxis), 0, (float) Math.sin(bendAxis));
        Matrix3f matrix3f = new Matrix3f().set(bendDirection.getRotation());
        axis.mul(matrix3f);
        Matrix4f transformMatrix = new Matrix4f();

        transformMatrix.translate(bendX, bendY, bendZ);
        transformMatrix.rotate(bendValue, axis);
        transformMatrix.translate(-bendX, -bendY, -bendZ);

        Vector3f directionUnit;

        directionUnit = bendDirection.step();
        directionUnit.cross(axis);
        //parallel to the bend's axis and to the cube's bend direction
        Plane bendPlane = new Plane(directionUnit, new Vector3f(bendX, bendY, bendZ));
        float halfSize = bendHeight/2;

        return new BendApplier(transformMatrix, pos -> {
            float distFromBend = isBendInverted ? -bendPlane.distanceTo(pos) : bendPlane.distanceTo(pos);
            float distFromBase = basePlane.distanceTo(pos);
            float distFromOther = otherPlane.distanceTo(pos);
            double s = Math.tan(bendValue/2)*distFromBend;
            Vector3f x = bendDirection.step();
            if(Math.abs(distFromBase) < Math.abs(distFromOther)){
                x.mul((float) (-distFromBase/halfSize*s));
                pos.add(x);
                Vector4f reposVector = new Vector4f(pos, 1f);
                reposVector.mul(transformMatrix);
                pos = new Vector3f(reposVector.x, reposVector.y, reposVector.z);
            }
            else {
                x.mul((float) (-distFromOther/halfSize*s));
                pos.add(x);
            }
            return pos;
        });
    }
}
