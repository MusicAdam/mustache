package com.gearworks.mos.state;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gearworks.mos.Client;

public class GameState implements State {
	private static int ID = 0;
	
	protected SpriteBatch batch;
	protected OrthographicCamera cam;

	@Override
	public void render(Client game) {
		cam.update();
		
		batch.begin();
		game.font.draw(batch, "GameState.render()", 10, 10);
		batch.end();
	}

	@Override
	public void update(Client game) {
		
	}

	@Override
	public void onEnter(Client game) {
		System.out.println("[GameState::onEnter]");
		batch = new SpriteBatch();
		cam = game.CreateCamera();		
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
	

}
