package io.github.kosmx.bendylib.mixin.player_animator;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PlayerModel.class)
public interface PlayerModelAccessor {
    @Accessor
    List<ModelPart> getBodyParts();
}
