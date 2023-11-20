package dev.draylar.illusion.mixin.palette;

import dev.draylar.illusion.api.Illusion;
import dev.draylar.illusion.api.IllusionContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.chunk.ArrayPalette;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArrayPalette.class)
public class ArrayPaletteMixin<T> {

    @Shadow
    private int size;
    @Shadow
    @Final
    private T[] array;

    @Inject(method = "writePacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeVarInt(I)Lnet/minecraft/network/PacketByteBuf;", shift = At.Shift.AFTER, ordinal = 0), cancellable = true)
    private void illusion$remapHiddenData(PacketByteBuf buf, CallbackInfo ci) {
        // only try to process blocks if some exist
        if (size > 0) {

            // double-check that we're processing BlockState and not something else (such as Biome)
            if (array[0] instanceof BlockState) {
                for (int i = 0; i < size; ++i) {
                    BlockState remapped = Illusion.remap(IllusionContext.TARGET_PLAYER, (BlockState) array[i]);
                    buf.writeVarInt(Block.STATE_IDS.getRawId(remapped));
                }

                ci.cancel();
            }
        }
    }
}
