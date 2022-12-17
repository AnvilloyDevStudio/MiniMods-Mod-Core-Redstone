package io.github.anvilloystudio.minimods.mod.core.redstone;

import io.github.anvilloystudio.minimods.mod.core.redstone.tiles.OreTiles;

public class Main {
	public static void main(String[] args) {
		System.out.println("THIS JAR IS NOT PURPOSED TO BE EXECUTED DIRECTLY.");
	}

	public static void entry() {
		OreTiles.postInit();
	}

	public static void init() {
		OreTiles.init();
		Recipes.init();
	}
}
