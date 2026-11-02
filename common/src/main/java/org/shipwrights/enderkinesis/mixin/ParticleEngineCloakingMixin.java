package org.shipwrights.enderkinesis.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.shipwrights.enderkinesis.client.CloakedParticleBuffer;
import org.shipwrights.enderkinesis.client.CloakingMixinSupport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Pass 1 of the cloaked-particle pipeline: capture.
 *
 * <p>For each {@code Particle.render(VertexConsumer, Camera, float)} call inside
 * {@code ParticleEngine.render}, if the particle's world position falls inside a
 * concealed ship, skip the vanilla draw (which would write to main FB) and instead
 * record the particle in [CloakedParticleBuffer]. Pass 2 — the side-FB flush —
 * runs from [LevelRendererBubblePassMixin] at {@code renderLevel} TAIL, alongside
 * the cloaked-entity flush. That timing is known to land in the side FB correctly
 * (cloaked entities render fine); an earlier attempt that flushed from inside
 * {@code ParticleEngine.render}'s @At("RETURN") issued valid GL draws but produced
 * no visible pixels in the side FB.
 */
@Mixin(ParticleEngine.class)
public abstract class ParticleEngineCloakingMixin {

    // Forge turns vanilla's 5-arg render into a deprecated delegate and moves the loop
    // into its own overload that takes a Frustum. The bare "render" is pinned to the
    // vanilla descriptor by the refmap, so the Forge overload has to be listed explicitly;
    // on Fabric it simply matches nothing.
    @WrapOperation(
        method = {
            "render",
            "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;)V"
        },
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/particle/Particle;render(Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/Camera;F)V")
    )
    private void enderkinesis$captureIfConcealed(
        Particle particle, VertexConsumer buffer, Camera camera, float partialTick,
        Operation<Void> original
    ) {
        // Fast-path: no concealed ships → skip the position accessor
        // cast + 3 field reads + isWorldPointConcealed call entirely.
        // Particles render hundreds-to-thousands per frame; spark
        // showed this wrap accruing ~1.2s self / 10.7s cum even with
        // the inner isWorldPointConcealed already early-exiting, due
        // to the per-particle wrap dispatch + accessor work that ran
        // regardless. The empty-set check is a single hashmap.isEmpty.
        if (!CloakingMixinSupport.isAnyShipConcealed()) {
            original.call(particle, buffer, camera, partialTick);
            return;
        }
        ParticlePositionAccessor pos = (ParticlePositionAccessor) particle;
        if (CloakingMixinSupport.isWorldPointConcealed(
            pos.enderkinesis$getX(), pos.enderkinesis$getY(), pos.enderkinesis$getZ()
        )) {
            CloakedParticleBuffer.capture(particle);
            return;
        }
        original.call(particle, buffer, camera, partialTick);
    }
}
