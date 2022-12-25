package io.github.anvilloystudio.minimods.mod.core.redstone.tiles;

import io.github.anvilloystudio.minimods.api.GraphicComp;
import io.github.anvilloystudio.minimods.api.ModProcedure;
import io.github.anvilloystudio.minimods.api.interfaces.Tickable;
import io.github.anvilloystudio.minimods.mod.core.redstone.tiles.RedstoneNodeTile.RedstoneReceiver;
import io.github.anvilloystudio.minimods.mod.core.redstone.tiles.RedstoneNodeTile.RedstoneTransmitter;
import minicraft.core.World;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.gfx.SpriteSheet;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RepeaterTile extends Tile implements RedstoneTransmitter<RepeaterTile>, RedstoneReceiver<RepeaterTile> {
	private static final Sprite[] spriteOff;
	private static final Sprite[] spriteOn;
	private static final Sprite[] spriteLock;
	private static final Sprite spriteTorch;
	private static final Sprite spriteTorchOff;

	static {
		try {
			BufferedImage image = ImageIO.read(Objects.requireNonNull(RepeaterTile.class.getResourceAsStream("/assets/textures/tiles/repeater.png")));
			spriteOff = new Sprite[4];
			spriteOff[0] = GraphicComp.getSpriteFromSheet(2, 2, new SpriteSheet(image));
			spriteOff[1] = GraphicComp.getSpriteFromSheet(2, 2, new SpriteSheet(GraphicComp.rotate180(image)));
			spriteOff[2] = GraphicComp.getSpriteFromSheet(2, 2, new SpriteSheet(GraphicComp.rotateClockwise90(image)));
			spriteOff[3] = GraphicComp.getSpriteFromSheet(2, 2, new SpriteSheet(GraphicComp.rotateAnticlockwise90(image)));

			image = ImageIO.read(Objects.requireNonNull(RepeaterTile.class.getResourceAsStream("/assets/textures/tiles/repeater_on.png")));
			spriteOn = new Sprite[4];
			spriteOn[0] = GraphicComp.getSpriteFromSheet(2, 2, new SpriteSheet(image));
			spriteOn[1] = GraphicComp.getSpriteFromSheet(2, 2, new SpriteSheet(GraphicComp.rotate180(image)));
			spriteOn[2] = GraphicComp.getSpriteFromSheet(2, 2, new SpriteSheet(GraphicComp.rotateClockwise90(image)));
			spriteOn[3] = GraphicComp.getSpriteFromSheet(2, 2, new SpriteSheet(GraphicComp.rotateAnticlockwise90(image)));

			image = ImageIO.read(Objects.requireNonNull(RepeaterTile.class.getResourceAsStream("/assets/textures/tiles/repeater_lock.png")));
			spriteLock = new Sprite[4];
			spriteLock[0] = GraphicComp.getSpriteFromSheet(2, 2, new SpriteSheet(image));
			spriteLock[1] = GraphicComp.getSpriteFromSheet(2, 2, new SpriteSheet(GraphicComp.rotate180(image)));
			spriteLock[2] = GraphicComp.getSpriteFromSheet(2, 2, new SpriteSheet(GraphicComp.rotateClockwise90(image)));
			spriteLock[3] = GraphicComp.getSpriteFromSheet(2, 2, new SpriteSheet(GraphicComp.rotateAnticlockwise90(image)));

			spriteTorch = GraphicComp.getSpriteFromSheet(2, 2,
				GraphicComp.getSpriteSheetFromInputStream(RepeaterTile.class.getResourceAsStream("/assets/textures/tiles/repeater_torch.png")));
			spriteTorchOff = GraphicComp.getSpriteFromSheet(2, 2,
				GraphicComp.getSpriteSheetFromInputStream(RepeaterTile.class.getResourceAsStream("/assets/textures/tiles/repeater_torch_off.png")));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		ModProcedure.tickables0.add(new Tickable() {
			@Override
			public void tick(InputHandler input) {
				// Copied from Redstone Torch.
				for (Integer key : existingTiles) {
					if (!receivingTiles.contains(key)) {
						int lvlIdx = key / (World.lvlw * World.lvlh);
						int pos = key % (World.lvlw * World.lvlh);
						int x = pos % World.lvlw;
						int y = pos / World.lvlw;
						int data = World.levels[lvlIdx].getData(x, y);
						if (((data >> 1) & 1) == 0) { // Making unlocked tiles unpowered after the delays.
							if (!delays.containsKey(key)) {
								delays.put(key, 0);
							}
						}
					} if (!lockingTiles.contains(key)) {
						int lvlIdx = key / (World.lvlw * World.lvlh);
						int pos = key % (World.lvlw * World.lvlh);
						int x = pos % World.lvlw;
						int y = pos / World.lvlw;
						int data = World.levels[lvlIdx].getData(x, y);
						World.levels[lvlIdx].setData(x, y, data & 0b111101);
					}
				}

				existingTiles.clear();
				receivingTiles.clear();
				lockingTiles.clear();

				for (Integer key : delays.keySet()) {
					int lvlIdx = key / (World.lvlw * World.lvlh);
					int pos = key % (World.lvlw * World.lvlh);
					int x = pos % World.lvlw;
					int y = pos / World.lvlw;
					int data = World.levels[lvlIdx].getData(x, y);
					if (((data >> 1) & 1) == 1) // Omitting the delays if the tiles are locked.
						delays.remove(key);
					else {
						int delayData = delays.get(key) + (1 << 1); // Incrementing the delay.
						int delay = delayData >> 1;
						if (delay >= ((data >> 2) & 3)) { // If the delays have reached the set delays.
							if ((delayData & 1) == 0)
								World.levels[lvlIdx].setData(x, y, data & 0b111110);
							else
								World.levels[lvlIdx].setData(x, y, data | 1);
							delays.remove(key); // Unregistering the delays.
						} else
							delays.put(key, delayData);
					}
				}
			}
		});
	}

	public RepeaterTile() {
		super("Repeater", (Sprite) null);
	}

	@Override
	public @NotNull EnumSet<Direction> getTransmittableDirections(Level level, int x, int y) {
		addExistingTile(level, x, y);
		Direction dir = Direction.getDirection((level.getData(x, y) >> 4) & 3);
		return EnumSet.of(dir);
	}

	// Data bits (leftmost to rightmost): Direction {2}, Delay {2}, Locked {1}, Powered {1}

	@Override
	public int getTransmittingPower(Level level, int x, int y, Direction dir, RedstoneNodeTile target) {
		addExistingTile(level, x, y);
		return (level.getData(x, y) & 1) == 1 ? 15 : 0;
	}

	@Override
	public boolean getTransmittingStrength(Level level, int x, int y, Direction dir) {
		addExistingTile(level, x, y);
		return true;
	}

	@Override
	public @NotNull EnumSet<Direction> getReceivableDirections(Level level, int x, int y) {
		addExistingTile(level, x, y);
		EnumSet<Direction> set = EnumSet.of(Direction.DOWN, Direction.UP, Direction.LEFT, Direction.RIGHT);
		Direction dir = Direction.getDirection((level.getData(x, y) >> 4) & 3);
		set.remove(dir);
		return set;
	}

	@Override
	public boolean receivePower(Level level, int x, int y, Direction dir, int power, boolean strong, RedstoneNodeTile source) {
		Direction transmittingDir = Direction.getDirection((level.getData(x, y) >> 4) & 3);
		addExistingTile(level, x, y);
		int pos = x + y * level.w + World.lvlIdx(level.depth) * level.w * level.h;
		if (power > 0) {
			if (transmittingDir == Direction.DOWN && dir == Direction.UP || // Opposite direction (rear) of transmitting.
				transmittingDir == Direction.UP && dir == Direction.DOWN ||
				transmittingDir == Direction.LEFT && dir == Direction.RIGHT ||
				transmittingDir == Direction.RIGHT && dir == Direction.LEFT) {

				if (!receivingTiles.contains(pos)) {
					receivingTiles.add(pos);
					int data = level.getData(x, y);
					if (((data >> 1) & 1) == 0 && (data & 1) == 0) { // If the tiles are unlocked and unpowered.
						if (!delays.containsKey(pos)) {
							delays.put(pos, 1); // To make the tiles powered after the delays.
						}
					}
				}
			} else if (strong &&
				(source instanceof RepeaterTile || source instanceof ComparatorTile)) { // Other directions (besides) of receiving.

				if (!lockingTiles.contains(pos)) {
					int data = level.getData(x, y);
					if (((data >> 1) & 1) == 0) { // Locking tiles if unlocked.
						level.setData(x, y, data | 0b10);
						lockingTiles.add(pos);
						return true;
					}
				}
			}
		}

		return false;
	}

	private static void addExistingTile(Level level, int x, int y) {
		int pos = x + y * level.w + World.lvlIdx(level.depth) * level.w * level.h;
		if (!existingTiles.contains(pos)) {
			existingTiles.add(pos);
		}
	}

	// The rightmost bit is to make the tile powered or not. The left bits are used to record the delays.
	private static final ConcurrentHashMap<Integer, Integer> delays = new ConcurrentHashMap<>();
	private static final CopyOnWriteArrayList<Integer> existingTiles = new CopyOnWriteArrayList<>();
	private static final CopyOnWriteArrayList<Integer> receivingTiles = new CopyOnWriteArrayList<>();
	private static final CopyOnWriteArrayList<Integer> lockingTiles = new CopyOnWriteArrayList<>();

	@Override
	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Pickaxe) {
				if (player.payStamina(4 - ToolItem.LEVELS.get(tool.level)) && tool.payDurability()) {
					level.setTile(xt, yt, Tiles.get("Stone Bricks"));
					Sound.monsterHurt.play();
					level.dropItem(xt * 16 + 8, yt * 16 + 8, Items.get("Repeater"));
					return true;
				}
			}
		}

		Sound.monsterHurt.play();
		int data = level.getData(xt, yt);
		int delay = (data >> 2) & 3;
		level.setData(xt, yt, ((data >> 4) << 4) + (((delay + 1) & 3) << 2) + (data & 3));
		return true;
	}

	@Override
	public boolean hurt(Level level, int x, int y, Mob source, int dmg, Direction attackDir) {
		if (source instanceof Player)
			return interact(level, x, y, (Player) source, null, attackDir);
		return super.hurt(level, x, y, source, dmg, attackDir);
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		minicraft.level.tile.Tiles.get(30).render(screen, level, x, y);
		int data = level.getData(x, y);
		int dir = (data >> 4) & 3;
		boolean powered = (data & 1) == 1;
		int delay = (data >> 2) & 3;
		(powered ? spriteOn : spriteOff)[dir].render(screen, x << 4, y << 4);
		Sprite torchSprite = powered ? spriteTorch : spriteTorchOff;
		Sprite tweakerSprite = ((data >> 1) & 1) == 0 ? torchSprite : spriteLock[dir];
		if (dir == 0) {
			torchSprite.render(screen, x << 4, (y << 4) + 5);
			tweakerSprite.render(screen, x << 4, (y << 4) + 1 - delay * 2);
		} else if (dir == 1) {
			torchSprite.render(screen, x << 4, (y << 4) - 5);
			tweakerSprite.render(screen, x << 4, (y << 4) - 1 + delay * 2);
		} else if (dir == 2) {
			torchSprite.render(screen, (x << 4) - 5, y << 4);
			tweakerSprite.render(screen, (x << 4) - 1 + delay * 2, y << 4);
		} else { // dir == 3
			torchSprite.render(screen, (x << 4) + 5, y << 4);
			tweakerSprite.render(screen, (x << 4) + 1 - delay * 2, y << 4);
		}
	}

	@Override
	public boolean isConnectableToDust(Level level, int x, int y, Direction dir) {
		Direction transmittingDir = Direction.getDirection((level.getData(x, y) >> 4) & 3);
		return (transmittingDir == Direction.DOWN || transmittingDir == Direction.UP) && (dir == Direction.DOWN || dir == Direction.UP) ||
			(transmittingDir == Direction.LEFT || transmittingDir == Direction.RIGHT) && (dir == Direction.LEFT || dir == Direction.RIGHT);
	}
}
