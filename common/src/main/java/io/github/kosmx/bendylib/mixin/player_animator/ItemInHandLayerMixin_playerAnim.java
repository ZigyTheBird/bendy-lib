package io.github.kosmx.bendylib.mixin.player_animator;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.kosmx.playerAnim.api.PartKey;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.core.util.Pair;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.impl.IPlayerAnimationState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin_playerAnim<S extends ArmedEntityRenderState> {
    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V", ordinal = 0))
    private void renderMixin(S armedEntityRenderState, ItemStackRenderState itemStackRenderState, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci){
        if(armedEntityRenderState instanceof IPlayerAnimationState state){
            if(state.playerAnimator$getAnimationApplier().isActive()){
                AnimationProcessor anim = state.playerAnimator$getAnimationApplier();
                Vec3f data = anim.get3DTransform(humanoidArm == HumanoidArm.LEFT ? PartKey.LEFT_ARM : PartKey.RIGHT_ARM, TransformType.BEND, new Vec3f(0f, 0f, 0f));
                Pair<Float, Float> pair = new Pair<>(data.getX(), data.getY());
                float offset = 0.25f;
                poseStack.translate(0, offset, 0);
                float bend = pair.getRight();
                float axisf = - pair.getLeft();
                Vector3f axis = new Vector3f((float) Math.cos(axisf), 0, (float) -Math.sin(axisf));
                poseStack.mulPose(new Quaternionf().rotateAxis(bend, axis));
                poseStack.translate(0, - offset, 0);
            }
        }
    }
}
