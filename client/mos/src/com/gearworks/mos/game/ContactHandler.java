package com.gearworks.mos.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class ContactHandler implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Entity entA = (Entity)contact.getFixtureA().getBody().getUserData();
		Entity entB = (Entity)contact.getFixtureB().getBody().getUserData();
		
		entA.beginContact(entB, contact);
		entB.beginContact(entA, contact);
	}

	@Override
	public void endContact(Contact contact) {
		Entity entA = (Entity)contact.getFixtureA().getBody().getUserData();
		Entity entB = (Entity)contact.getFixtureB().getBody().getUserData();
		
		entA.endContact(entB, contact);
		entB.endContact(entA, contact);
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}

}
