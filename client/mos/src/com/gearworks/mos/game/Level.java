package com.gearworks.mos.game;

import java.util.List;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.gearworks.mos.Client;

/*
 * Loads in tiled levels and constructs box2d collision bodies from the data
 */
public class Level {
	private TiledMap tileMap;
	private OrthogonalTiledMapRenderer mapRenderer;
	private Client game;
	
	public Level(Client game){
		this.game = game;
	}
	
	public void load(String name){
		tileMap = new TmxMapLoader().load(name);
		mapRenderer = new OrthogonalTiledMapRenderer(tileMap);
		
		createCollisionMap();
	}
	
	private void createCollisionMap(){
		if(tileMap == null) return;
		
		//tileMap.
		
	}

	public void render() {
		if(mapRenderer == null) return;
				
		mapRenderer.setView(game.camera());
		mapRenderer.render();
	}

	public void update() {
	}
	
}
