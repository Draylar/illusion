package dev.draylar.illusion.mixin.server;

import dev.draylar.illusion.api.Illusion;
import dev.draylar.illusion.api.IllusionState;
import dev.draylar.illusion.impl.PlayerIllusionState;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements PlayerIllusionState {

    @Unique
    private final IllusionState.StateRemap remapper = new IllusionState.StateRemap(new HashMap<>());

    @Unique
    private final Map<Illusion, Boolean> illusionResultCache = new HashMap<>();

    @Unique
    @Override
    public boolean tickIllusion(Illusion illusion) {
        boolean reload = false;
        Boolean prev = illusionResultCache.putIfAbsent(illusion, false);
        boolean result = illusion.test((ServerPlayerEntity) (Object) this);
        if(prev == null || result != prev) {
            illusionResultCache.put(illusion, result);

            // The predicate changed values from false to true.
            // This Illusion should now be applied to all BlockStates it covers.
            if(result) {

                // For each Block that this Illusion remaps, store it in the block's list.
                for (var entry : illusion.getReplacements().entrySet()) {
                    remapper.add(entry.getKey(), illusion);
                }
            }

            // The predicate changed values from true to false.
            // This Illusion should no longer be applied.
            else {
                for (var entry : illusion.getReplacements().entrySet()) {
                    remapper.remove(entry.getKey(), illusion);
                }
            }

            reload = true;
        }

        return reload;
    }

    @Unique
    @Override
    public Illusion getActive(BlockState state) {
        @Nullable List<Illusion> globalRemap = remapper.get(state.getBlock());
        if(globalRemap != null) {
            if(!globalRemap.isEmpty()) {
                return globalRemap.get(0);
            }
        }

        return null;
    }

    @Unique
    public void clear() {
        remapper.values().clear();
        illusionResultCache.clear();
    }
}
