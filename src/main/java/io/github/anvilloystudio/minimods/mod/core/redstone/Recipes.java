package io.github.anvilloystudio.minimods.mod.core.redstone;

import minicraft.item.Recipe;

public class Recipes {
	public static void init() {
		minicraft.item.Recipes.workbenchRecipes.add(new Recipe("Redstone_1", "Redstone_4", "Rock_1"));
	}
}
