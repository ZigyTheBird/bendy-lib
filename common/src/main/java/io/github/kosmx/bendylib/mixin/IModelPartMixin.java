package io.github.kosmx.bendylib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.kosmx.bendylib.WorkaroundEnum;
import io.github.kosmx.bendylib.api.MutableCuboid;
import io.github.kosmx.bendylib.impl.accessors.CuboidSideAccessor;
import io.github.kosmx.bendylib.impl.accessors.IModelPartAccessor;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(ModelPart.class)
public abstract class IModelPartMixin implements IModelPartAccessor {

    @Shadow @Final private Map<String, ModelPart> children;


    @Shadow @Final private List<ModelPart.Cube> cubes;

    @Unique
    private boolean hasMutatedCuboid = false;
    /**
     * VanillaDraw won't cause a slowdown in vanilla and will fix many issues.
     * If needed, use {@link IModelPartAccessor#setWorkaround(WorkaroundEnum)} to set the workaround consumer
     * {@link WorkaroundEnum#None} to do nothing about it.
     * It will work in Vanilla, but not with Sodium.
     */
    @Unique
    private WorkaroundEnum workaround = WorkaroundEnum.VanillaDraw;

    @Override
    public List<ModelPart.Cube> getCuboids() {
        hasMutatedCuboid = true;
        return cubes;
    }

    @Override
    public Map<String, ModelPart> getChildren() {
        return children;
    }

    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void copyTransformExtended(ModelPart part, CallbackInfo ci){
        if(((IModelPartAccessor)part).getCuboids() == null || cubes == null) return; // Not copying state
        Iterator<ModelPart.Cube> iterator0 = ((IModelPartAccessor)part).getCuboids().iterator();
        Iterator<ModelPart.Cube> iterator1 = cubes.iterator();

        while (iterator0.hasNext() && iterator1.hasNext()){
            MutableCuboid cuboid1 = (MutableCuboid) iterator1.next();
            MutableCuboid cuboid0 = (MutableCuboid) iterator0.next();
            cuboid1.copyStateFrom(cuboid0);
        }

    }

    @WrapOperation(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;compile(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"), require = 0) //It might not find anything if OF already broke the game
    private void redirectRenderCuboids(ModelPart modelPart, PoseStack.Pose entry, VertexConsumer vertexConsumer, int light, int overlay, int color, Operation<Void> original){
        redirectedFunction(modelPart, entry, vertexConsumer, light, overlay, color, original);
    }

    @Unique
    private void redirectedFunction(ModelPart modelPart, PoseStack.Pose entry, VertexConsumer vertexConsumer, int light, int overlay, int color, Operation<Void> original) {
        if(workaround == WorkaroundEnum.VanillaDraw){
            if(!hasMutatedCuboid || cubes.size() == 1 && ((MutableCuboid)cubes.get(0)).getActiveMutator() == null){
                original.call(modelPart, entry, vertexConsumer, light, overlay, color);
            }
            else {
                for(ModelPart.Cube cuboid:cubes){
                    cuboid.compile(entry, vertexConsumer, light, overlay, color);
                }
            }
        }
        else if(workaround == WorkaroundEnum.ExportQuads){
            for(ModelPart.Cube cuboid:cubes){
                ((CuboidSideAccessor)cuboid).doSideSwapping(); //:D
            }

            original.call(modelPart, entry, vertexConsumer, light, overlay, color);

            for(ModelPart.Cube cuboid:cubes){
                ((CuboidSideAccessor)cuboid).resetSides(); //:D
            }
        }
        else {
            original.call(modelPart, entry, vertexConsumer, light, overlay, color);
        }
    }

    @Override
    public void setWorkaround(WorkaroundEnum workaround) {
        this.workaround = workaround;
    }
}
