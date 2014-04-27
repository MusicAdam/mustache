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
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gearworks.mos.Box2DVars;
import com.gearworks.mos.Client;
import com.gearworks.mos.InputMapper;
import com.gearworks.mos.game.Entity;
import com.gearworks.mos.game.EntityType;

public class PlayerEntity extends Entity implements InputProcessor {	
	public final float groundJump 	= .8f;	//The impulse applied when jumping from the ground
	public final float wallJump 	= .4f;	//The impulse applied when jumping from a wall
	public final float pushStrength	= 100f; //Force applied to push off of wall
	public final float groundForce  = 1.2f;	//The force applied to ground movement
	public final float airForce  	= .8f;	//The force applied to air movement
	public final float groundSpeed	= 10;  //10 M/s
	public final float airSpeed		= 5;  //M/s
	public final float stickiness 	= 3f;  //How much the player sticks to the wall
	
	private Texture texture;
	private Sprite idleSprite;
	private InputMapper inputMapper;
	
	//TODO: Move this to MoveState class
	private boolean inputLeft;
	private boolean inputRight;
	private boolean inputJump;
	private boolean inputUp;
	private boolean inputDown;
	private boolean stuckToWall; //Gets set to true if the player grabs the wall (is touching and moves into the wall)
	
	private Entity onWall;
	private Vector2 wallNorm;
	private Entity onGround;
	
	private ArmGhost gostArms;
	
	private int width, height;
	
	public PlayerEntity(Client cRef) {
		super(EntityType.Player, cRef);
		
		inputMapper = new InputMapper();
		inputMapper.put("jump", Input.Keys.SPACE);
		inputMapper.put("left", Input.Keys.A);
		inputMapper.put("right", Input.Keys.D);
		inputMapper.put("down", Input.Keys.S);
		inputMapper.put("up", Input.Keys.W);
		
		Vector2 size = new Vector2(8, 14);
		width = 8;
		height = 14;
		
		texture = new Texture(Gdx.files.internal("sprites/player.png"));
		idleSprite = new Sprite(texture, 0, 0, width, height);
		
		resetMoveState();
	}
	
	@Override
	public void update(){
		followBody();
		
		Vector2 force = new Vector2();
		Vector2 impulse = new Vector2();
		
		if(body().getLinearVelocity().len() < calculateSpeed()){
			if(inputRight){
				moveRight(force);
				
				if(onWall() && wallNorm.x == -1){
					stuckToWall = true;
				}
			}else if(inputLeft){
				moveLeft(force);
				
				if(onWall() && wallNorm.x == 1){
					stuckToWall = true;
				}
			}
		}
					
		
		if(inputJump){
			if(onWall()){
				doWallJump(impulse);
			}else{
				doJump(impulse);
			}
		}
		
		if(stuckToWall && inputDown){
			stuckToWall = false;
		}
		
		doStick(force);
		
		body().applyForce(force, body().getPosition(), true);
		body().applyLinearImpulse(impulse, body().getPosition(), true);
	}

	private float calculateSpeed() {
		if(onGround()){
			return groundSpeed;
		}else{
			return airSpeed;
		}
	}

	@Override
	public void render(SpriteBatch batch){
		idleSprite.draw(batch);
		renderDbg(batch);
	}
	
	@Override
	public void dispose(){
		texture.dispose();
		idleSprite = null;
	}
	
	public void moveRight(Vector2 force){
		if(onWall()) return;
		
		force.add(calculateMoveForce());
		
		body().applyForce(force, body().getPosition(), true);
	}
	
	public void moveLeft(Vector2 force){
		if(onWall()) return;
		
		Vector2 f = calculateMoveForce();
		f.x *= -1;
		force.add(f);
	}
	
	
	public void doJump(Vector2 impulse){
		if(!onGround() || onWall()) return; 

		impulse.add(new Vector2(0, groundJump));
		onGround(null);
	}
	
	public void doWallJump(Vector2 impulse){
		if(!onWall() || onGround()) return;
		
		Vector2 jump = wallNorm.cpy().add(0, .8f).scl(wallJump);
		System.out.println(jump);
		
		impulse.add(jump);
		onWall(null, null);
		stuckToWall = false;
	}
	
	//If on wall, hold onto to simulate grabbing, if stuck, multiply that so it sticks
	private void doStick(Vector2 force) {
		if(!onWall()) return;
		float stick = stickiness;
		
		if(stuckToWall)
			stick *= 10;
		
		force.add(wallNorm.cpy().scl(-1 * stick));
	}
	
	//Returns the player's position in pixels
	public Vector2 getPosition(){
		return body().getPosition().scl(PPM);
	}
	
