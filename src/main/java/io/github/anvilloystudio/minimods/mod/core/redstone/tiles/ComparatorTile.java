package io.github.anvilloystudio.minimods.mod.core.redstone.tiles;

import io.github.anvilloystudio.minimods.api.GraphicComp;
import io.github.anvilloystudio.minimods.api.ModProcedure;
import io.github.anvilloystudio.minimods.api.Vector2;
import io.github.anvilloystudio.minimods.api.interfaces.Tickable;
import io.github.anvilloystudio.minimods.mod.core.redstone.items.RedstoneWrenchItem;
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

public class ComparatorTile extends Tile implements RedstoneTransmitter<ComparatorTile>, RedstoneReceiver<ComparatorTile> {
	private static final Sprite[] spriteOff;
	private static final Sprite[] spriteOn;
	private static final Sprite spriteTorch;
	private static final Sprite spriteTorchOff;
	private static final Sprite spriteTorchHead;
	private static final Sprite spriteTorchHeadOff;

	static {
		try {
			BufferedImage image = ImageIO.read(Objects.requireNonNull(RepeaterTile.class.getResourceAsStream("/assets/textures/tiles/comparator.png")));
			spriteOff = new Sprite[4];
			spriteOff[0] = GraphicComp.getSpriteFromSheet(2, 2, new SpriteSheet(image));
			spriteOff[1] = GraphicComp.getSpriteFromSheet(2, 2, new SpriteSheet(GraphicComp.rotate180(image)));
			spriteOff[2] = GraphicComp.getSpriteFromSheet(2, 2, new SpriteSheet(GraphicComp.rotateClockwise90(image)));
			spriteOff[3] = GraphicComp.getSpriteFromSheet(2, 2, new SpriteSheet(GraphicComp.rotateAnticlockwise90(image)));

			image = ImageIO.read(Objects.requireNonNull(RepeaterTile.class.getResourceAsStream("/assets/textures/tiles/comparator_on.png")));
			spriteOn = new Sprite[4];
			spriteOn[0] = GraphicComp.getSpriteFromSheet(2, 2, new SpriteSheet(image));
			spriteOn[1] = GraphicComp.getSpriteFromSheet(2, 2, new SpriteSheet(GraphicComp.rotate180(image)));
			spriteOn[2] = GraphicComp.getSpriteFromSheet(2, 2, new SpriteSheet(GraphicComp.rotateClockwise90(image)));
			spriteOn[3] = GraphicComp.getSpriteFromSheet(2, 2, new SpriteSheet(GraphicComp.rotateAnticlockwise90(image)));

			spriteTorch = GraphicComp.getSpriteFromSheet(2, 2,
				GraphicComp.getSpriteSheetFromInputStream(RepeaterTile.class.getResourceAsStream("/assets/textures/tiles/comparator_torch.png")));
			spriteTorchOff = GraphicComp.getSpriteFromSheet(2, 2,
				GraphicComp.getSpriteSheetFromInputStream(RepeaterTile.class.getResourceAsStream("/assets/textures/tiles/comparator_torch_off.png")));
			spriteTorchHead = GraphicComp.getSpriteFromSheet(2, 2,
				GraphicComp.getSpriteSheetFromInputStream(RepeaterTile.class.getResourceAsStream("/assets/textures/tiles/comparator_torch_head.png")));
			spriteTorchHeadOff = GraphicComp.getSpriteFromSheet(2, 2,
				GraphicComp.getSpriteSheetFromInputStream(RepeaterTile.class.getResourceAsStream("/assets/textures/tiles/comparator_torch_head_off.png")));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		ModProcedure.tickables0.add(new Tickable() {
			@Override
			public void tick(InputHandler input) {
				// Calculating transmitting signals.
				transmittingTiles.clear();
				for (Integer key : receivingTiles) {
					int lvlIdx = key / (World.lvlw * World.lvlh);
					int pos = key % (World.lvlw * World.lvlh);
					int x = pos % World.lvlw;
					int y = pos / World.lvlw;
					int data = World.levels[lvlIdx].getData(x, y);
					int rear = receivingBack.get(key);
					int transmittingVal = 0;
					if (receivingSides.containsKey(key)) {
						Vector2<Integer> sides = receivingSides.get(key);
						if (((data >> 1) & 1) == 0) { // Mode 0: comparing
							transmittingVal = (sides.x <= rear && sides.y <= rear) ? rear : 0;
						} else { // Mode 1: subtracting
							transmittingVal = Math.max(rear - Math.max(sides.x, sides.y), 0);
						}
					} else {
						transmittingVal = rear;
					}

					if (transmittingVal > 0)
						World.levels[lvlIdx].setData(x, y, data | 1);
					else
						World.levels[lvlIdx].setData(x, y, data & 0b1110);
					transmittingTiles.put(key, transmittingVal);
				}

				// Copied from Redstone Torch.
				for (Integer key : existingTiles) {
					if (!receivingTiles.contains(key)) {
						int lvlIdx = key / (World.lvlw * World.lvlh);
						int pos = key % (World.lvlw * World.lvlh);
						int x = pos % World.lvlw;
						int y = pos / World.lvlw;
						int data = World.levels[lvlIdx].getData(x, y);
						World.levels[lvlIdx].setData(x, y, data & 0b1110);
					}
				}

				existingTiles.clear();
				receivingTiles.clear();
				receivingSides.clear();
				receivingBack.clear();
			}
		});
	}

	public ComparatorTile() {
		super("Comparator", (Sprite) null);
	}

	@Override
	public boolean isConnectableToDust(Level level, int x, int y, Direction dir) {
		addExistingTile(level, x, y);
		return true;
	}

	// Date bits: Direction {2}, Mode {1}, Powered {1}

	@Override
	public @NotNull EnumSet<Direction> getTransmittableDirections(Level level, int x, int y) {
		addExistingTile(level, x, y);
		Direction dir = Direction.getDirection((level.getData(x, y) >> 2) & 3);
		return EnumSet.of(dir);
	}

	@Override
	public int getTransmittingPower(Level level, int x, int y, Direction dir) {
		addExistingTile(level, x, y);
		int pos = x + y * level.w + World.lvlIdx(level.depth) * level.w * level.h;
		return transmittingTiles.getOrDefault(pos, 0);
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
		Direction dir = Direction.getDirection((level.getData(x, y) >> 2) & 3);
		set.remove(dir);
		return set;
	}

	@Override
	public boolean receivePower(Level level, int x, int y, Direction dir, int power, boolean strong, RedstoneNodeTile source) {
		Direction transmittingDir = Direction.getDirection((level.getData(x, y) >> 2) & 3);
		addExistingTile(level, x, y);
		int pos = x + y * level.w + World.lvlIdx(level.depth) * level.w * level.h;
		if (power > 0) {
			if (transmittingDir == Direction.DOWN && dir == Direction.UP || // Opposite direction (rear) of transmitting.
				transmittingDir == Direction.UP && dir == Direction.DOWN ||
				transmittingDir == Direction.LEFT && dir == Direction.RIGHT ||
				transmittingDir == Direction.RIGHT && dir == Direction.LEFT) {

				if (!receivingTiles.contains(pos)) {
					receivingTiles.add(pos);
					receivingBack.put(pos, power);
				}
			} else { // Other directions (besides) of receiving.
				if (transmittingDir == Direction.DOWN && dir == Direction.RIGHT || // The left side of transmitting.
					transmittingDir == Direction.UP && dir == Direction.LEFT ||
					transmittingDir == Direction.LEFT && dir == Direction.DOWN ||
					transmittingDir == Direction.RIGHT && dir == Direction.UP) {

					if (!receivingSides.containsKey(pos)) {
						receivingSides.put(pos, new Vector2<>(power, 0));
					} else {
						receivingSides.get(pos).x = power;
					}
				} else { // The right side of transmitting.
					if (!receivingSides.containsKey(pos)) {
						receivingSides.put(pos, new Vector2<>(0, power));
					} else {
						receivingSides.get(pos).y = power;
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

	private static final CopyOnWriteArrayList<Integer> existingTiles = new CopyOnWriteArrayList<>();
	private static final CopyOnWriteArrayList<Integer> receivingTiles = new CopyOnWriteArrayList<>();
	private static final ConcurrentHashMap<Integer, Vector2<Integer>> receivingSides = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<Integer, Integer> receivingBack = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<Integer, Integer> transmittingTiles = new ConcurrentHashMap<>();

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
		} else if (item instanceof RedstoneWrenchItem) { // Rotating the tile.
			int data = level.getData(xt, yt);
			int dir = (data >> 2) & 3;
			level.setData(xt, yt, (((dir + 1) & 3) << 2) + (data & 0b11));
			return true;
		}

		Sound.monsterHurt.play();
		int data = level.getData(xt, yt); // Changing the mode.
		level.setData(xt, yt, data ^ 0b10);
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
		int dir = (data >> 2) & 3;
		boolean powered = (data & 1) == 1;
		int mode = (data >> 1) & 1;
		(powered ? spriteOn : spriteOff)[dir].render(screen, x << 4, y << 4);
		Sprite torchSprite = powered ? spriteTorch : spriteTorchOff;
		Sprite torchHeadSprite = mode == 0 ? spriteTorchHeadOff : spriteTorchHead;
		if (dir == 0) {
			torchHeadSprite.render(screen, x << 4, (y << 4) + 5);
			torchSprite.render(screen, (x << 4) + 4, (y << 4) - 5);
			torchSprite.render(screen, (x << 4) - 4, (y << 4) - 5);
		} else if (dir == 1) {
			torchHeadSprite.render(screen, x << 4, (y << 4) - 5);
			torchSprite.render(screen, (x << 4) + 4, (y << 4) + 5);
			torchSprite.render(screen, (x << 4) - 4, (y << 4) + 5);
		} else if (dir == 2) {
			torchHeadSprite.render(screen, (x << 4) - 5, y << 4);
			torchSprite.render(screen, (x << 4) + 5, (y << 4) + 4);
			torchSprite.render(screen, (x << 4) + 5, (y << 4) - 4);
		} else { // dir == 3
			torchHeadSprite.render(screen, (x << 4) + 5, y << 4);
			torchSprite.render(screen, (x << 4) - 5, (y << 4) + 4);
			torchSprite.render(screen, (x << 4) - 5, (y << 4) - 4);
		}
	}
}
