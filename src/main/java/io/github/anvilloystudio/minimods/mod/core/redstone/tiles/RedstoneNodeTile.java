package io.github.anvilloystudio.minimods.mod.core.redstone.tiles;

import minicraft.entity.Direction;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.function.BiConsumer;
import java.util.function.IntUnaryOperator;

/**
 * This is a base class to mark that the class is a kind of redstone tile.
 * @deprecated This should not be implemented directly; unexpected problems might occur.
 * All subclasses should implement the extended interfaces of this.
 */
@Deprecated
@SuppressWarnings({"deprecated", "unused"})
public interface RedstoneNodeTile {
	interface RedstoneTransmitter<T extends Tile & RedstoneTransmitter<T>> extends RedstoneNodeTile {

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
		 * @param level The level of the tile being on.
		 * @param x The x-coordinate of the tile.
		 * @param y The y-coordinate of the tile.
		 * @param dir The direction to transmit.
		 * @param powerQueryResponder The operation to give the current powers by the given coordinates. Available only in same circuit.
		 * @param powerSetter The setter to set the power (right arg) by the specified coordinates (left arg). Available only in same circuit.
		 * @return The redstone power. The value should be in between 0 and 15. 0 if the direction is not transmittable.
		 */
		int getTransmittingPower(Level level, int x, int y, Direction dir, IntUnaryOperator powerQueryResponder, BiConsumer<Integer, Integer> powerSetter);
	}

	interface RedstoneReceiver<T extends Tile & RedstoneReceiver<T>> extends RedstoneNodeTile {
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
		 * @param powerQueryResponder The operation to give the current powers by the given coordinates. Available only in same circuit.
		 * @param powerSetter The setter to set the power (right arg) by the specified coordinates (left arg). Available only in same circuit.
		 * @return `true` if the power has been received successfully. `false` if the direction is not receivable.
		 */
		boolean receivePower(Level level, int x, int y, Direction dir, int power, IntUnaryOperator powerQueryResponder, BiConsumer<Integer, Integer> powerSetter);
	}
}
