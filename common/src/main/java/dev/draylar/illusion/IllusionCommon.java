package dev.draylar.illusion;

import dev.architectury.platform.Platform;
import dev.draylar.illusion.api.Illusion;
import dev.draylar.illusion.api.IllusionRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;

public class IllusionCommon {

    public static void init() {
        if (Platform.isDevelopmentEnvironment()) {
            IllusionRegistry.register(Illusion.create()
                    .map(Illusion.from(Blocks.IRON_ORE), Illusion.to(Blocks.DIAMOND_BLOCK))
                    .modifyDrops(true)
                    .modifyProperties(true)
                    .when(player -> player.getMainHandStack().getItem().equals(Items.DIAMOND) && player.isSneaking())
                    .build());

            IllusionRegistry.register(Illusion.create()
                    .map(Illusion.from(Blocks.IRON_ORE), Illusion.to(Blocks.STONE))
                    .modifyDrops(true)
                    .modifyProperties(true)
                    .when(Entity::isSneaking)
                    .build());
        }
    }
}
