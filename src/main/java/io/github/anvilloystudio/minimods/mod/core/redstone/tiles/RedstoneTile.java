package io.github.anvilloystudio.minimods.mod.core.redstone.tiles;

import minicraft.entity.Direction;
import minicraft.gfx.Sprite;
import minicraft.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class RedstoneTile extends RedstoneNodeTile {
	protected RedstoneTile(String name, Sprite sprite) {
		super(name, sprite);
	}

	@Override
	public @NotNull EnumSet<Direction> getConnectableDirections() {
		return EnumSet.of(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT);
	}

	@Override
	public @NotNull EnumSet<Direction> getConnectedDirections(Level level, int x, int y) {
		EnumSet<Direction> set = EnumSet.noneOf(Direction.class);
		if (level.getTile(x, y + 1) instanceof RedstoneNodeTile) set.add(Direction.UP);
		if (level.getTile(x, y - 1) instanceof RedstoneNodeTile) set.add(Direction.DOWN);
		if (level.getTile(x - 1, y) instanceof RedstoneNodeTile) set.add(Direction.LEFT);
		if (level.getTile(x + 1, y) instanceof RedstoneNodeTile) set.add(Direction.RIGHT);
		return set;
	}
}
