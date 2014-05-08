package com.gearworks.mos;


import static com.gearworks.mos.Box2DVars.PPM;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.gearworks.mos.game.Entity;
import com.gearworks.mos.game.entities.Ship;
import com.gearworks.mos.state.GameState;

public class UserInterface implements InputProcessor{
	private Client game;
	
	private ArrayList<Entity> selected;
	private Vector2 dragStart;
	private Vector2 dragPos;
	private ShapeRenderer renderer;
	
	float selectionPadding = 0f;
	
	//Takes a mouse coordinate and returns screen coordinates, does not alter original vector
	public static Vector2 mouseToScreen(Vector2 coord){
		Vector2 screenCoord = coord.cpy();
		screenCoord.x = coord.x;
		screenCoord.y = Math.abs(coord.y - (Client.V_HEIGHT * Client.SCALE));
		return screenCoord;
	}
	
	public UserInterface(Client game){
		this.game = game;
		renderer = new ShapeRenderer();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == 0){
			selected = null;
		}
		
		if(button == 1 && selected != null){
			for(Entity ent : selected){
				if(ent instanceof Ship){
					Ship ship = (Ship)ent;
					ship.destination(mouseToScreen(new Vector2(screenX, screenY)));
				}
			}
		}
		return true; //This could interfere with menus in the future, unless this class handles the clicks...
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(dragStart != null && dragPos != null){
			//If the height is negative we want to swap dragStart.y and dragPos.y to flip the box so it is not upside down and picking will work
			if(dragPos.y - dragStart.y < 0){
				float tmp = dragPos.y;
				dragPos.y = dragStart.y;
				dragStart.y = tmp;
			}
			
			//Do this for x axis as well
			if(dragPos.x - dragStart.x < 0){
				float tmp = dragPos.x;
				dragPos.x = dragStart.x;
				dragStart.x = tmp;
			}
			
			Rectangle bounds = new Rectangle(	dragStart.x,
												dragStart.y,
												dragPos.x - dragStart.x,
												dragPos.y - dragStart.y);
			selected = Utils.findEntitiesInBox(game.state().entities(), bounds);
			
			dragStart = null;
			dragPos = null;
		}
		return true; //This could interfere with menus in the future, unless this class handles the clicks...
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
			if(dragStart == null)
				dragStart = mouseToScreen(new Vector2(screenX, screenY));
			
			dragPos = mouseToScreen(new Vector2(screenX, screenY));
		}
		
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
	
	public void render(SpriteBatch batch){
		renderer.setProjectionMatrix(game.camera().combined);
		renderer.identity();
		
		//Draw the selection box
		if(dragStart != null && dragPos != null){
			float width = dragPos.x - dragStart.x;
			float height = dragPos.y - dragStart.y;

			Utils.drawRect(renderer, Color.RED, dragStart.x, dragStart.y, width, height);
		}
		
		
		//Draw box around selected entities
		if(selected != null){
			
			for(Entity ent : selected){
				BoundingBox aabb = ent.aabb();
				
				float width = aabb.max.x - aabb.min.x;
				float height= aabb.max.y - aabb.min.y;
				float x = aabb.max.x - width/2;
				float y = aabb.max.y - height/2;
				
				//Draw aabb
				Utils.drawRect(renderer, Color.GREEN, ent.position().x + x * PPM - selectionPadding / 2, ent.position().y + y * PPM - selectionPadding /2, width * PPM + selectionPadding, height * PPM + selectionPadding);

				if(ent instanceof Ship){
					//Draw the forces being applied to selected ships
					Ship ship = (Ship)ent;
					Vector2 v1 = ship.position();
					Vector2 v2 = ship.destination();
					if(v1 != null && v2 != null)
						Utils.drawLine(renderer,  Color.GREEN, v1.x, v1.y,  v2.x,  v2.y);
					v1 = ship.position();
					v2 = ship.direction().cpy().scl(ship.height()/2).add(v1);
					if(v1 != null && v2 != null)
						Utils.drawLine(renderer,  Color.CYAN, v1.x, v1.y,  v2.x,  v2.y);
					v1 = ship.thrusterPosition().cpy().add(ship.position());
					v2 = ship.thrustDirection().cpy().scl(10).add(v1);
					if(v1 != null && v2 != null)
						Utils.drawLine(renderer,  Color.ORANGE, v1.x, v1.y,  v2.x,  v2.y);
					//Draw UI Debug hints
					batch.begin();
						game.font.draw(batch, "Speed: " + ship.currentSpeed(), ship.position().x + ship.width()/2, ship.position().y + ship.height()/2);
					batch.end();
				}
			}
		}
	}

}
