package com.gearworks.mos.state;

import static com.gearworks.mos.Box2DVars.PPM;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gearworks.mos.Client;
import com.gearworks.mos.game.Entity;
import com.gearworks.mos.game.entities.PlayerEntity;
import com.gearworks.mos.game.levels.TestLevel;

public class GameState implements State {
	private static int ID = 0;

	protected List<Entity> entities;
	protected SpriteBatch batch;
	
	private TestLevel testLevel;

	@Override
	public void render(Client game) {
		batch.begin();
		game.font.draw(batch, "GameState.render()", 100, 50);
		for(Entity ent : entities){
			ent.render(batch);
		}
		batch.end();
		testLevel.render();
	}

	@Override
	public void update(Client game) {
		for(Entity ent : entities){
			ent.update();
		}
		
		testLevel.update();
	}

	@Override
	public void onEnter(Client game) {				
		entities = new ArrayList<Entity>();
		batch = new SpriteBatch();		
		
		testLevel = new TestLevel(game);
		
		addEntity(game.player());
		
		
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
