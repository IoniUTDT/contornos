package com.turin.tur.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.turin.tur.Visound;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;


public class DesktopLauncher {
	
	private static boolean rebuildAtlas = false;
	private static boolean drawDebugOutline = false;
		
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
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
	    System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
		config.width = 800;
		config.height = 480;
		new LwjglApplication(new Visound(), config);
	}
}
