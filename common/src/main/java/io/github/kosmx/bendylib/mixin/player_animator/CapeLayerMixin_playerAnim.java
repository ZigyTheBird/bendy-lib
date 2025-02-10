package io.github.kosmx.bendylib.mixin.player_animator;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.kosmx.playerAnim.api.PartKey;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.core.util.Pair;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.impl.IPlayerAnimationState;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import dev.kosmx.playerAnim.mixin.CapeLayerAccessor;
import io.github.kosmx.bendylib.impl.compatibility.PlayerBendHelper;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(CapeLayer.class)
public abstract class CapeLayerMixin_playerAnim extends RenderLayer<PlayerRenderState, PlayerModel> {
    @Shadow @Final private HumanoidModel<PlayerRenderState> model;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(RenderLayerParent renderLayerParent, EntityModelSet entityModelSet, EquipmentAssetManager equipmentAssetManager, CallbackInfo ci) {
        PlayerBendHelper.initCapeBend(((PlayerCapeModelAccessor)model).getCape());
    }

    public CapeLayerMixin_playerAnim(RenderLayerParent<PlayerRenderState, PlayerModel> renderLayerParent) {
        super(renderLayerParent);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/PlayerRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HumanoidModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"))
    private void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, PlayerRenderState playerRenderState, float f, float g, CallbackInfo ci) {
        AnimationApplier emote = ((IPlayerAnimationState) playerRenderState).playerAnimator$getAnimationApplier();
        if (model instanceof CapeLayerAccessor capeLayer) {
            if (emote.isActive()) {
                ModelPart torso = this.getParentModel().body;
                Pair<Float, Float> torsoBend = emote.getBend(PartKey.TORSO);
                Pair<Float, Float> bodyBend = emote.getBend(PartKey.BODY);
                bodyBend = new Pair<>(torsoBend.getLeft() + bodyBend.getLeft(), torsoBend.getRight() + bodyBend.getRight());

                poseStack.translate(torso.x / 16, torso.y / 16, torso.z / 16);
                poseStack.mulPose((new Quaternionf()).rotateXYZ(torso.xRot, torso.yRot, torso.zRot));
                PlayerBendHelper.rotateMatrixStack(poseStack, torsoBend);
                poseStack.translate(0.0F, 0.0F, 0.125F);
                poseStack.mulPose(Axis.YP.rotationDegrees(180));

                ModelPart cape = capeLayer.getCape();
                Vec3f transform = emote.get3DTransform(PartKey.CAPE, TransformType.POSITION, Vec3f.ZERO);
                Vec3f rotation = emote.get3DTransform(PartKey.CAPE, TransformType.ROTATION, Vec3f.ZERO);
                Vec3f scale = emote.get3DTransform(PartKey.CAPE, TransformType.SCALE, Vec3f.ONE);
                Pair<Float, Float> bend = emote.getBend(PartKey.CAPE);
                if (Objects.equals(bend.getRight(), bend.getLeft()) && bend.getLeft() == 0)
                    bend = bodyBend;

                cape.x = transform.getX();
                cape.y = transform.getY();
                cape.z = transform.getZ();
                cape.xRot = rotation.getX();
                cape.yRot = rotation.getY();
                cape.zRot = rotation.getZ();
                cape.xScale = scale.getX();
                cape.yScale = scale.getY();
                cape.zScale = scale.getZ();

                PlayerBendHelper.bend(cape, bend);
            } else {
                PlayerBendHelper.bend(capeLayer.getCape(), null);
            }
        }
    }


    @WrapWithCondition(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/PlayerRenderState;FF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    private boolean translate(PoseStack instance, float f, float g, float h, @Local(argsOnly = true) PlayerRenderState playerRenderState) {
        AnimationApplier emote = ((IPlayerAnimationState) playerRenderState).playerAnimator$getAnimationApplier();
        return !emote.isActive();
    }
}
