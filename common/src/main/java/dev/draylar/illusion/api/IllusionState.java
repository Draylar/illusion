package dev.draylar.illusion.api;

import dev.draylar.illusion.impl.PlayerIllusionState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IllusionState {

    private final StateRemap global = new StateRemap(new HashMap<>());

    public void tick(Illusion illusion, List<ServerPlayerEntity> players) {
        if(illusion.getStrategy() == ApplicationStrategy.GLOBAL) {
            // TODO: NYI
        } else {
            for (ServerPlayerEntity player : players) {
                boolean result = ((PlayerIllusionState) player).tickIllusion(illusion);
                if(result) {
                    // Re-send chunk packets
                    player.getWorld().getChunkManager().threadedAnvilChunkStorage.handlePlayerAddedOrRemoved(
                            player,
                            true
                    );
                }
            }
        }
    }

    @Nullable
    public Illusion getActive(ServerPlayerEntity player, BlockState state) {
        @Nullable List<Illusion> globalRemap = global.get(state.getBlock());
        if(globalRemap != null) {
            if(!globalRemap.isEmpty()) {
                return globalRemap.get(0);
            }
        }

        return  ((PlayerIllusionState) player).getActive(state);
    }

    public BlockState remap(ServerPlayerEntity player, BlockState state) {
        @Nullable Illusion illusion = getActive(player, state);
        if(illusion == null) {
            return state;
        }

        return remap(illusion, state);
    }

    public BlockState remap(Illusion illusion, BlockState state) {
        if(illusion != null) {
            @Nullable BlockState replacement = illusion.getReplacements().get(state.getBlock());
            if(replacement != null) {
                return replacement;
            }
        }

        return state;
    }

    public record StateRemap(Map<Block, List<Illusion>> values) {

        public List<Illusion> get(Block state) {
            return values.get(state);
        }

        public void add(Block key, Illusion illusion) {
            values.computeIfAbsent(key, it -> new ArrayList<>()).add(illusion);
        }

        public void remove(Block key, Illusion illusion) {
            @Nullable List<Illusion> illusions = values.get(key);
            if(illusions != null) {
                illusions.remove(illusion);
            }
        }
    }
}
