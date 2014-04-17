package com.gearworks.mos.state;

import com.gearworks.mos.Client;

public class StateManager {
	protected State state;
	protected Client clientRef;
	
	//Compares state id's
	public static boolean statesEqual(State state, State cState){
		if(state == null || cState == null) return false;
		if(state.getId() == cState.getId()){
			return true;
		}
		
		return false;
	}
	
	public StateManager(Client clientRef){
		this.clientRef = clientRef;
	}
	
	public void update(){
		if(state != null)
			state.update(clientRef);
	}
	
	public void render(){
		if(state != null)
			state.render(clientRef);
	}
	
	public boolean setState(State toState){
		//Don't change states if they are the same
		if(statesEqual(state, toState)) return false;
		
		if(state == null){
			state = toState;
		}else{
			state.onExit(clientRef);
			state = toState;
		}
		
		state.onEnter(clientRef);
		
		return true;
	}
	
}
