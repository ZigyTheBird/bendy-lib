package io.github.kosmx.bendylib.mixin.player_animator;

import dev.kosmx.playerAnim.api.PartKey;
import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.core.util.Pair;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import io.github.kosmx.bendylib.impl.compatibility.PlayerBendHelper;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimationApplier.class)
public class AnimationApplierMixin_playerAnim {
    @Inject(method = "updatePart", at = @At("TAIL"))
    private void inject(PartKey partKey, ModelPart part, CallbackInfo ci) {
        AnimationProcessor processor = ((AnimationProcessor)(Object)this);
        if (partKey != PartKey.HEAD) {
            if (partKey == PartKey.TORSO) {
                Pair<Float, Float> torsoBend = processor.getBend(partKey);
                Pair<Float, Float> bodyBend = processor.getBend(PartKey.BODY);
                Pair<Float, Float> bend = new Pair<>(torsoBend.getLeft() + bodyBend.getLeft(), torsoBend.getRight() + bodyBend.getRight());
                PlayerBendHelper.bend(part, bend);
                String childName = bendy_lib$getChildForPart(partKey);
                if (part.hasChild(childName)) PlayerBendHelper.bend(part.getChild(childName), bend);
            } else {
                Pair<Float, Float> bend = processor.getBend(partKey);
                PlayerBendHelper.bend(part, bend);
                String childName = bendy_lib$getChildForPart(partKey);
                if (part.hasChild(childName)) PlayerBendHelper.bend(part.getChild(childName), bend);
            }
        }
    }

    @Unique
    private String bendy_lib$getChildForPart(PartKey partKey) {
        if (partKey == PartKey.HEAD) {
            return "hat";
        }
        if (partKey == PartKey.TORSO) {
            return "jacket";
        }
        if (partKey == PartKey.RIGHT_ARM) {
            return "right_sleeve";
        }
        if (partKey == PartKey.LEFT_ARM) {
            return "left_sleeve";
        }
        if (partKey == PartKey.RIGHT_LEG) {
            return "right_pants";
        }
        if (partKey == PartKey.LEFT_LEG) {
            return "left_pants";
        }
        return null;
    }
}
