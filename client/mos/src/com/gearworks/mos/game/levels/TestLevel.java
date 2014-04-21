package com.gearworks.mos.game.levels;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.gearworks.mos.Client;
import com.gearworks.mos.game.Entity;
import com.gearworks.mos.game.Level;

public class TestLevel extends Level {

	public TestLevel(Client game) {
		super(game);
		load("maps/test.tmx");
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(game.camera().viewportWidth, 1f);
		Entity.createStaticBody(new Entity(game), new Vector2(0, 0), shape);
	}

}