	//Update the sprite's position to that of the body
	private void followBody(){
		float dX = (body().getPosition().x * PPM - idleSprite.getWidth() * 0.5f) - idleSprite.getX();
		float dY = (body().getPosition().y * PPM - idleSprite.getHeight() * 0.5f) - idleSprite.getY();
		idleSprite.setPosition( idleSprite.getX() + dX/2, 
								idleSprite.getY() + dY/2 );
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
		
		if(key == "up"){
			inputUp = active;
		}
		
		if(key == "down"){
			inputDown = active;
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
	
	public void beginWallContact(Entity ent, Contact cnt){
		Fixture fixA = cnt.getFixtureA();
		Fixture fixB = cnt.getFixtureB();
		Fixture myFix;
		
		//Find which fixture is ours...
		if(fixA.getBody().getUserData() instanceof PlayerEntity ){
			myFix = fixA;
		}else{
			myFix = fixB;
		}
		
		if(ent.type() == EntityType.Wall){
			Vector2 normal = cnt.getWorldManifold().getNormal();
			
			//If the y direction is 1 (up) it is the ground.
			if(normal.y == 1f){
				onGround(ent);
				onWall(null, null); //Reset onWall because we dont want to be on the wall and on the ground at the same time
			//If the y direction of the normal is 0 that means it is a wall.
			}else if(normal.y == 0 && !onGround()){
				onWall(ent, normal);
			}
		}
	}
	
	public void endWallContact(Entity ent, Contact cnt){
		if(ent.type() == EntityType.Wall){
			if(ent == onWall){
				onWall(null, null);
			}
			
			if(ent == onGround){
				onGround(null);
			}
		}
	}
	
	public boolean onWall(){
		return (onWall != null);
	}
	
	public boolean onGround(){
		return (onGround != null);
	}
	
	//Initialize the player's collision body
	public void createPhysics(){
		// Create a polygon shape
		PolygonShape playerBox = new PolygonShape();  
		// (setAsBox takes half-width and half-height as arguments)
		playerBox.setAsBox(width * 0.5f / PPM, height * 0.5f / PPM);
		
		// Create a fixture definition 
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = playerBox;
		fixtureDef.density = 0.1f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0f; // Make it bounce a little bit
		fixtureDef.filter.categoryBits = type();
		fixtureDef.filter.maskBits = (short) (type() | EntityType.Wall);
		
		Vector2 pos = new Vector2(10, 2);
		
		createDynamicBody(this, pos, fixtureDef);
		body().setFixedRotation(true);
		body().setUserData(this);
		
		playerBox.dispose();
		
		float			plhx	= (float)width()/2 / PPM;
		
		// Create a polygon shape
		CircleShape armShape = new CircleShape();  
			armShape.setRadius(width/2 / PPM + plhx);
			armShape.setPosition(new Vector2(0, (height() / 2 * .66f) / PPM));
		
		FixtureDef armFixture = new FixtureDef();
		armFixture.shape = armShape;
		armFixture.isSensor = true;
		//body().createFixture(armFixture);

		//gostArms = new ArmGhost(game);
		//gostArms.createPhysics(width/2 / PPM);
	}
	
	//Reset move state
	private void resetMoveState(){
		inputLeft 	= false;
		inputRight 	= false;
		inputJump 	= false;
	}
	
	private void onWall(Entity ent, Vector2 norm){		
		onWall = ent;
		wallNorm = norm;
		
		if(wallNorm != null){
			wallNorm.x = Math.round(wallNorm.x);
			wallNorm.y = Math.round(wallNorm.y);
		}
	}
	
	private void onGround(Entity ent){
		onGround = ent;
	}
	
	
	private Vector2 calculateMoveForce(){
		Vector2 force = new Vector2();
		
		if(onGround()){
			force.x = groundForce;
		}else{
			force.x = airForce;
		}
		
		return force;
	}
	
	private void renderDbg(SpriteBatch b){		
		game.font.draw(b, "onWall: " + onWall(), 0 + game.camera().position.x/2, 0);
		game.font.draw(b, "onGround: " + onGround(), 0 + game.camera().position.x/2, -15);
		game.font.draw(b, "stuckToWall: " + stuckToWall, 0 + game.camera().position.x/2, -30);
		
		String wallNorm_str;
		if(wallNorm == null){
			wallNorm_str = "null";
		}else{
			wallNorm_str = wallNorm.x + ", " + wallNorm.y;
		}
		game.font.draw(b, "wallNorm: " + wallNorm_str, 100 + game.camera().position.x/2, 0);
	}

	public int width() { return width; }	
	public int width(int width) { return width; }	
	public int height(){ return height; }
	public int height(int height) { return height; }	
}
