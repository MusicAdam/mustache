package com.gearworks.mos.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gearworks.mos.Client;

//Should be shared 
public class Entity {
	private Body body;
	private EntityType type;
	protected Client game;
	
	//Make sure to dispose of the fixture definition's shape 
	//Creates a dynamic body and returns the associated entity
	public static Entity createDynamicBody(Entity ent, Vector2 pos, FixtureDef fixDef) {
		//Create body def
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(pos.x, pos.y);
		
		//Create body
		ent.body = ent.game.world().createBody(bodyDef);
		
		//Create Fixture
		ent.body.createFixture(fixDef);
		
		return ent;
	}
	
	//Make sure to dispose of the fixture definition's shape 
	//Creates a static body and returns the associated entity
	public static Entity createStaticBody(Entity ent, Vector2 pos, Shape shape) {
		//Create body def
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(pos.x , pos.y);
		
		//Create body
		ent.body = ent.game.world().createBody(bodyDef);
		
		//Create Fixture
		ent.body.createFixture(shape, 0.0f);
		
		return ent;
	}
	
	public Entity(EntityType type, Client cRef){
		game = cRef;
		this.type = type;
	}
	
	public Body body() {
		return body;
	}

	public Vector2 position() {
		if(body == null) return null;
		
		return null;
	}
	
	public void render(SpriteBatch batch){}
	public void update(){}
	
	public void dispose(){}
	
	public void beginContact(Entity ent, Contact contact){}
	public void endContact(Entity ent, Contact contact){}

	public EntityType type() {
		return type;
	}
}
