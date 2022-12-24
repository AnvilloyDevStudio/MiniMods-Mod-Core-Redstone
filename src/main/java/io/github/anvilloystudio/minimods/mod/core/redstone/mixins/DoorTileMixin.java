package io.github.anvilloystudio.minimods.mod.core.redstone.mixins;

import io.github.anvilloystudio.minimods.api.ModProcedure;
import io.github.anvilloystudio.minimods.api.interfaces.Tickable;
import io.github.anvilloystudio.minimods.mod.core.redstone.tiles.RedstoneNodeTile;
import io.github.anvilloystudio.minimods.mod.core.redstone.tiles.RedstoneNodeTile.RedstoneReceiver;
import minicraft.core.World;
import minicraft.core.io.InputHandler;
import minicraft.entity.Direction;
import minicraft.gfx.Sprite;
import minicraft.level.Level;
import minicraft.level.tile.DoorTile;
import minicraft.level.tile.Tile;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.util.EnumSet;
import java.util.concurrent.CopyOnWriteArrayList;

@Mixin(DoorTile.class)
public class DoorTileMixin extends Tile implements RedstoneReceiver<DoorTileMixin> {
	static {
		ModProcedure.tickables0.add(new Tickable() {
			@Override
			public void tick(InputHandler input) {
				for (Integer key : existingTiles) { // Emitting not existed tiles.
					if (!receivedTiles.contains(key) && receivingTiles.contains(key)) { // Newly activated.
						int lvlIdx = key / (World.lvlw * World.lvlh);
						int pos = key % (World.lvlw * World.lvlh);
						int x = pos % World.lvlw;
						int y = pos / World.lvlw;
						World.levels[lvlIdx].setData(x, y, 1);
					} else if (receivedTiles.contains(key) && !receivingTiles.contains(key)) { // Newly unactivated.
						int lvlIdx = key / (World.lvlw * World.lvlh);
						int pos = key % (World.lvlw * World.lvlh);
						int x = pos % World.lvlw;
						int y = pos / World.lvlw;
						World.levels[lvlIdx].setData(x, y, 0);
					}
				}

				existingTiles.clear();
				receivedTiles.clear();
				receivedTiles.addAll(receivingTiles);
				receivingTiles.clear();
			}
		});
	}

	protected DoorTileMixin(String name, Sprite sprite) {
		super(name, sprite);
	}

	@Override
	public boolean isConnectableToDust(Level level, int x, int y, Direction dir) {
		return true;
	}

	@Override
	public @NotNull EnumSet<Direction> getReceivableDirections(Level level, int x, int y) {
		return EnumSet.of(Direction.DOWN, Direction.UP, Direction.LEFT, Direction.RIGHT);
	}

	@Override
	public boolean receivePower(Level level, int x, int y, Direction dir, int power, boolean strong, RedstoneNodeTile source) {
		int pos = x + y * level.w + World.lvlIdx(level.depth) * level.w * level.h;
		if (!existingTiles.contains(pos))
			existingTiles.add(pos);
		if (power > 0) {
			if (!receivingTiles.contains(pos))
				receivingTiles.add(pos);
		}
		return false;
	}

	private static final CopyOnWriteArrayList<Integer> existingTiles = new CopyOnWriteArrayList<>();
	private static final CopyOnWriteArrayList<Integer> receivedTiles = new CopyOnWriteArrayList<>();
	private static final CopyOnWriteArrayList<Integer> receivingTiles = new CopyOnWriteArrayList<>();
}
