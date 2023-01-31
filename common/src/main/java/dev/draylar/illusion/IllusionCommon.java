package dev.draylar.illusion;

import dev.architectury.platform.Platform;
import dev.draylar.illusion.api.Illusion;
import dev.draylar.illusion.api.Illusions;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

public class IllusionCommon {

    public static void init() {
        if(Platform.isDevelopmentEnvironment()) {
            Illusions.add(registry -> {
                Illusion.create()
                        .map(Illusion.from(Blocks.IRON_ORE), Illusion.to(Blocks.STONE))
                        .modifyDrops(true)
                        .modifyProperties(true)
                        .when(Entity::isSneaking)
                        .build(registry);

                Illusion.create()
                        .map(Illusion.from(Blocks.DIAMOND_ORE), Illusion.to(Blocks.STONE))
                        .modifyDrops(true)
                        .modifyProperties(true)
                        .when(ServerPlayerEntity::isCreative)
                        .build(registry);
            });
        }
    }
}