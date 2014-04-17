package com.gearworks.mos.game.levels;

import com.gearworks.mos.Client;
import com.gearworks.mos.game.Level;

public class TestLevel extends Level {

	public TestLevel(Client game) {
		super(game);
		load("maps/test.tmx");
	}

}
