package io.github.anvilloystudio.minimods.mod.core.redstone.tiles;

import io.github.anvilloystudio.minimods.api.mixins.TilesMixin;

public class Tiles {
	public static void postInit() {
		TilesMixin.invokeAdd(61, new RedstoneTile());
	}
}
