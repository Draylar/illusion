package dev.draylar.illusion.impl;

import dev.draylar.illusion.api.Illusion;
import net.minecraft.block.BlockState;

public interface PlayerIllusionState {

    boolean tickIllusion(Illusion illusion);

    Illusion getActive(BlockState state);
}
