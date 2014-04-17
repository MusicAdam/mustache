package com.gearworks.mos.game.entities;

import static com.gearworks.mos.Box2DVars.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gearworks.mos.Client;
import com.gearworks.mos.game.Entity;

public class PlayerEntity extends Entity {
	private Texture texture;
	private Sprite idleSprite;
	
	public PlayerEntity(Client cRef) {
		super(cRef);
		//Average human size in meters
		Vector2 size = new Vector2(8, 14);
		
		texture = new Texture(Gdx.files.internal("sprites/player.png"));
		idleSprite = new Sprite(texture, 0, 0, (int)size.x, (int)size.y);
		idleSprite.setPosition(10,  10);
		/////////	Setup Box2d
		
		// Create a polygon shape
		PolygonShape playerBox = new PolygonShape();  
		// Set the polygon shape as a box which is twice the size of our view port and 20 high
		// (setAsBox takes half-width and half-height as arguments)
		playerBox.setAsBox(size.x / PPM, size.y / PPM);
		
		// Create a fixture definition 
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = playerBox;
		fixtureDef.density = 0.1f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0f; // Make it bounce a little bit
		
		Vector2 pos = new Vector2(
					1,
					1
				);
		
		createDynamicBody(this, pos, fixtureDef);
		
		playerBox.dispose();
	}
	
	@Override
	public void update(){
		idleSprite.setPosition(body().getPosition().x * PPM, body().getPosition().y * PPM);
	}
	
	@Override
	public void render(SpriteBatch batch){
		idleSprite.draw(batch);
	}
	
	@Override
	public void dispose(){
		
	}
}
