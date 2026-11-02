package org.shipwrights.enderkinesis.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;

/** Forge turns {@code LootTable.pools} from vanilla's array into a list, so each loader reads it with its own accessor. */
public final class LootPlatform {
    private LootPlatform() {}

    @ExpectPlatform
    public static List<LootPool> pools(LootTable table) {
        throw new AssertionError();
    }
}
