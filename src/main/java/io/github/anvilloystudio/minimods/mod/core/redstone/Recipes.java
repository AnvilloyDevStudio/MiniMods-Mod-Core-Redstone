package io.github.anvilloystudio.minimods.mod.core.redstone;

import minicraft.item.Recipe;

public class Recipes {
	public static void init() {
		minicraft.item.Recipes.workbenchRecipes.add(new Recipe("Redstone Switch_1", "Redstone_2", "Rock_2"));
		minicraft.item.Recipes.workbenchRecipes.add(new Recipe("Button_1", "Redstone_1", "Rock_4"));
		minicraft.item.Recipes.workbenchRecipes.add(new Recipe("Permanent Redstone Transmitter_1", "Redstone_10", "Rock_2"));
		minicraft.item.Recipes.workbenchRecipes.add(new Recipe("Daylight Detector_1", "Redstone_2", "Rock_1", "Plank_1", "Glass_2", "Iron Ingot_1"));
		minicraft.item.Recipes.workbenchRecipes.add(new Recipe("Pressure Plate_1", "Redstone_1", "Rock_4"));
		minicraft.item.Recipes.workbenchRecipes.add(new Recipe("Redstone Torch_1", "Redstone_1", "Plank_1"));
		minicraft.item.Recipes.workbenchRecipes.add(new Recipe("Repeater_1", "Redstone_2", "Redstone Torch_2", "Rock_4"));
		minicraft.item.Recipes.workbenchRecipes.add(new Recipe("Comparator_1", "Redstone_3", "Redstone Torch_3", "Rock_4"));
		minicraft.item.Recipes.workbenchRecipes.add(new Recipe("Redstone Lamp_1", "Redstone_2", "Redstone Torch_1", "Plank_2", "Glass_2"));
	}
}
