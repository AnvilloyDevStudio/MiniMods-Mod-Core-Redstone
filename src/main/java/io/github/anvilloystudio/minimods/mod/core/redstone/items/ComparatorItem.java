package io.github.anvilloystudio.minimods.mod.core.redstone.items;

import minicraft.core.Game;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.entity.mob.RemotePlayer;
import minicraft.gfx.Sprite;
import minicraft.item.TileItem;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

public class ComparatorItem extends TileItem {
	public ComparatorItem(String name, Sprite sprite, String model, String... validTiles) {
		super(name, sprite, model, validTiles);
	}

	@Override
	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, Direction attackDir) {
		for (String tilename : validTiles) {
			if (tile.matches(level.getData(xt, yt), tilename)) {
				level.setTile(xt, yt, model); // TODO maybe data should be part of the saved tile..?
				level.setData(xt, yt, player.dir.getDir() << 2); // TODO Applying this when updating the codes.

				Sound.place.play();

				return super.interactOn(true);
			}
		}

		if (Game.debug) System.out.println(model + " cannot be placed on " + tile.name);

		String note = "";
		if (model.contains("WALL")) {
			note = "Can only be placed on " + Tiles.getName(validTiles.get(0)) + "!";
		}
		else if (model.contains("DOOR")) {
			note = "Can only be placed on " + Tiles.getName(validTiles.get(0)) + "!";
		}
		else if ((model.contains("BRICK") || model.contains("PLANK"))) {
			note = "Dig a hole first!";
		}

		if (note.length() > 0) {
			if (!Game.isValidServer())
				Game.notifications.add(note);
			else
				Game.server.getAssociatedThread((RemotePlayer)player).sendNotification(note, 0);
		}

		return super.interactOn(false);
	}

	public ComparatorItem clone() {
		return new ComparatorItem(getName(), sprite, model, validTiles.toArray(new String[0]));
	}
}
