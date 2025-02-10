package io.github.kosmx.bendylib.mixin.player_animator;

import dev.kosmx.playerAnim.impl.IMutableModel;
import dev.kosmx.playerAnim.impl.IUpperPartHelper;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import io.github.kosmx.bendylib.impl.compatibility.PlayerBendHelper;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(value = PlayerModel.class, priority = 2001)
public abstract class PlayerModelMixin_playerAnim implements IMutableModel {
    @Shadow
    @Final
    public ModelPart jacket;
    @Shadow
    @Final
    public ModelPart rightSleeve;
    @Shadow
    @Final
    public ModelPart leftSleeve;
    @Shadow @Final public ModelPart rightPants;
    @Shadow @Final public ModelPart leftPants;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initBendableStuff(ModelPart modelPart, boolean bl, CallbackInfo ci) {
        PlayerModel model = ((PlayerModel)(Object)this);
        bendy_lib$addBendMutator(model.body, Direction.DOWN);
        bendy_lib$addBendMutator(model.rightArm, Direction.UP);
        bendy_lib$addBendMutator(model.leftArm, Direction.UP);
        bendy_lib$addBendMutator(model.rightLeg, Direction.UP);
        bendy_lib$addBendMutator(model.leftLeg, Direction.UP);
        bendy_lib$addBendMutator(this.jacket, Direction.DOWN);
        bendy_lib$addBendMutator(this.rightPants, Direction.UP);
        bendy_lib$addBendMutator(this.rightSleeve, Direction.UP);
        bendy_lib$addBendMutator(this.leftPants, Direction.UP);
        bendy_lib$addBendMutator(this.leftSleeve, Direction.UP);
        ((IUpperPartHelper)model.head).playerAnimator$setUpperPart(true);
        ((IUpperPartHelper)model.rightArm).playerAnimator$setUpperPart(true);
        ((IUpperPartHelper)model.leftArm).playerAnimator$setUpperPart(true);
    }

    @Unique
    private void bendy_lib$addBendMutator(ModelPart part, Direction d){
        PlayerBendHelper.initBend(part, d);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;)V", at = @At(value = "RETURN"))
    private void setupPlayerAnimation(PlayerRenderState playerRenderState, CallbackInfo ci) {
        if (this.playerAnimator$getAnimation() == AnimationApplier.EMPTY) {
            PlayerModel model = ((PlayerModel)(Object)this);
            resetBend(model.body);
            resetBend(model.leftArm);
            resetBend(model.rightArm);
            resetBend(model.leftLeg);
            resetBend(model.rightLeg);
            resetBend(this.jacket);
            resetBend(this.rightPants);
            resetBend(this.rightSleeve);
            resetBend(this.leftPants);
            resetBend(this.leftSleeve);
        }
    }

    @Unique
    private static void resetBend(ModelPart part) {
        PlayerBendHelper.bend(part, null);
    }
}
