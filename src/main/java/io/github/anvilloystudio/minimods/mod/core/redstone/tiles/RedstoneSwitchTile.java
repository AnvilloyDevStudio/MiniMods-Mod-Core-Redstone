package io.github.anvilloystudio.minimods.mod.core.redstone.tiles;

import io.github.anvilloystudio.minimods.api.GraphicComp;
import io.github.anvilloystudio.minimods.mod.core.redstone.tiles.RedstoneNodeTile.RedstoneTransmitter;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.mob.Mob;
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

public class RedstoneSwitchTile extends Tile implements RedstoneTransmitter<RedstoneSwitchTile> {
	private static final Sprite spriteOff;
	private static final Sprite spriteOn;

	static {
		try {
			spriteOff = GraphicComp.getSpriteFromSheet(2, 2,
				GraphicComp.getSpriteSheetFromInputStream(RedstoneSwitchTile.class.getResourceAsStream("/assets/textures/tiles/redstone_switch.png")));
			spriteOn = GraphicComp.getSpriteFromSheet(2, 2,
				GraphicComp.getSpriteSheetFromInputStream(RedstoneSwitchTile.class.getResourceAsStream("/assets/textures/tiles/redstone_switch_on.png")));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public RedstoneSwitchTile() {
		super("Redstone Switch", spriteOff);
	}

	@Override
	public @NotNull EnumSet<Direction> getTransmittableDirections(Level level, int x, int y) {
		return EnumSet.of(Direction.DOWN, Direction.UP, Direction.LEFT, Direction.RIGHT);
	}

	@Override
	public int getTransmittingPower(Level level, int x, int y, Direction dir, RedstoneNodeTile target) {
		return (level.getData(x, y) & 1) == 1 ? 15 : 0;
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
					level.dropItem(xt * 16 + 8, yt * 16 + 8, Items.get("Redstone Switch"));
					return true;
				}
			}
		}

		Sound.monsterHurt.play();
		level.setData(xt, yt, level.getData(xt, yt) ^ 1);
		return true;
	}

	@Override
	public boolean hurt(Level level, int x, int y, Mob source, int dmg, Direction attackDir) {
		if (source instanceof Player) {
			Sound.monsterHurt.play();
			level.setData(x, y, level.getData(x, y) ^ 1);
			return true;
		}

		return super.hurt(level, x, y, source, dmg, attackDir);
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		minicraft.level.tile.Tiles.get(30).render(screen, level, x, y);
		if ((level.getData(x, y) & 1) == 0)
			sprite.render(screen, x << 4, y << 4);
		else
			spriteOn.render(screen, x << 4, y << 4);
	}

	@Override
	public boolean isConnectableToDust(Level level, int x, int y, Direction dir) {
		return true;
	}
}
