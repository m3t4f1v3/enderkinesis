package org.shipwrights.enderkinesis.forge.mixin;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

/**
 * Forge's {@code LootTable.pools} is a {@code List}; vanilla's is an array. See {@link org.shipwrights.enderkinesis.platform.LootPlatform}.
 *
 * <p>Mojmap only names the vanilla array field, so Loom leaves Forge's retyped one as {@code f_79109_} in dev,
 * which is also its SRG name in production.
 */
@Mixin(LootTable.class)
public interface LootTableForgeAccessor {
    @Accessor(value = "f_79109_", remap = false)
    List<LootPool> enderkinesis$getPools();
}
