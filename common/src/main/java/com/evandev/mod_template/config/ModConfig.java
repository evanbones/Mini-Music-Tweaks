package com.evandev.mod_template.config;

import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;

public class ModConfig extends ReflectiveConfig {

    public final Client client = new Client();

    @Comment("An example integer config option.")
    public final TrackedValue<Integer> exampleInteger = this.value(3);

    public static class Client extends Section {
        @Comment("An example client boolean config option.")
        public final TrackedValue<Boolean> exampleClientBoolean = this.value(true);
    }
}