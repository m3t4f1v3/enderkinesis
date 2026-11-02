package org.shipwrights.enderkinesis.platform.forge;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.shipwrights.enderkinesis.forge.mixin.LootTableForgeAccessor;

import java.util.List;

public final class LootPlatformImpl {
    private LootPlatformImpl() {}

    public static List<LootPool> pools(LootTable table) {
        return ((LootTableForgeAccessor) table).enderkinesis$getPools();
    }
}
