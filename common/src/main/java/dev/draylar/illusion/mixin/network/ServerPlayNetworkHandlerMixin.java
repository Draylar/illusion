package dev.draylar.illusion.mixin.network;

import dev.draylar.illusion.api.Illusion;
import dev.draylar.illusion.impl.PacketContextModifier;
import net.minecraft.block.BlockState;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

    @Shadow public ServerPlayerEntity player;

    /**
     * Before a {@link Packet} is sent to a {@link ServerPlayerEntity}: if the packet contains re-mappable {@link BlockState}
     * data from an {@link Illusion}, adjust the packet data now.
     */
    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"))
    private void illusion$remapPacketBlockstates(Packet<?> packet, CallbackInfo ci) {
        if(packet instanceof PacketContextModifier modifier) {
            modifier.remap(player);
        }
    }
}
