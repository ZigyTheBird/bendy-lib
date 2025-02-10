package io.github.kosmx.bendylib.mixin.player_animator;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.kosmx.playerAnim.api.PartKey;
import dev.kosmx.playerAnim.impl.IPlayerAnimationState;
import dev.kosmx.playerAnim.impl.IUpperPartHelper;
import io.github.kosmx.bendylib.impl.compatibility.PlayerBendHelper;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntityRenderer.class, priority = 2000)
public class LivingEntityMixin_playerAnim<S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
    @Inject(method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/RenderLayer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/EntityRenderState;FF)V"))
    private void inject(S livingEntityRenderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci,  @Local RenderLayer<S, M> layer) {
        if (livingEntityRenderState instanceof IPlayerAnimationState state && state.playerAnimator$getAnimationApplier().isActive() && ((IUpperPartHelper) layer).playerAnimator$isUpperPart()) {
            PlayerBendHelper.rotateMatrixStack(poseStack, state.playerAnimator$getAnimationApplier().getBend(PartKey.BODY));
        }
    }
}
