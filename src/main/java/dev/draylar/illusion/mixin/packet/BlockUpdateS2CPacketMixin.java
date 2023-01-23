package dev.draylar.illusion.mixin.packet;

import dev.draylar.illusion.api.Illusion;
import dev.draylar.illusion.impl.PacketContextModifier;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.*;

@Mixin(BlockUpdateS2CPacket.class)
public class BlockUpdateS2CPacketMixin implements PacketContextModifier {

    @Shadow @Final @Mutable private BlockState state;

    @Unique
    @Override
    public void remap(ServerPlayerEntity player) {
        state = Illusion.remap(player, state);
    }
}
