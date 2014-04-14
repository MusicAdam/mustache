package com.gearworks.mos.state;

import com.gearworks.mos.Client;

public interface State {
	public void render(Client game);
	public void update(Client game);
	public void onEnter(Client game);
	public void onExit(Client game);
	public boolean canEnterState(Client game);
	public boolean canExitState(Client game);
	public int getId();
}
