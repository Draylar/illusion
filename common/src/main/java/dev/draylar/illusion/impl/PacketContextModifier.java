package dev.draylar.illusion.impl;

import net.minecraft.server.network.ServerPlayerEntity;

public interface PacketContextModifier {

    void remap(ServerPlayerEntity player);
}
