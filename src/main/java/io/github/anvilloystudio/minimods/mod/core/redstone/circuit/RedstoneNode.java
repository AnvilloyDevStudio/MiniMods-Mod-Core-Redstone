package io.github.anvilloystudio.minimods.mod.core.redstone.circuit;

import com.google.common.collect.Sets;
import io.github.anvilloystudio.minimods.mod.core.redstone.tiles.RedstoneNodeTile;
import io.github.anvilloystudio.minimods.mod.core.redstone.tiles.RedstoneNodeTile.RedstoneReceiver;
import io.github.anvilloystudio.minimods.mod.core.redstone.tiles.RedstoneNodeTile.RedstoneTransmitter;
import io.github.anvilloystudio.minimods.mod.core.redstone.tiles.RedstoneTile;
import minicraft.core.World;
import minicraft.entity.Direction;
import minicraft.level.Level;
import minicraft.level.tile.Tile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

@SuppressWarnings("deprecated")
public class RedstoneNode {
	private static final HashSet<RedstoneNode> nodeList = new HashSet<>();
	// The key is the position is correspondingly related to the level position of the node.
	// Key = x-coordinate + y-coordinate * width + level index * width * height
	private static final HashMap<Integer, RedstoneNode> nodeMap = new HashMap<>();

	/**
	 * This is used in {@link minicraft.core.Updater#tick() Updater.tick()} for refreshing the list of existing nodes.
	 * This should be called before ticking tiles.
	 */
	public static void refreshNodes() {
		for (Level lvl : World.levels) {
			for (int x = 0; x < lvl.w; x++) {
				for (int y = 0; y < lvl.h; y++) {
					Tile tile = lvl.getTile(x, y);
					int lvlIdx = World.lvlIdx(lvl.depth);
					int pos = x + y * lvl.w + lvlIdx * lvl.w * lvl.h;
					if (tile instanceof RedstoneNodeTile) {
						if (!nodeMap.containsKey(pos)) {
							RedstoneNode node = new RedstoneNode(x, y, lvlIdx, (RedstoneNodeTile) tile);
							nodeList.add(node);
							nodeMap.put(pos, node);
						}
					} else {
						if (nodeMap.containsKey(pos)) {
							nodeList.remove(nodeMap.remove(pos));
						}
					}
				}
			}
		}
	}

	/**
	 * This is used in {@link minicraft.core.Updater#tick() Updater.tick()} for refreshing the all existing redstone circuits.
	 * This should be called right after {@link #refreshNodes()}.
	 */
	public static void refreshCircuits() {
		HashSet<RedstoneNode> processed = new HashSet<>();
		HashSet<HashSet<RedstoneNode>> circuits = new HashSet<>();
		for (RedstoneNode node : nodeList) {
			RedstoneNode[] nearNodes = processed.stream().filter(n -> isNear(n, node)).toArray(RedstoneNode[]::new);
			if (nearNodes.length > 0) { // Connecting to other existed circuit(s).
				HashSet<HashSet<RedstoneNode>> nearCircuits = new HashSet<>();
				for (RedstoneNode nearNode : nearNodes) { // The set should not be empty.
					nearCircuits.addAll(circuits.stream().filter(s -> s.contains(nearNode)).collect(Collectors.toSet()));
				}

				if (nearCircuits.size() == 1) {
					nearCircuits.stream().findAny().orElse(null).add(node); // Should not be null.
				} else { // Combining circuits.
					HashSet<RedstoneNode> set = Sets.newHashSet(node);
					nearCircuits.forEach(s -> { set.addAll(s); circuits.remove(s); });
					circuits.add(set);
				}
			} else
				circuits.add(Sets.newHashSet(node));
			processed.add(node);
			if (node.tile instanceof RedstoneTile) { // Reset all redstone dust powers.
				World.levels[node.lvlIdx].setData(node.x, node.y, 0);
			}
		}

		for (HashSet<RedstoneNode> circuit : circuits) {
			while (true) {
				boolean changed = false;
				for (RedstoneNode node : circuit) {
					if (node.tile instanceof RedstoneTransmitter) {
						Level level = World.levels[node.lvlIdx];
						for (Direction dir : ((RedstoneTransmitter<?>) node.tile).getTransmittableDirections(level, node.x, node.y)) {
							int targetX = node.x + dir.getX();
							int targetY = node.y + dir.getY();
							Tile target = level.getTile(targetX, targetY);
							if (target instanceof RedstoneReceiver) {
								Direction targetDir = null;
								if (dir == Direction.DOWN) targetDir = Direction.UP;
								else if (dir == Direction.UP) targetDir = Direction.DOWN;
								else if (dir == Direction.LEFT) targetDir = Direction.RIGHT;
								else if (dir == Direction.RIGHT) targetDir = Direction.LEFT;
								if (((RedstoneReceiver<?>) target).getReceivableDirections(level, targetX, targetY).contains(targetDir)) {
									int power = ((RedstoneTransmitter<?>) node.tile).getTransmittingPower(level, node.x, node.y, dir);
									boolean strong = ((RedstoneTransmitter<?>) node.tile).getTransmittingStrength(level, node.x, node.y, dir);
									if (((RedstoneReceiver<?>) target).receivePower(level, targetX, targetY, Direction.getDirection(-dir.getX(), -dir.getY()), power, strong, node.tile))
										changed = true;
								}
							}
						}
					}
				}

				if (!changed)
					break;
			}
		}
	}

	/**
	 * Checking if 2 nodes are near on the same floor.
	 * @param node1 The node to check.
	 * @param node2 The node to check.
	 * @return If the nodes are near.
	 */
	private static boolean isNear(RedstoneNode node1, RedstoneNode node2) {
		if (node1.lvlIdx == node2.lvlIdx) {
			int xDiff = Math.abs(node1.x - node2.x);
			int yDiff = Math.abs(node1.y - node2.y);
			if (xDiff == 1 || yDiff == 1) {
				return (xDiff ^ yDiff) == 1;
			}
		}

		return false;
	}

	public final int x;
	public final int y;
	public final int lvlIdx;
	private final RedstoneNodeTile tile;

	public RedstoneNode(int x, int y, int lvlIdx, RedstoneNodeTile tile) {
		this.x = x;
		this.y = y;
		this.lvlIdx = lvlIdx;
		this.tile = tile;
	}

	@Override
	public String toString() {
		return super.toString() + String.format("[%s;%s;%s;%s]" ,lvlIdx, x, y, tile);
	}
}
