package com.evandev.mod_template;

import com.evandev.mod_template.config.ModConfig;
import com.evandev.mod_template.platform.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
    public static final String MOD_ID = "mod_template";
    public static final String MOD_NAME = "Mod Template";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final ModConfig CONFIG = ModConfig.createToml(Services.PLATFORM.getConfigDirectory(), "", MOD_ID, ModConfig.class);
}