package com.evandev.mod_template;

import net.fabricmc.api.ModInitializer;

public class ExampleMod implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.init();
    }

}