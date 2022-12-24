package io.github.anvilloystudio.minimods.mod.core.redstone.tiles;

import io.github.anvilloystudio.minimods.api.mixins.TilesMixin;

public class Tiles {
	public static void postInit() {
		TilesMixin.invokeAdd(61, new RedstoneTile());
		TilesMixin.invokeAdd(62, new RedstoneSwitchTile());
		TilesMixin.invokeAdd(63, new ButtonTile());
		TilesMixin.invokeAdd(64, new PermanentRedstoneTransmitterTile());
		TilesMixin.invokeAdd(65, new DaylightDetectorTile());
		TilesMixin.invokeAdd(66, new PressurePlateTile());
		TilesMixin.invokeAdd(67, new RedstoneTorchTile());
		TilesMixin.invokeAdd(68, new RepeaterTile());
		TilesMixin.invokeAdd(69, new ComparatorTile());
	}
}
