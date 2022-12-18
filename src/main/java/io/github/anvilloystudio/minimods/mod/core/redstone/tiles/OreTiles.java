package io.github.anvilloystudio.minimods.mod.core.redstone.tiles;

import io.github.anvilloystudio.minimods.api.GraphicComp;
import io.github.anvilloystudio.minimods.api.ModTileGen;
import io.github.anvilloystudio.minimods.api.OreTypeMixinEnumUtil;
import io.github.anvilloystudio.minimods.api.mixins.OreTileMixin;
import io.github.anvilloystudio.minimods.api.mixins.TilesMixin;
import minicraft.item.Items;
import minicraft.level.tile.OreTile;
import minicraft.level.tile.OreTile.OreType;
import minicraft.level.tile.Tiles;

import java.io.IOException;
import java.util.Random;

public class OreTiles {
	public static void init() {
		// Redstone Ore
		OreTypeMixinEnumUtil.addVariant(new OreTypeMixinEnumUtil.OreTypeMixinEnumData("Redstone", () -> Items.get("Redstone"), 0));
		new ModTileGen(-1, OreTiles::redstoneGenerationUnderground);
		new ModTileGen(-2, OreTiles::redstoneGenerationUnderground);
		new ModTileGen(-3, OreTiles::redstoneGenerationUnderground);
	}

	public static void postInit() {
		try {
			// Redstone Ore
			OreTile redstoneTile = OreTileMixin.invokeInit(OreType.valueOf("Redstone"));
			((OreTileMixin) redstoneTile).setSprite(GraphicComp.getSpriteFromSheet(2, 2,
				GraphicComp.getSpriteSheetFromInputStream(OreTiles.class.getResourceAsStream("/assets/textures/tiles/redstone_ore.png"))));
			TilesMixin.invokeAdd(60, redstoneTile);
		} catch (IOException e) {
			throw new RuntimeException("Unable to initialize tile(s).", e);
		}
	}

	private static void redstoneGenerationUnderground(byte[] map, byte[] data, int layer, int w, int h, Random random) {
		int r = 2;
		for (int i = 0; i < w * h / 40 * Math.abs(layer); i++) { // How many times attempting to generate.
			int x = random.nextInt(w); // Random select a location.
			int y = random.nextInt(h);
			for (int j = 0; j < 20; j++) { // How large for each ore vein.
				int xx = x + random.nextInt(5) - random.nextInt(5); // Random select a tile for the vein.
				int yy = y + random.nextInt(5) - random.nextInt(5);
				if (xx >= r && yy >= r && xx < w - r && yy < h - r) { // If the location of the tile is valid.
					if (map[xx + yy * w] == Tiles.get("rock").id) {
						map[xx + yy * w] = (byte) ((Tiles.get("Redstone Ore").id & 0xff));
					}
				}
			}
		}
	}
}
