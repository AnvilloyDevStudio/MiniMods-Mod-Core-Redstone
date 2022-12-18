package io.github.anvilloystudio.minimods.mod.core.redstone.mixins;

import io.github.anvilloystudio.minimods.api.GraphicComp;
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
		} catch (IOException e) {
			throw new RuntimeException("Unable to initialize item(s).", e);
		}
	}
}
