package org.shipwrights.enderkinesis.platform.fabric;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.shipwrights.enderkinesis.fabric.mixin.LootTableAccessor;

import java.util.Arrays;
import java.util.List;

public final class LootPlatformImpl {
    private LootPlatformImpl() {}

    public static List<LootPool> pools(LootTable table) {
        return Arrays.asList(((LootTableAccessor) table).enderkinesis$getPools());
    }
}
