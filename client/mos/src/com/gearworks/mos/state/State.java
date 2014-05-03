package com.gearworks.mos.state;

import java.util.ArrayList;

import com.gearworks.mos.Client;
import com.gearworks.mos.game.Entity;

public interface State {
	public void render(Client game);
	public void update(Client game);
	public void onEnter(Client game);
	public void onExit(Client game);
	public boolean canEnterState(Client game);
	public boolean canExitState(Client game);
	public void deleteEntity(Entity ent);
	public void addEntity(Entity ent);
	public ArrayList<Entity> entities();
	public int getId();
}
