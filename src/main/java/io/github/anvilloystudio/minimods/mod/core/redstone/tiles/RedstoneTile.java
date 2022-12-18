package io.github.anvilloystudio.minimods.mod.core.redstone.tiles;

import io.github.anvilloystudio.minimods.api.GraphicComp;
import io.github.anvilloystudio.minimods.mod.core.redstone.tiles.RedstoneNodeTile.RedstoneReceiver;
import io.github.anvilloystudio.minimods.mod.core.redstone.tiles.RedstoneNodeTile.RedstoneTransmitter;
import minicraft.core.World;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.gfx.SpriteSheet;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.EnumSet;
import java.util.function.BiConsumer;
import java.util.function.IntUnaryOperator;

@SuppressWarnings("deprecated")
public class RedstoneTile extends Tile implements RedstoneTransmitter<RedstoneTile>, RedstoneReceiver<RedstoneTile> {
	private static final Sprite spriteDot;
	private static final SpriteSheet spriteVerticalLine;
	private static final SpriteSheet spriteHorizontalLine;

	static {
		try {
			spriteDot = GraphicComp.getSpriteFromSheet(2, 2,
				GraphicComp.getSpriteSheetFromInputStream(io.github.anvilloystudio.minimods.mod.core.redstone.tiles.Tiles.class.getResourceAsStream("/assets/textures/tiles/redstone_dust_dot.png")));
			spriteVerticalLine = GraphicComp.getSpriteSheetFromInputStream(io.github.anvilloystudio.minimods.mod.core.redstone.tiles.Tiles.class.getResourceAsStream("/assets/textures/tiles/redstone_dust_line0.png"));
			spriteHorizontalLine = GraphicComp.getSpriteSheetFromInputStream(io.github.anvilloystudio.minimods.mod.core.redstone.tiles.Tiles.class.getResourceAsStream("/assets/textures/tiles/redstone_dust_line1.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public RedstoneTile() {
		super("Redstone", (Sprite) null);
	}

	@Override
	public @NotNull EnumSet<Direction> getTransmittableDirections(Level level, int x, int y) {
		return EnumSet.of(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT);
	}

	@Override
	public int getTransmittingPower(Level level, int x, int y, Direction dir, IntUnaryOperator powerQueryResponder, BiConsumer<Integer, Integer> powerSetter) {
		return level.getData(x, y) - 1;
	}

	@Override
	public @NotNull EnumSet<Direction> getReceivableDirections(Level level, int x, int y) {
		return EnumSet.of(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT);
	}

	@Override
	public boolean receivePower(Level level, int x, int y, Direction dir, int power, IntUnaryOperator powerQueryResponder, BiConsumer<Integer, Integer> powerSetter) {
		if (power > level.getData(x, y))
			powerSetter.accept(x + y * level.w + World.lvlIdx(level.depth) * level.w * level.h, power & 0xF);
		return true;
	}

	@Override
	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		Sound.monsterHurt.play();
		return hurt(level, xt, yt, player, 0, attackDir);
	}

	@Override
	public boolean hurt(Level level, int x, int y, Mob source, int dmg, Direction attackDir) {
		level.setTile(x, y, Tiles.get(30)); // Placing stone floor. The tile it can only be placed on.
		level.dropItem(x * 16 + 8, y * 16 + 8, Items.get("Redstone"));
		return true;
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		Tiles.get(30).render(screen, level, x, y);

		boolean up, down, left, right, straight;
		up = down = left = right = straight = false;
		if (level.getTile(x, y - 1) instanceof RedstoneNodeTile) up = true;
		if (level.getTile(x, y + 1) instanceof RedstoneNodeTile) down = true;
		if (level.getTile(x - 1, y) instanceof RedstoneNodeTile) left = true;
		if (level.getTile(x + 1, y) instanceof RedstoneNodeTile) right = true;

		// Rendering full directional look if not connected.
		if (!(up || down || left || right)) up = down = left = right = true;

		if ((up || down) && !(left || right)) // If only up or down, making it straight vertical.
			up = down = straight = true;
		if ((left || right) && !(up || down)) // If only left or right, making it straight horizontal.
			left = right = straight = true;

		int color = 0xFF - (15 - (level.getData(x, y) & 0xF)) * 10 << 16;

		// At least 2 directions are true.
		if (up) {
			screen.render(x << 4, y << 4, 0, 0, spriteVerticalLine, color, false);
			screen.render((x << 4) + 8, y << 4, 1, 0, spriteVerticalLine, color, false);
		} if (down) {
			screen.render(x << 4, (y << 4) + 8, 32, 0, spriteVerticalLine, color, false);
			screen.render((x << 4) + 8, (y << 4) + 8, 1 + 32, 0, spriteVerticalLine, color, false);
		} if (left) {
			screen.render(x << 4, y << 4, 0, 0, spriteHorizontalLine, color, false);
			screen.render(x << 4, (y << 4) + 8, 32, 0, spriteHorizontalLine, color, false);
		} if (right) {
			screen.render((x << 4) + 8, y << 4, 1, 0, spriteHorizontalLine, color, false);
			screen.render((x << 4) + 8, (y << 4) + 8, 1 + 32, 0, spriteHorizontalLine, color, false);
		}

		if (!straight)
			spriteDot.render(screen, x << 4, y << 4, 0, color);
	}
}
