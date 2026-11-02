package org.shipwrights.enderkinesis.mixin;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelTimeAccess;
import org.shipwrights.enderkinesis.client.WohlonBiomeSkyState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Snaps {@code getTimeOfDay} to Wohlon's dusk once the biome blend reaches the sky threshold. A lerp
 * swirls the sun and moon; {@link LevelSkyDarkenSmoothMixin} smooths the brightness step instead.
 *
 * **Target gotcha:** {@link LevelTimeAccess} is the only valid mixin target — 1.20.1 declares
 * {@code getTimeOfDay(F)F} as a default method on this interface, NOT on Level/ClientLevel.
 * The handler is a public {@code default} method because Forge's Mixin 0.8.5 rejects non-public
 * interface mixin members outright, dropping the whole mixin.
 */
@Mixin(LevelTimeAccess.class)
public interface LevelTimeAccessGetTimeOfDayWohlonBiomeMixin {

    @Inject(method = "getTimeOfDay(F)F", at = @At("RETURN"), cancellable = true)
    default void enderkinesis$blendWohlonTimeOfDay(float partialTick, CallbackInfoReturnable<Float> cir) {
        if (WohlonBiomeSkyState.getCurrentBlend() <= 0f) return;
        // The integrated server shares the JVM, so its levels and WorldGenRegions also reach this.
        Object self = this;
        if (!(self instanceof Level level) || !level.isClientSide) return;
        float actual = cir.getReturnValueF();
        float blended = WohlonBiomeSkyState.blendTimeOfDay(actual);
        if (blended != actual) cir.setReturnValue(blended);
    }
}
