package dev.draylar.illusion.api;

import dev.draylar.illusion.impl.PlayerIllusionState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Consumer;

public class Illusions {

    private static final Set<Illusion> ILLUSIONS = new HashSet<>();
    private static final Map<Block, List<Illusion>> BY_STATE = new LinkedHashMap<>();
    private static final Illusions INSTANCE = new Illusions();
    private static final List<Consumer<Illusions>> LISTENERS = new ArrayList<>();

    /**
     * Clears all active {@link Illusion} listeners, and re-invokes registry listeners to refresh active Illusions.
     */
    @ApiStatus.AvailableSince("1.1.0")
    public static void reload(MinecraftServer server) {
        ILLUSIONS.clear();
        BY_STATE.clear();

        // Clear global Illusion state cache
        Illusion.STATE.clear();

        // Clear per-player Illusion state cache
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ((PlayerIllusionState) player).clear();
        }

        for (Consumer<Illusions> listener : LISTENERS) {
            listener.accept(INSTANCE);
        }
    }

    /**
     * Registers the {@link Illusion}.
     */
    @ApiStatus.AvailableSince("1.1.0")
    public void register(Illusion illusion) {
        if(illusion.isEmpty()) {
            return;
        }

        ILLUSIONS.add(illusion);
        for (var entry : illusion.getReplacements().entrySet()) {
            BY_STATE.computeIfAbsent(entry.getKey(), it -> new ArrayList<>()).add(illusion);
        }
    }

    @ApiStatus.AvailableSince("1.0.0")
    public static void delete(Illusion illusion) {
        ILLUSIONS.remove(illusion);
    }

    @ApiStatus.AvailableSince("1.0.0")
    public static BlockState getFirstRemap(BlockState target, ServerPlayerEntity player) {
        Block asBlock = target.getBlock();
        List<Illusion> illusions = BY_STATE.get(asBlock);
        if(illusions == null) {
            return target;
        }

        for (Illusion illusion : illusions) {

            // todo: cache test values every tick, rather than checking them every time a block is serialized
            // otherwise we might run this ~1000 times in a single operation
            if(illusion.test(player)) {
                return illusion.getReplacements().get(asBlock);
            }
        }

        return target;
    }

    @ApiStatus.AvailableSince("1.1.0")
    public static void add(Consumer<Illusions> consumer) {
        LISTENERS.add(consumer);
        consumer.accept(INSTANCE);
    }

    @ApiStatus.AvailableSince("1.0.0")
    public static Set<Illusion> getAll() {
        return ILLUSIONS;
    }
}
