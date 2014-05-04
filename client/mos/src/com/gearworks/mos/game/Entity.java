package com.gearworks.mos.game;

import static com.gearworks.mos.Box2DVars.PPM;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gearworks.mos.Client;
import com.gearworks.mos.Utils;

//Should be shared 
public class Entity {	
	private Body body;
	private short type;
	protected Client game;
	private BoundingBox aabb;
	private Vector2 size;	
	
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
	public static Entity createStaticBody(Entity ent, Vector2 pos, Shape shape, short categoryBits, short maskBits) {
		//Create body def
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(pos.x , pos.y);
		
		//Create body
		ent.body = ent.game.world().createBody(bodyDef);
		
		//Create Fixture
		Fixture fix = ent.body.createFixture(shape, 0.0f);
		fix.getFilterData().categoryBits = categoryBits;
		fix.getFilterData().maskBits = maskBits;
		
		return ent;
	}
	
	public Entity(short type, Client cRef){
		game = cRef;
		this.type = type;
	}
	
	public Body body() {
		return body;
	}

	public Vector2 position() {
		if(body == null) return null;
		
		return body().getPosition().scl(PPM);
	}
	
	public void updateBoundingBox(){
		aabb = Utils.calculateBoundingBox(this);
	}
	
	public void render(SpriteBatch batch){}
	public void update(){}
	
	public void dispose(){}
	
	public void beginContact(Entity ent, Contact contact){}
	public void endContact(Entity ent, Contact contact){}
	
	public BoundingBox aabb(){ return aabb; }
	public short type() { return type; }
	public Vector2 size(){ return size; }
	public void size(Vector2 s){ size = s; }
	public float width(){ return size.x; }
	public void width(float w){ size.x = w; }
	public float height(){ return size.y; }
	public void height(float h){ size.y = h; }
}
