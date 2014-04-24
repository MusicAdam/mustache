package com.gearworks.mos.game.entities;

import static com.gearworks.mos.Box2DVars.PPM;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.gearworks.mos.Client;
import com.gearworks.mos.game.Entity;
import com.gearworks.mos.game.EntityType;

/* ArmGhost - Collision box to follow the player
 * 			  used to simulate a player only being 
 *			  grab where his arms can reach.
 */
public class ArmGhost extends Entity {
	private Joint joint;	
	
	//armLen: Length of arm in meters
	public ArmGhost(float armLen, Client cRef) {
		super(EntityType.Ghost, cRef);
		
		createPhysics(armLen);
	}
	
	public void createPhysics(float armLen){
		PlayerEntity	pl		= game.player();
		float			plhx	= (float)game.player().width() / PPM;
		
		// Create a polygon shape
		CircleShape ghostArms = new CircleShape();  
			ghostArms.setRadius(armLen + plhx);
		
		// Create a fixture definition 
		FixtureDef 	fixtureDef = new FixtureDef();
			fixtureDef.shape = ghostArms;
			fixtureDef.density = 0f; 
			fixtureDef.friction = 0f;
			fixtureDef.restitution = 0f; // Make it bounce a little bit
		
		//Create the body
		createDynamicBody(this, new Vector2(), fixtureDef);
		body().setFixedRotation(true);
		body().setUserData(this);
		
		
		//Join to player
		RevoluteJointDef 	joint = new RevoluteJointDef();
			joint.bodyA = body();
			joint.bodyB = pl.body();
			joint.collideConnected 	= false;
			joint.localAnchorA.set(0, (pl.height() / 2 * .66f) / PPM); //Arms are located like... at least 2/3 the way up your body?
			joint.localAnchorB.set(0, 0);
			joint.referenceAngle = 0f;
			joint.enableLimit = false;
			joint.enableMotor = false;
		this.joint = game.world().createJoint(joint);
		
		//Clean up
		ghostArms.dispose();
	}
}
