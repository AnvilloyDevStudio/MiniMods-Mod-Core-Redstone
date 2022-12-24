package io.github.anvilloystudio.minimods.mod.core.redstone.mixins;

import io.github.anvilloystudio.minimods.api.GraphicComp;
import io.github.anvilloystudio.minimods.mod.core.redstone.items.ComparatorItem;
import io.github.anvilloystudio.minimods.mod.core.redstone.items.RepeaterItem;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.TileItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.ArrayList;

@Mixin(TileItem.class)
public class TileItemMixin {
	@Invoker(value = "<init>", remap = false)
	private static TileItem invokeInit(String name, Sprite sprite, String model, String... validTiles) {
		throw new AssertionError();
	}

	@Inject(method = "getAllInstances()Ljava/util/ArrayList;", at = @At(value = "TAIL", remap = false), remap = false, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private static void injectMoreItems(CallbackInfoReturnable<ArrayList<Item>> ci, ArrayList<Item> items) {
		try {
			items.add(invokeInit("Redstone", GraphicComp.getSpriteFromSheet(1, 1,
					GraphicComp.getSpriteSheetFromInputStream(TileItemMixin.class.getResourceAsStream("/assets/textures/items/redstone.png"))),
				"Redstone", "Stone Bricks")); // Redstone Dust
			items.add(invokeInit("Redstone Switch", GraphicComp.getSpriteFromSheet(1, 1,
					GraphicComp.getSpriteSheetFromInputStream(TileItemMixin.class.getResourceAsStream("/assets/textures/items/redstone_switch.png"))),
				"Redstone Switch", "Stone Bricks")); // Redstone Switch
			items.add(invokeInit("Button", GraphicComp.getSpriteFromSheet(1, 1,
					GraphicComp.getSpriteSheetFromInputStream(TileItemMixin.class.getResourceAsStream("/assets/textures/items/button.png"))),
				"Button", "Stone Bricks")); // Button
			items.add(invokeInit("Permanent Redstone Transmitter", GraphicComp.getSpriteFromSheet(1, 1,
					GraphicComp.getSpriteSheetFromInputStream(TileItemMixin.class.getResourceAsStream("/assets/textures/items/permanent_redstone_transmitter.png"))),
				"Permanent Redstone Transmitter", "Stone Bricks")); // Permanent Redstone Transmitter
			items.add(invokeInit("Daylight Detector", GraphicComp.getSpriteFromSheet(1, 1,
					GraphicComp.getSpriteSheetFromInputStream(TileItemMixin.class.getResourceAsStream("/assets/textures/items/daylight_detector.png"))),
				"Daylight Detector", "Stone Bricks")); // Daylight Detector
			items.add(invokeInit("Pressure Plate", GraphicComp.getSpriteFromSheet(1, 1,
					GraphicComp.getSpriteSheetFromInputStream(TileItemMixin.class.getResourceAsStream("/assets/textures/items/pressure_plate.png"))),
				"Pressure Plate", "Stone Bricks")); // Pressure Plate
			items.add(invokeInit("Redstone Torch", GraphicComp.getSpriteFromSheet(1, 1,
					GraphicComp.getSpriteSheetFromInputStream(TileItemMixin.class.getResourceAsStream("/assets/textures/items/redstone_torch.png"))),
				"Redstone Torch", "Stone Bricks")); // Redstone Torch
			items.add(new RepeaterItem("Repeater", GraphicComp.getSpriteFromSheet(1, 1,
				GraphicComp.getSpriteSheetFromInputStream(TileItemMixin.class.getResourceAsStream("/assets/textures/items/repeater.png"))),
				"Repeater", "Stone Bricks")); // Repeater
			items.add(new ComparatorItem("Comparator", GraphicComp.getSpriteFromSheet(1, 1,
				GraphicComp.getSpriteSheetFromInputStream(TileItemMixin.class.getResourceAsStream("/assets/textures/items/comparator.png"))),
				"Comparator", "Stone Bricks")); // Comparator
		} catch (IOException e) {
			throw new RuntimeException("Unable to initialize item(s).", e);
		}
	}
}
