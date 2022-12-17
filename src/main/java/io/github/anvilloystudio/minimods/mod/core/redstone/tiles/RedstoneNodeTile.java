package io.github.anvilloystudio.minimods.mod.core.redstone.tiles;

import minicraft.entity.Direction;
import minicraft.gfx.Sprite;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public abstract class RedstoneNodeTile extends Tile {
	protected RedstoneNodeTile(String name, Sprite sprite) {
		super(name, sprite);
	}

	public abstract class RedstoneTransmitter extends RedstoneNodeTile {
		protected RedstoneTransmitter(String name, Sprite sprite) {
			super(name, sprite);
		}
	}

	public abstract class RedstoneReceiver extends RedstoneNodeTile {
		protected RedstoneReceiver(String name, Sprite sprite) {
			super(name, sprite);
		}
	}

	/**
	 * Getting all connectable directions the tile could.
	 * @return The set of directions. Empty if it depends on the tile state.
	 */
	@NotNull
	public abstract EnumSet<Direction> getConnectableDirections();

	/**
	 * Getting all connectable directions the tile could, but depending on the tile state.
	 * @param level The level of tile being on.
	 * @param x The x-coordinate of the tile.
	 * @param y The y-coordinate of the tile.
	 * @return The set of directions. By default, this returns the result of {@link #getConnectableDirections()}.
	 */
	@NotNull
	public EnumSet<Direction> getConnectableDirections(Level level, int x, int y) {
		return getConnectableDirections();
	}

	/**
	 * Getting all connected directions the tile have.
	 * @param level The level of tile being on.
	 * @param x The x-coordinate of the tile.
	 * @param y The y-coordinate of the tile.
	 * @return The set of directions.
	 */
	@NotNull
	public abstract EnumSet<Direction> getConnectedDirections(Level level, int x, int y);
}
