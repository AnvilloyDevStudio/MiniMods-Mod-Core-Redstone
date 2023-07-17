package io.github.anvilloystudio.minimods.mod.core.redstone.tiles;

import io.github.anvilloystudio.minimods.api.GraphicComp;
import io.github.anvilloystudio.minimods.api.ModProcedure;
import io.github.anvilloystudio.minimods.api.interfaces.Tickable;
import io.github.anvilloystudio.minimods.mod.core.redstone.tiles.RedstoneTileNode.RedstoneReceiver;
import io.github.anvilloystudio.minimods.mod.core.redstone.tiles.RedstoneTileNode.RedstoneTransmitter;
import minicraft.core.World;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.EnumSet;
import java.util.concurrent.CopyOnWriteArrayList;

public class RedstoneTorchTile extends Tile implements RedstoneTransmitter<RedstoneTorchTile>, RedstoneReceiver<RedstoneTorchTile> {
	private static final Sprite spriteOff;
	private static final Sprite spriteOn;

	static {
		try {
			spriteOn = GraphicComp.getSpriteFromSheet(2, 2,
				GraphicComp.getSpriteSheetFromInputStream(RedstoneTorchTile.class.getResourceAsStream("/assets/textures/tiles/redstone_torch.png")));
			spriteOff = GraphicComp.getSpriteFromSheet(2, 2,
				GraphicComp.getSpriteSheetFromInputStream(RedstoneTorchTile.class.getResourceAsStream("/assets/textures/tiles/redstone_torch_off.png")));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		ModProcedure.tickables0.add(new Tickable() {
			@Override
			public void tick(InputHandler input) {
				for (Integer key : receivedTiles) {
					if (!receivingTiles.contains(key)) {
						int lvlIdx = key / (World.lvlw * World.lvlh);
						int pos = key % (World.lvlw * World.lvlh);
						int x = pos % World.lvlw;
						int y = pos / World.lvlw;
						World.levels[lvlIdx].setData(x, y, 0);
					}
				}

				receivedTiles.clear();
				receivedTiles.addAll(receivingTiles);
				receivingTiles.clear();
			}
		});
	}

	public RedstoneTorchTile() {
		super("Redstone Torch", spriteOn);
	}

	@Override
	public @NotNull EnumSet<Direction> getTransmittableDirections(Level level, int x, int y) {
		return EnumSet.of(Direction.DOWN, Direction.UP, Direction.LEFT, Direction.RIGHT);
	}

	@Override
	public int getTransmittingPower(Level level, int x, int y, Direction dir, RedstoneTileNode target) {
		return level.getData(x, y) == 0 ? 15 : 0;
	}

	@Override
	public boolean getTransmittingStrength(Level level, int x, int y, Direction dir) {
		return true;
	}

	@Override
	public @NotNull EnumSet<Direction> getReceivableDirections(Level level, int x, int y) {
		return EnumSet.of(Direction.DOWN, Direction.UP, Direction.LEFT, Direction.RIGHT);
	}

	@Override
	public boolean receivePower(Level level, int x, int y, Direction dir, int power, boolean strong, RedstoneTileNode source) {
		if (power > 0 && strong) {
			int pos = x + y * level.w + World.lvlIdx(level.depth) * level.w * level.h;
			if (!receivingTiles.contains(pos)) {
				receivingTiles.add(pos);
				level.setData(x, y, 1);
				return true;
			}
		}

		return false;
	}

	private static final CopyOnWriteArrayList<Integer> receivedTiles = new CopyOnWriteArrayList<>();
	private static final CopyOnWriteArrayList<Integer> receivingTiles = new CopyOnWriteArrayList<>();

	@Override
	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		Sound.monsterHurt.play();
		level.setTile(xt, yt, Tiles.get(30)); // Placing stone floor. The tile it can only be placed on.
		level.dropItem(xt * 16 + 8, yt * 16 + 8, Items.get("Redstone Torch"));
		return true;
	}

	@Override
	public boolean hurt(Level level, int x, int y, Mob source, int dmg, Direction attackDir) {
		if (source instanceof Player)
			return interact(level, x, y, (Player) source, null, attackDir);
		return false;
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		minicraft.level.tile.Tiles.get(30).render(screen, level, x, y);
		if (level.getData(x, y) == 0)
			sprite.render(screen, x << 4, y << 4);
		else
			spriteOff.render(screen, x << 4, y << 4);
	}

	@Override
	public boolean isConnectableToDust(Level level, int x, int y, Direction dir) {
		return true;
	}
}
