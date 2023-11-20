package dev.draylar.illusion.mixin.palette;

import dev.draylar.illusion.api.Illusion;
import dev.draylar.illusion.api.IllusionContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.chunk.SingularPalette;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SingularPalette.class)
public class SingularPaletteMixin<T> {

    @Shadow
    private @Nullable T entry;

    @Inject(method = "writePacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeVarInt(I)Lnet/minecraft/network/PacketByteBuf;"), cancellable = true)
    private void illusion$remapHiddenData(PacketByteBuf buf, CallbackInfo ci) {
        if (entry instanceof BlockState singleState) {
            BlockState remapped = Illusion.remap(IllusionContext.TARGET_PLAYER, singleState);
            buf.writeVarInt(Block.STATE_IDS.getRawId(remapped));
            ci.cancel();
        }
    }
}
