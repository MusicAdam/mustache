package com.gearworks.mos;

import static com.gearworks.mos.Box2DVars.PPM;
import static com.gearworks.mos.Box2DVars.MPP;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.gearworks.mos.game.entities.PlayerEntity;
import com.gearworks.mos.state.GameState;
import com.gearworks.mos.state.StateManager;

public class Client implements ApplicationListener {
	public static final String TITLE = "Mostache - Client";
	public static final int V_WIDTH = 400;
	public static final	int V_HEIGHT = 400;
	public static final float ASPECT_RATIO = (float)V_WIDTH/(float)V_HEIGHT;
	public static final int SCALE = 2;
	
	public static final float STEP = 1 / 60f;
	private float accum;
	
	public BitmapFont font;
	
	protected StateManager sm;
	
	private Rectangle viewport;
	private boolean updateViewport;
	private World world;
	private OrthographicCamera camera;
	private Box2DDebugRenderer dbgRenderer;
	private PlayerEntity player;
	private FPSLogger fpsLogger;
	private InputMultiplexer inputMultiplexer;
	
	@Override
	public void create() {	
		fpsLogger = new FPSLogger();

		inputMultiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		//Camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, V_WIDTH, V_HEIGHT);
		//camera.zoom = MPP;
		updateViewport = false;
		
		//Box2d
		world = new World(new Vector2(0, -10f), true);
		dbgRenderer = new Box2DDebugRenderer();
		
		//State Manager
		sm = new StateManager(this);
		sm.setState(new GameState());
		
		
		font = new BitmapFont();
		font.setColor(Color.BLACK);
	}

	@Override
	public void dispose() {
		world.dispose();
	}

	@Override
	public void render() {
		
		//Update viewport
		if(updateViewport){
			updateViewport = false;
			
	        Gdx.gl.glViewport((int) viewport.x, (int) viewport.y,
                    (int) viewport.width, (int) viewport.height);
	        
	        camera.viewportWidth = viewport.width;
	        camera.viewportHeight = viewport.height;
	        System.out.println("UPDATE VIEWPORT");
		}
		

		//Render
		Gdx.gl.glClearColor(.21f, .21f, .21f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		accum += Gdx.graphics.getDeltaTime();
		while(accum >= STEP) {
			accum -= STEP;
			
			sm.update();
			camera.update();
			
			//Step
			world.step(1/60f, 6, 2);
		}
		

		sm.render();
		Matrix4 dbgMatrix = camera.combined.cpy().scl(PPM);
		dbgRenderer.render(world, dbgMatrix);
		
		
		
		
		//fpsLogger.log();
	}

	@Override
	public void resize(int width, int height) {
		float aspectRatio = (float)width/(float)height;
		float scale = 1f;
		Vector2 crop = new Vector2(0f, 0f);
		
		if(aspectRatio > ASPECT_RATIO){
			scale = (float)height/(float)V_HEIGHT;
			crop.x = (width - V_WIDTH*scale)/2f;
		}else if(aspectRatio < ASPECT_RATIO){
			scale = (float)width/(float)V_WIDTH;
			crop.y = (height - V_HEIGHT*scale)/2f;
		}else{
			scale = (float)width/(float)V_WIDTH;
		}
		
		float w = (float)V_WIDTH*scale;
		float h = (float)V_HEIGHT*scale;
		
		viewport = new Rectangle(crop.x, crop.y, w, h);
		updateViewport = true;
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	
	public OrthographicCamera camera(){ return camera; }
	public World world(){ return world; }
	
	//singleton player 
	public PlayerEntity player(){ 
		if(player == null){
			player = new PlayerEntity(this);
			inputMultiplexer.addProcessor(player);
		}
		
		return player;
	}
}
