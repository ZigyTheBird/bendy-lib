package io.github.kosmx.bendylib.mixin.player_animator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.kosmx.playerAnim.api.PartKey;
import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.impl.IMutableModel;
import dev.kosmx.playerAnim.impl.IUpperPartHelper;
import io.github.kosmx.bendylib.impl.compatibility.PlayerBendHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Model.class)
public abstract class ModelMixin_playerAnim {
    @Inject(method = "renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V", at = @At("HEAD"), cancellable = true)
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color, CallbackInfo ci) {
        if(((Model)(Object)this) instanceof PlayerModel playerModel && ((IMutableModel)this).playerAnimator$getAnimation().isActive()){
            ((PlayerModelAccessor)playerModel).getBodyParts().forEach((part)->{
                if(!((IUpperPartHelper) part).playerAnimator$isUpperPart()){
                    part.render(matrices, vertices, light, overlay, color);
                }
            });
            matrices.pushPose();
            AnimationProcessor emoteSupplier = ((IMutableModel)playerModel).playerAnimator$getAnimation();
            PlayerBendHelper.rotateMatrixStack(matrices, emoteSupplier.getBend(PartKey.BODY));
            ((PlayerModelAccessor)playerModel).getBodyParts().forEach((part)->{
                if(((IUpperPartHelper) part).playerAnimator$isUpperPart()){
                    part.render(matrices, vertices, light, overlay, color);
                }
            });
            matrices.popPose();
            ci.cancel();
        }
    }
}
