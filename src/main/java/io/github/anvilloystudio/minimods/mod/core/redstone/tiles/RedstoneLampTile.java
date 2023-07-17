package io.github.anvilloystudio.minimods.mod.core.redstone.tiles;

import io.github.anvilloystudio.minimods.api.GraphicComp;
import io.github.anvilloystudio.minimods.api.ModProcedure;
import io.github.anvilloystudio.minimods.api.interfaces.Tickable;
import io.github.anvilloystudio.minimods.mod.core.redstone.tiles.RedstoneTileNode.RedstoneReceiver;
import minicraft.core.World;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.EnumSet;
import java.util.concurrent.CopyOnWriteArrayList;

public class RedstoneLampTile extends Tile implements RedstoneReceiver<RedstoneLampTile> {
	private static final Sprite spriteOff;
	private static final Sprite spriteOn;

	static {
		try {
			spriteOff = GraphicComp.getSpriteFromSheet(2, 2,
				GraphicComp.getSpriteSheetFromInputStream(RedstoneSwitchTile.class.getResourceAsStream("/assets/textures/tiles/redstone_lamp.png")));
			spriteOn = GraphicComp.getSpriteFromSheet(2, 2,
				GraphicComp.getSpriteSheetFromInputStream(RedstoneSwitchTile.class.getResourceAsStream("/assets/textures/tiles/redstone_lamp_on.png")));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		ModProcedure.tickables0.add(new Tickable() {
			@Override
			public void tick(InputHandler input) {
				for (Integer key : existingTiles) { // Emitting not existed tiles.
					int lvlIdx = key / (World.lvlw * World.lvlh);
					int pos = key % (World.lvlw * World.lvlh);
					int x = pos % World.lvlw;
					int y = pos / World.lvlw;
					int data = World.levels[lvlIdx].getData(x, y) & 1;
					if (receivingTiles.contains(key) && data == 0) { // Unpowered but activating.
						World.levels[lvlIdx].setData(x, y, 1);
					} else if (!receivingTiles.contains(key) && data == 1) { // Powered but inactivating.
						World.levels[lvlIdx].setData(x, y, 0);
					}
				}

				existingTiles.clear();
				receivingTiles.clear();
			}
		});
	}

	public RedstoneLampTile() {
		super("Redstone Lamp", spriteOff);
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
	public boolean receivePower(Level level, int x, int y, Direction dir, int power, boolean strong, RedstoneTileNode source) {
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
	private static final CopyOnWriteArrayList<Integer> receivingTiles = new CopyOnWriteArrayList<>();

	@Override
	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Pickaxe) {
				if (player.payStamina(4 - ToolItem.LEVELS.get(tool.level)) && tool.payDurability()) {
					level.setTile(xt, yt, Tiles.get("Stone Bricks"));
					Sound.monsterHurt.play();
					level.dropItem(xt * 16 + 8, yt * 16 + 8, Items.get("Redstone Lamp"));
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		if ((level.getData(x, y) & 1) == 0)
			sprite.render(screen, x << 4, y << 4);
		else
			spriteOn.render(screen, x << 4, y << 4);
	}
}
