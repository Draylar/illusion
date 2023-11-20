package dev.draylar.illusion.mixin.server;

import dev.draylar.illusion.api.Illusion;
import dev.draylar.illusion.api.IllusionRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class ServerTickMixin {

    @Shadow
    private PlayerManager playerManager;
    @Shadow
    private Profiler profiler;

    /**
     * Each server tick: update {@link Illusion} states for the global server, and for each individual player.
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void illusion$tickPredicates(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        profiler.push("Illusion");
        for (Illusion illusion : IllusionRegistry.getAll()) {
            Illusion.STATE.tick(illusion, playerManager.getPlayerList());
        }

        profiler.pop();
    }
}
