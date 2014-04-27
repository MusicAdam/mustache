package com.gearworks.mos; 

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = Client.TITLE;
		cfg.width = Client.V_WIDTH * Client.SCALE;
		cfg.height = Client.V_HEIGHT * Client.SCALE;
		
		new LwjglApplication(new Client(), cfg);
	}
}