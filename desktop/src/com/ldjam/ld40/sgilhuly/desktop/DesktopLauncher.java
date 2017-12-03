package com.ldjam.ld40.sgilhuly.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ldjam.ld40.sgilhuly.RuinGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "In Temperance Ruin";
		config.width = 640;
		config.height = 360;
		//config.fullscreen = true;
		new LwjglApplication(new RuinGame(), config);
	}
}
