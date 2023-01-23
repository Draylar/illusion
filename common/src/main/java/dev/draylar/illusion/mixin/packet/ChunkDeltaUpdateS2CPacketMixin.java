package dev.draylar.illusion.mixin.packet;

import dev.draylar.illusion.api.Illusion;
import dev.draylar.illusion.impl.PacketContextModifier;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChunkDeltaUpdateS2CPacket.class)
public class ChunkDeltaUpdateS2CPacketMixin implements PacketContextModifier {

    @Shadow @Final private BlockState[] blockStates;

    @Unique
    @Override
    public void remap(ServerPlayerEntity player) {
        for (int i = 0; i < blockStates.length; i++) {
            blockStates[i] = Illusion.remap(player, blockStates[i]);
        }
    }
}
