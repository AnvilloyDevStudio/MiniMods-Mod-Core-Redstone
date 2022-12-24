package io.github.anvilloystudio.minimods.mod.core.redstone.tiles;

import io.github.anvilloystudio.minimods.api.GraphicComp;
import io.github.anvilloystudio.minimods.api.ModProcedure;
import io.github.anvilloystudio.minimods.api.interfaces.Tickable;
import io.github.anvilloystudio.minimods.mod.core.redstone.tiles.RedstoneNodeTile.RedstoneTransmitter;
import minicraft.core.World;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PressurePlateTile extends Tile implements RedstoneTransmitter<PressurePlateTile> {
	private static final Sprite spriteOff;
	private static final Sprite spriteOn;

	static {
		try {
			spriteOff = GraphicComp.getSpriteFromSheet(2, 2,
				GraphicComp.getSpriteSheetFromInputStream(PressurePlateTile.class.getResourceAsStream("/assets/textures/tiles/pressure_plate.png")));
			spriteOn = GraphicComp.getSpriteFromSheet(2, 2,
				GraphicComp.getSpriteSheetFromInputStream(PressurePlateTile.class.getResourceAsStream("/assets/textures/tiles/pressure_plate_on.png")));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		ModProcedure.tickables0.add(new Tickable() {
			@Override
			public void tick(InputHandler input) {
				for (Integer key : pressedTiles) {
					int lvlIdx = key / (World.lvlw * World.lvlh);
					int pos = key % (World.lvlw * World.lvlh);
					int x = pos % World.lvlw;
					int y = pos / World.lvlw;
					List<Entity> entities = World.levels[lvlIdx].getEntitiesInTiles(x, y, 0);
					if (entities.isEmpty()) {
						pressedTiles.remove(key);
						World.levels[lvlIdx].setData(x, y, 0);
						Sound.monsterHurt.play();
					}
				}
			}
		});
	}

	public PressurePlateTile() {
		super("Pressure Plate", spriteOff);
	}

	@Override
	public @NotNull EnumSet<Direction> getTransmittableDirections(Level level, int x, int y) {
		return EnumSet.of(Direction.DOWN, Direction.UP, Direction.LEFT, Direction.RIGHT);
	}

	@Override
	public int getTransmittingPower(Level level, int x, int y, Direction dir, RedstoneNodeTile target) {
		return level.getData(x, y) == 1 ? 15 : 0;
	}

	@Override
	public boolean getTransmittingStrength(Level level, int x, int y, Direction dir) {
		return true;
	}

	@Override
	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Pickaxe) {
				if (player.payStamina(4 - ToolItem.LEVELS.get(tool.level)) && tool.payDurability()) {
					level.setTile(xt, yt, Tiles.get("Stone Bricks"));
					Sound.monsterHurt.play();
					level.dropItem(xt * 16 + 8, yt * 16 + 8, Items.get("Pressure Plate"));
					return true;
				}
			}
		}

		return false;
	}

	private static final CopyOnWriteArrayList<Integer> pressedTiles = new CopyOnWriteArrayList<>();

	@Override
	public void steppedOn(Level level, int xt, int yt, Entity entity) {
		int pos = xt + yt * level.w + World.lvlIdx(level.depth) * level.w * level.h;
		if (!pressedTiles.contains(pos)) {
			Sound.monsterHurt.play();
			pressedTiles.add(pos);
		}

		level.setData(xt, yt, 1);
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		minicraft.level.tile.Tiles.get(30).render(screen, level, x, y);
		if (level.getData(x, y) == 0)
			sprite.render(screen, x << 4, y << 4);
		else
			spriteOn.render(screen, x << 4, y << 4);
	}

	@Override
	public boolean isConnectableToDust(Level level, int x, int y, Direction dir) {
		return true;
	}
}
