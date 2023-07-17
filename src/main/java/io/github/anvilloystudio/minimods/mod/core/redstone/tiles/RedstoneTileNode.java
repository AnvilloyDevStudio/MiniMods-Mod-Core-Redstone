package io.github.anvilloystudio.minimods.mod.core.redstone.tiles;

import minicraft.entity.Direction;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

/**
 * This is a base class to mark that the class is a kind of redstone tile.
 * Note: This should not be implemented directly; unexpected problems might occur.
 * All subclasses should implement the extended interfaces of this.
 */
@SuppressWarnings("unused")
public interface RedstoneTileNode {
	/**
	 * Getting if the tile is connectable to redstone dust at the specified direction in rendering.
	 * @param level The level of the tile being on.
	 * @param x The x-coordinate of the tile.
	 * @param y The y-coordinate of the tile.
	 * @param dir The direction to connect. The direction is based on this tile.
	 * @return Whether the tile is connectable to redstone dust at the specified direction.
	 */
	boolean isConnectableToDust(Level level, int x, int y, Direction dir);

	interface RedstoneTransmitter<T extends Tile & RedstoneTransmitter<T>> extends RedstoneTileNode {

		/**
		 * Getting all transmittable directions the tile could, depending on the tile state.
		 * @param level The level of the tile being on.
		 * @param x The x-coordinate of the tile.
		 * @param y The y-coordinate of the tile.
		 * @return The set of directions.
		 */
		@NotNull
		EnumSet<Direction> getTransmittableDirections(Level level, int x, int y);

		/**
		 * Getting the transmitting power to the specified direction on the specified tile.
		 *
		 * @param level The level of the tile being on.
		 * @param x The x-coordinate of the tile.
		 * @param y The y-coordinate of the tile.
		 * @param dir The direction to transmit.
		 * @param target The target to transmit power.
		 * @return The redstone power. The value should be in between 0 and 15. 0 if the direction is not transmittable.
		 */
		int getTransmittingPower(Level level, int x, int y, Direction dir, RedstoneTileNode target);

		/**
		 * Getting the transmitting power strength to the specified direction on the specified tile.
		 * @param level The level of the tile being on.
		 * @param x The x-coordinate of the tile.
		 * @param y The y-coordinate of the tile.
		 * @param dir The direction to transmit.
		 * @return The redstone power. The value should be in between 0 and 15. 0 if the direction is not transmittable.
		 */
		boolean getTransmittingStrength(Level level, int x, int y, Direction dir);
	}

	interface RedstoneReceiver<T extends Tile & RedstoneReceiver<T>> extends RedstoneTileNode {
		/**
		 * Getting all receivable directions the tile could, depending on the tile state.
		 * @param level The level of the tile being on.
		 * @param x The x-coordinate of the tile.
		 * @param y The y-coordinate of the tile.
		 * @return The set of directions.
		 */
		@NotNull
		EnumSet<Direction> getReceivableDirections(Level level, int x, int y);

		/**
		 * Handling the receiving redstone power from the direction of the tile.
		 * Note that the direction and coordinates should be the tile itself receiving the power.
		 * @param level The level of the tile being on.
		 * @param x The x-coordinate of the tile.
		 * @param y The y-coordinate of the tile.
		 * @param dir The direction to receive from.
		 * @param power The redstone power. The value should be in between 0 and 15.
		 * @param strong The strength of power, `true` if strong; weak otherwise.
		 * @param source The source tile of the redstone power.
		 * @return `true` if the there is any data changed on the level; `false` if not.
		 */
		boolean receivePower(Level level, int x, int y, Direction dir, int power, boolean strong, RedstoneTileNode source);
	}
}
