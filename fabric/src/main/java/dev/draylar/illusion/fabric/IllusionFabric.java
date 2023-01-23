package dev.draylar.illusion.fabric;

import dev.draylar.illusion.IllusionCommon;
import net.fabricmc.api.ModInitializer;

public class IllusionFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        IllusionCommon.init();
    }
}
