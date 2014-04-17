package com.gearworks.mos.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gearworks.mos.Client;

//Should be shared 
public class Entity {
	private Body body;
	protected Client game;
	
	//Make sure to dispose of the fixture definition's shape 
	public static Body createDynamicBody(Entity ent, Vector2 pos, FixtureDef fixDef) {
		//Create body def
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(pos.x, pos.y);
		
		//Create body
		ent.body = ent.game.world().createBody(bodyDef);
		
		//Create Fixture
		ent.body.createFixture(fixDef);
		
		return ent.body;
	}
	
	//Make sure to dispose of the fixture definition's shape 
	public static Body createStaticBody(Entity ent, Vector2 pos, Shape shape) {
		//Create body def
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(pos.x , pos.y);
		
		//Create body
		ent.body = ent.game.world().createBody(bodyDef);
		
		//Create Fixture
		ent.body.createFixture(shape, 0.0f);
		
		return ent.body;
	}
	
	public Entity(Client cRef){
		game = cRef;
	}
	
	public Body body() {
		return body;
	}

	public Vector2 position() {
		if(body == null) return null;
		
		return null;
	}
	
}
