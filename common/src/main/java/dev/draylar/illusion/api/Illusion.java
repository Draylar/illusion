package dev.draylar.illusion.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class Illusion {

    public static final IllusionState STATE = new IllusionState();

    private final Map<Block, BlockState> replacements = new LinkedHashMap<>();
    private final boolean modifyDrops;
    private final boolean modifyProperties;
    private final int frequency;
    private final Predicate<ServerPlayerEntity> check;
    private final ApplicationStrategy strategy;

    private Illusion(Map<IllusionTarget, IllusionReplacement> replacements, boolean modifyDrops, boolean modifyProperties, int frequency, ApplicationStrategy strategy, Predicate<ServerPlayerEntity> check) {
        this.modifyDrops = modifyDrops;
        this.modifyProperties = modifyProperties;
        this.frequency = frequency;
        this.strategy = strategy;
        this.check = check;

        // boil Replacement wrapper class into base elements
        replacements.forEach((target, replacement) -> {
            for (Block value : target.values()) {
                this.replacements.put(value, replacement.block());
            }
        });
    }

    public static Builder create() {
        return new Builder();
    }

    public static IllusionTarget from(Block... blocks) {
        return new IllusionTarget(blocks);
    }

    public static IllusionReplacement to(BlockState block) {
        return new IllusionReplacement(block);
    }

    public static IllusionReplacement to(Block block) {
        return new IllusionReplacement(block.getDefaultState());
    }

    public static BlockState remap(ServerPlayerEntity player, BlockState target) {
        @Nullable BlockState remapped = IllusionRegistry.getFirstRemap(target, player);
        return Objects.requireNonNullElse(remapped, target);
    }

    public boolean isEmpty() {
        return replacements.isEmpty();
    }

    public Map<Block, BlockState> getReplacements() {
        return replacements;
    }

    public void delete() {
        IllusionRegistry.delete(this);
    }

    public boolean test(ServerPlayerEntity player) {
        return check.test(player);
    }

    public ApplicationStrategy getStrategy() {
        return strategy;
    }

    public boolean modifyDrops() {
        return modifyDrops;
    }

    public static class Builder {

        private final Map<IllusionTarget, IllusionReplacement> replacements = new HashMap<>();
        private boolean modifyDrops = true;
        private boolean modifyProperties = true;
        private int updateFrequency = 1;
        private ApplicationStrategy applicationStrategy = ApplicationStrategy.PER_PLAYER;
        private Predicate<ServerPlayerEntity> predicate = player -> true;

        public Builder map(IllusionTarget from, IllusionReplacement to) {
            replacements.put(from, to);
            return this;
        }

        public Builder modifyDrops(boolean value) {
            modifyDrops = value;
            return this;
        }

        public Builder modifyProperties(boolean value) {
            modifyProperties = value;
            return this;
        }

        public Builder when(Predicate<ServerPlayerEntity> predicate) {
            this.predicate = predicate;
            return this;
        }

        public Builder withApplicationStrategy(ApplicationStrategy strategy) {
            this.applicationStrategy = strategy;
            return this;
        }

        public Builder updateFrequency(int ticks) {
            if (ticks <= 0) {
                throw new IllegalStateException("Illusion update frequency must be 1 or more ticks!");
            }

            this.updateFrequency = ticks;
            return this;
        }

        public Illusion build() {
            return new Illusion(replacements, modifyDrops, modifyProperties, updateFrequency, applicationStrategy, predicate);
        }
    }
}
