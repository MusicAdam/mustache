package com.gearworks.mos.state;


import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gearworks.mos.Client;
import com.gearworks.mos.game.Entity;
import com.gearworks.mos.game.ships.Ship;

public class GameState implements State {
	private static int ID = 0;

	protected ArrayList<Entity> entities;
	protected SpriteBatch batch;
	
	public Ship ship;

	@Override
	public void render(Client game) {
		batch.setProjectionMatrix(game.camera().combined);
		batch.begin();
		game.font.draw(batch, "GameState.render()", 100, 50);
		for(Entity ent : entities){
			ent.render(batch);
		}
		batch.end();
	}

	@Override
	public void update(Client game) {
		for(Entity ent : entities){
			ent.update();
		}
	}

	@Override
	public void onEnter(Client game) {				
		entities = new ArrayList<Entity>();
		batch = new SpriteBatch();	
		
		ship = new Ship(game);
		ship.createPhysics();
		
		addEntity(ship);
		
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

	@Override
	public ArrayList<Entity> entities() {
		return entities;
	}
	

}
