package com.gearworks.mos.game.entities;
import static com.gearworks.mos.Box2DVars.PPM;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Transform;
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
	private Vector2 thrusterPosition;				//Local position of the engine
	private float cruiseSpeedLimit = .8f;			//Will try to hover around this speed
	private float turnSpeed = .5f;					//Impulse used to turn the ship
	private float engineMag = .2f;					//Magnitude of engine force
	private Vector2 lastPosition;					//The position last step used for speed calculation
	private float currentSpeed;
	private int timeIterations;
	private Vector2 dirToDest;
	private float angleEpsilon = .1745f;//.0349f;			//The angle in radians at which the ship will snap to the destination angle
	
	public Ship(Client cRef) {
		super(EntityType.Ship, cRef);
		
		timeIterations = 0;
		lastPosition = new Vector2();
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
		//Update speed = d/t
		currentSpeed = body().getPosition().sub(lastPosition).len() / Client.STEP;
		
		
		//Set the direction to destination
		if(destination() != null){
			dirToDest = new Vector2(	destination().x - position().x,
										destination().y - position().y);
			dirToDest.nor();
		}
		
		Vector2 fxy = new Vector2();	//Force applied this step
		Vector2 ixy = new Vector2(); 	//Impulse applied this step
		
		if(currentSpeed < cruiseSpeedLimit)
			fxy = direction.cpy().scl(new Vector2(engineMag, engineMag));
		
		if(dirToDest() != null){
			float dAngle = Utils.angle(direction, dirToDest());
			
			if( dAngle > Utils.PI_OVER_2 )
				dAngle = Utils.PI_OVER_2 - dAngle;
			System.out.println(dAngle + " > " + angleEpsilon);
			if(Math.abs(body().getAngularVelocity()) < turnSpeed && dAngle > angleEpsilon){
				//turnToDestination(ixy, dAngle);
			}else if(dAngle <= angleEpsilon && dAngle != 0){
				zeroAngularVelocity();
				//snapToDestinationDirection();
			}else{
				thrustDir = new Vector2();
				
				//if(destination() != null && !Utils.isWithin(position(), destination(), .5f)){
					//body().applyForce(fxy, body().getPosition(), true);
				//}else if(destination() != null){
				//	body().applyForce(body().getLinearVelocity().scl(-1), body().getPosition(), false);
				//	destination(null);
				//}
			}
		}
		lastPosition = body().getPosition().cpy();
		
		

		body().applyLinearImpulse(thrustDir.cpy().scl(.01f), thrusterPosition, true);
	}
	
	//Sets angular velocity to turn to the desired location
	private void turnToDestination(Vector2 ixy, float dAngle){
		
	}
	
	//Sets the body to face the direction exactly
	private void snapToDestinationDirection(){
		Vector2 dir;
		if( (dir = dirToDest()) != null){
			float angle = 	Utils.angle(direction, dir);
			
			Transform t = body().getTransform();
			t.setRotation(body().getAngle() + angle);
			body().setTransform(t.getPosition(), t.getRotation());
		}
	}
	
	private void zeroAngularVelocity(){
		body().setAngularVelocity(0);
		thrustDir = new Vector2();
		System.out.println("Zero angular velocity");
	}
	
	public Vector2 dirToDest(){
		if(destination == null)
			dirToDest = null;
		return dirToDest;
	}
	public void destination(Vector2 d){ destination = d; }
	public Vector2 destination(){ return destination; }
	public Vector2 direction(){ return direction; }
	public Vector2 thrustDirection(){ return thrustDir; }

	public float currentSpeed() {
		return currentSpeed;
	}

	public Vector2 thrusterPosition() {
		return thrusterPosition;
	}
}
