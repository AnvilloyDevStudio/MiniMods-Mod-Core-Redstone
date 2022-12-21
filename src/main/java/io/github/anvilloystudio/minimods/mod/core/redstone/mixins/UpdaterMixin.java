package io.github.anvilloystudio.minimods.mod.core.redstone.mixins;

import io.github.anvilloystudio.minimods.mod.core.redstone.circuit.RedstoneNode;
import minicraft.core.Updater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Updater.class)
public class UpdaterMixin {
	@Inject(method = "tick()V", at = @At(value = "INVOKE", target = "Lminicraft/level/Level;tick(Z)V", remap = false), remap = false)
	private static void updateRedstoneTiles(CallbackInfo ci) {
		RedstoneNode.refreshNodes();
		RedstoneNode.refreshCircuits();
	}
}
