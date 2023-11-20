package dev.draylar.illusion.mixin.block;

import dev.draylar.illusion.api.Illusion;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockMixin {

    @Inject(method = "calcBlockBreakingDelta", at = @At("HEAD"), cancellable = true)
    private void illusion$adjustBlockDelta(PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (player instanceof ServerPlayerEntity serverPlayerEntity) {
            BlockState remapped = Illusion.STATE.remap(serverPlayerEntity, (BlockState) (Object) this);
            cir.setReturnValue(
                    remapped.getBlock().calcBlockBreakingDelta(
                            remapped,
                            player,
                            world,
                            pos
                    )
            );
        }
    }
}
