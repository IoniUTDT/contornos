package com.turin.tur.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.turin.tur.ContornosMain;
import com.turin.tur.main.util.Constants;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;


public class DesktopLauncher {
	
	private static boolean rebuildAtlas = false;
	private static boolean drawDebugOutline = false;
	private static boolean rebuildAtlasSource = false;
	
	public static void main (String[] arg) {
		
		
		if (rebuildAtlas) {
			Settings settings = new Settings();
			settings.maxWidth = 1024;
			settings.maxHeight = 1024;
			settings.duplicatePadding = false;
			settings.debug = drawDebugOutline;
			TexturePacker.process(settings, "asset-raw/images", "../android/assets/images",
					"cajas.pack");
			TexturePacker.process(settings, "asset-raw/images-ui", "../android/assets/images",
					"cajas-ui.pack");
		}
		
		if (rebuildAtlasSource) {
			
			int version_temp = MathUtils.roundPositive(Constants.VERSION);
			int temp;
			if (version_temp>Constants.VERSION) {temp=-1;} else {temp=0;} 
			int version = version_temp + temp;
			
			Settings settings = new Settings();
			settings.maxWidth = 1024;
			settings.maxHeight = 1024;
			settings.duplicatePadding = false;
			settings.debug = drawDebugOutline;
			TexturePacker.process(settings, "asset-raw/imagesource/"+version+"/", "../android/assets/images/experimentalsource/",version+"/images.pack");
		}
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 480;
		new LwjglApplication(new ContornosMain(), config);
	}
}
