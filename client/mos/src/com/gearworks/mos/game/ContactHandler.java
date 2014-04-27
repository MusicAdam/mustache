package com.gearworks.mos.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.gearworks.mos.game.entities.PlayerEntity;

public class ContactHandler implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();

		if( (fixA.getFilterData().categoryBits & fixB.getFilterData().maskBits) != 0 ){
			if(fixA.getBody().getUserData() instanceof Entity && fixB.getBody().getUserData() instanceof Entity){
				sendContact(true, (Entity)fixA.getBody().getUserData(), (Entity)fixB.getBody().getUserData(), contact);
				sendContact(true, (Entity)fixB.getBody().getUserData(), (Entity)fixA.getBody().getUserData(), contact);
			}
		}else if( (fixB.getFilterData().categoryBits & fixA.getFilterData().maskBits) != 0 ){
			if(fixA.getBody().getUserData() instanceof Entity && fixB.getBody().getUserData() instanceof Entity){
				sendContact(true, (Entity)fixA.getBody().getUserData(), (Entity)fixB.getBody().getUserData(), contact);
				sendContact(true, (Entity)fixB.getBody().getUserData(), (Entity)fixA.getBody().getUserData(), contact);
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();

		if( (fixA.getFilterData().categoryBits & fixB.getFilterData().maskBits) != 0 ){
			if(fixA.getBody().getUserData() instanceof Entity && fixB.getBody().getUserData() instanceof Entity){
				sendContact(false, (Entity)fixA.getBody().getUserData(), (Entity)fixB.getBody().getUserData(), contact);
				sendContact(false, (Entity)fixB.getBody().getUserData(), (Entity)fixA.getBody().getUserData(), contact);
			}
		}else if( (fixB.getFilterData().categoryBits & fixA.getFilterData().maskBits) != 0 ){
			if(fixA.getBody().getUserData() instanceof Entity && fixB.getBody().getUserData() instanceof Entity){
				sendContact(false, (Entity)fixA.getBody().getUserData(), (Entity)fixB.getBody().getUserData(), contact);
				sendContact(false, (Entity)fixB.getBody().getUserData(), (Entity)fixA.getBody().getUserData(), contact);
			}
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}
	
	public void sendContact(boolean begin, Entity entA, Entity entB, Contact contact){
		//Handle player collisions
		if(entA.type() == EntityType.Player){
			PlayerEntity pl = (PlayerEntity)entA;
			
			if(entB.type() == EntityType.Wall){
				if(begin){
					pl.beginWallContact(entB, contact);
				}else{
					pl.endWallContact(entB, contact);
					
				}
			}
		}
	}

}
