package io.github.anvilloystudio.minimods.mod.core.redstone;

import io.github.anvilloystudio.minimods.mod.core.redstone.items.Items;
import io.github.anvilloystudio.minimods.mod.core.redstone.items.Recipes;
import io.github.anvilloystudio.minimods.mod.core.redstone.tiles.OreTiles;
import io.github.anvilloystudio.minimods.mod.core.redstone.tiles.Tiles;

public class Main {
	public static void main(String[] args) {
		System.out.println("THIS JAR IS NOT PURPOSED TO BE EXECUTED DIRECTLY.");
	}

	public static void entry() {
		OreTiles.postInit();
		Items.postInit();
		Tiles.postInit();
	}

	public static void init() {
		OreTiles.init();
		Recipes.init();
	}
}
