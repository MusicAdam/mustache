package com.gearworks.mos.game.ships;
import static com.gearworks.mos.Box2DVars.PPM;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.gearworks.mos.Client;
import com.gearworks.mos.Utils;
import com.gearworks.mos.game.Entity;
import com.gearworks.mos.game.EntityType;
import com.gearworks.mos.game.Level;

//TODO: Implement waypoints.
public class Ship extends Entity{
	private Vector2 direction = new Vector2(0, 1);	//Direction of the hull fixture.
	private Vector2 thrustDir = new Vector2();		//Direction of the thrust impulse applied to the enginePosition
	private Vector2 destination;					//Destination of the vessel
	private Vector2 thrusterPosition;				//Where thrusters should apply impulse
	private float cruiseSpeedLimit = .1f;			//Will try to hover around this speed
	private float turnSpeed = 200;					//Will try to be turned to destination in this amount of time.
	private float engineMag = .6f;					//Magnitude of engine force
	
	public Ship(Client cRef) {
		super(EntityType.Ship, cRef);
		
		size(new Vector2(10, 20));
	}
	
	public void createPhysics(){
		float width = width();
		float height= height();
		float deg = (float)(2 * Math.PI) / 180f;
		float division = 36;
		float xRadius = width;
		float yRadius = height;
		
		////////////
		//HULL
		
		PolygonShape hullShape = new PolygonShape();  
		hullShape.setAsBox(width * 0.5f / PPM, height * 0.5f / PPM);
		
		FixtureDef hullDef = new FixtureDef();
		hullDef.shape 		= hullShape;
		hullDef.density 	= 1000f;
		hullDef.restitution = 0f;
		hullDef.filter.categoryBits	= EntityType.Hull;
		hullDef.filter.maskBits		= EntityType.Hull | EntityType.Shield | EntityType.Wall;
		
		Entity.createDynamicBody(this, new Vector2(100 / PPM, 100 / PPM), hullDef);
		hullShape.dispose();
		
		////////////
		//SHIELD
		ChainShape shieldShape = new ChainShape();
		ArrayList<Vector2> vertices = new ArrayList<Vector2>(); 
		
		for(int idx = 0; idx < division; idx++){
			float angle = ( ( (float)Math.PI * 2 ) / division ) * idx;
			float xPos, yPos;
			
			xPos = xRadius * (float)Math.cos(angle);
			yPos = yRadius * (float)Math.sin(angle);
			vertices.add(new Vector2(xPos / PPM, yPos / PPM));
		}
		
		vertices.add(vertices.get(0));
		Vector2[] vertexArray = vertices.toArray(new Vector2[vertices.size()]);
		shieldShape.createChain(vertexArray);
		
		FixtureDef shieldDef = new FixtureDef();
		shieldDef.shape = shieldShape;
		body().createFixture(shieldDef);

		thrusterPosition = new Vector2(body().getPosition().x, body().getPosition().y - (height()/PPM) / 2);
		updateBoundingBox();
	}
	
	@Override
	public void update(){
		//Update direction
		direction.x = 0;
		direction.y = 1;
		direction.rotateRad(body().getAngle());	
		direction.nor();
		
		
		//Set the direction to destination
		Vector2 dirToDest = new Vector2();
		if(destination() != null){
			dirToDest = new Vector2(	destination().x - position().x,
								destination().y - position().y);
			dirToDest.nor();
		}
		
		float wHeight = height() / PPM;
		Vector2 curVel = body().getLinearVelocity();
				
		float fx = engineMag;
		float fy = engineMag;
		Vector2 fxy = direction.cpy().scl(new Vector2(engineMag, engineMag));
		
		if(curVel.x > fxy.x/2){
			fxy.x *= 0;
		}
		
		if(curVel.y > fxy.y/2){
			fxy.y *= 0;
		}
		
		//System.out.println(Utils.isWithin(direction, dir, 2f));
		/*if(Math.abs(body().getAngularVelocity()) < .2f && !Utils.isWithin(direction, dir, .3f)){
			System.out.println("Turn");
			thrustDir = direction.cpy().sub(dir);
			thrustDir.nor();
		}else if(Math.abs(body().getAngularVelocity()) != 0 && Utils.isWithin(direction, dir, .01f)){
			body().setAngularVelocity(0);
			thrustDir = new Vector2();
		}else{
			thrustDir = new Vector2();				
		}
		*/
		//thrustDir.nor();
		System.out.println(direction);
		body().applyForce(fxy, body().getPosition(), true);
		//body().applyLinearImpulse(thrustDir.cpy().scl(.01f), enginePos, true);
	}
	
	public void destination(Vector2 d){ destination = d; }
	public Vector2 destination(){ return destination; }
	public Vector2 direction(){ return direction; }
	public Vector2 thrustDirection(){ return thrustDir; }
}
