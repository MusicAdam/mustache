package com.gearworks.mos.state;

import static com.gearworks.mos.Box2DVars.PPM;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gearworks.mos.Client;
import com.gearworks.mos.game.Entity;
import com.gearworks.mos.game.entities.PlayerEntity;

public class GameState implements State {
	private static int ID = 0;
	
	protected SpriteBatch batch;
	
	private List<Entity> entities;

	@Override
	public void render(Client game) {
		batch.begin();
		game.font.draw(batch, "GameState.render()", 100, 50);
		batch.end();
	}

	@Override
	public void update(Client game) {
		
	}

	@Override
	public void onEnter(Client game) {				
		entities = new ArrayList<Entity>();
		batch = new SpriteBatch();		
		
		game.player();

		PolygonShape levelBox = new PolygonShape();  
		// Set the polygon shape as a box which is twice the size of our view port and 20 high
		// (setAsBox takes half-width and half-height as arguments)
		levelBox.setAsBox(game.camera().viewportWidth, 1.0f / PPM);
		
		Entity level = new Entity(game);
		Entity.createStaticBody(level, new Vector2(game.camera().viewportWidth/2, 0), levelBox);
		
		System.out.println("[GameState::onEnter]");
	}

	@Override
	public void onExit(Client game) {
		
	}
	
	@Override
	public boolean canEnterState(Client game) {
		return true;
	}

	@Override
	public boolean canExitState(Client game) {
		return false;
	}
	
	@Override
	public int getId(){ return ID; }

	@Override
	public void deleteEntity(Entity ent) {
		entities.remove(ent);
	}

	@Override
	public void addEntity(Entity ent) {
		entities.add(ent);
	}
	

}
