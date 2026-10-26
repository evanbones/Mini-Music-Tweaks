package com.evandev.music_tweaks;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
	public static final String MOD_ID = "music_tweaks";
	public static final String MOD_NAME = "Mini Music Tweaks";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	public static ResourceLocation id(String path) {
		//? if <1.21 {
		/*return new ResourceLocation(MOD_ID, path);
		*///?} else {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
		//?}
	}

	public static ResourceLocation parse(String id) {
		//? if <1.21 {
		/*return new ResourceLocation(id);
		*///?} else {
		return ResourceLocation.parse(id);
		//?}
	}
}
