package com.gearworks.mos.game.entities;

import static com.gearworks.mos.Box2DVars.PPM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
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
import com.gearworks.mos.Box2DVars;
import com.gearworks.mos.Client;
import com.gearworks.mos.InputMapper;
import com.gearworks.mos.game.Entity;

public class PlayerEntity extends Entity implements InputProcessor {
	private Texture texture;
	private Sprite idleSprite;
	private InputMapper inputMapper;
	
	//TODO: Move this to MoveState class
	private boolean inputLeft;
	private boolean inputRight;
	private boolean inputJump;
	
	public PlayerEntity(Client cRef) {
		super(cRef);
		
		inputMapper = new InputMapper();
		inputMapper.put("jump", Input.Keys.SPACE);
		inputMapper.put("left", Input.Keys.A);
		inputMapper.put("right", Input.Keys.D);
		
		Vector2 size = new Vector2(8, 14);
		
		texture = new Texture(Gdx.files.internal("sprites/player.png"));
		idleSprite = new Sprite(texture, 0, 0, 8, 14);
		
		createPhysics(size);
		resetMoveState();
	}
	
	@Override
	public void update(){
		if(inputRight){
			moveRight();
		}else if(inputLeft){
			moveLeft();
		}
		
		if(inputJump){
			doJump();
		}
		
		followBody();
	}
	
	@Override
	public void render(SpriteBatch batch){
		idleSprite.draw(batch);
	}
	
	@Override
	public void dispose(){
		texture.dispose();
		idleSprite = null;
	}
	
	public void moveRight(){
		body().applyForce(new Vector2(1.0f, 0f), body().getPosition(), true);
	}
	
	public void moveLeft(){
		body().applyForce(new Vector2(-1.0f, 0f), body().getPosition(), true);
	}
	
	public void doJump(){
		body().applyForce(new Vector2(0f, 10.0f), body().getPosition(), true);
	}
	
	//Returns the player's position in pixels
	public Vector2 getPosition(){
		return body().getPosition().scl(PPM);
	}
	
	//Update the sprite's position to that of the body
	private void followBody(){
		idleSprite.setPosition( (body().getPosition().x * PPM - idleSprite.getWidth() * 0.5f), 
								(body().getPosition().y * PPM - idleSprite.getHeight() * 0.5f) );
	}
	
	public void processInput(String key, boolean active){
		if(key == "right"){
			inputRight = active;
		}

		if(key == "left"){
			inputLeft = active;
		}
		
		if(key == "jump"){
			inputJump = active;
		}
	}
	
	@Override
	public boolean keyDown(int keycode) {
		String key = inputMapper.getMapping(keycode);
		
		if(key != null){
			processInput(key, true);
			return true;
		}
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		String key = inputMapper.getMapping(keycode);
		
		if(key != null){
			processInput(key, false);
			return true;
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		//Handle mouse down
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		//Handle mouse up
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		//Don't handle this event
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		//Don't handle this event
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		//Don't handle this event
		return false;
	}
	
	//Initialize the player's collision body
	private void createPhysics(Vector2 size){
		// Create a polygon shape
		PolygonShape playerBox = new PolygonShape();  
		// (setAsBox takes half-width and half-height as arguments)
		playerBox.setAsBox(size.x * 0.5f / PPM, size.y * 0.5f / PPM);
		
		// Create a fixture definition 
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = playerBox;
		fixtureDef.density = 0.1f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0f; // Make it bounce a little bit
		
		Vector2 pos = new Vector2(10, 1);
		
		createDynamicBody(this, pos, fixtureDef);
		body().setFixedRotation(true);
		
		playerBox.dispose();
	}
	
	//Reset move state
	private void resetMoveState(){
		inputLeft 	= false;
		inputRight 	= false;
		inputJump 	= false;
	}
}
