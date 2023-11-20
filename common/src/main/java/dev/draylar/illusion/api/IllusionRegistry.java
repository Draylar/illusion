package dev.draylar.illusion.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class IllusionRegistry {

    private static final Set<Illusion> ILLUSIONS = new HashSet<>();
    private static final Map<Block, List<Illusion>> BY_STATE = new LinkedHashMap<>();

    public static void register(Illusion illusion) {
        if (illusion.isEmpty()) {
            return;
        }

        ILLUSIONS.add(illusion);
        illusion.getReplacements().forEach((block, replacement) -> {
            BY_STATE.computeIfAbsent(block, it -> new ArrayList<>()).add(illusion);
        });
    }

    public static void delete(Illusion illusion) {
        ILLUSIONS.remove(illusion);
    }

    public static BlockState getFirstRemap(BlockState target, ServerPlayerEntity player) {
        Block asBlock = target.getBlock();
        List<Illusion> illusions = BY_STATE.get(asBlock);
        if (illusions == null) {
            return target;
        }

        for (Illusion illusion : illusions) {

            // todo: cache test values every tick, rather than checking them every time a block is serialized
            // otherwise we might run this ~1000 times in a single operation
            if (illusion.test(player)) {
                return illusion.getReplacements().get(asBlock);
            }
        }

        return target;
    }

    public static Set<Illusion> getAll() {
        return ILLUSIONS;
    }
}
