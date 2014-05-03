package com.gearworks.mos;

import static com.gearworks.mos.Box2DVars.PPM;
import java.util.ArrayList;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.gearworks.mos.game.Entity;

public class Utils {
	
	//TODO: Update this to check for overlapping entity bounds rather than entity point position in bounds
	public static ArrayList<Entity> findEntitiesInBox(ArrayList<Entity> haystack, Rectangle bounds){
		ArrayList<Entity> found = new ArrayList<Entity>();
		for(Entity e : haystack){
			float entX 		= e.position().x;
			float entY 		= e.position().y;
			float bndX 		= bounds.getX();
			float bndY 		= bounds.getY();
			float bndXMax 	= bndX + bounds.getWidth();
			float bndYMax 	= bndY + bounds.getHeight();
			
			if(entX <= bndXMax 	&& 
			   entX >= bndX		&&
			   entY <= bndYMax	&&
			   entY >= bndY)
			{
				found.add(e);
			}
		}
		
		return found;
	}
	
	/*
	//Calculates an aabb in world coords
	public static BoundingBox calculateBoundingBox(Entity ent){
		float entX = ent.body().getPosition().x;
		float entY = ent.body().getPosition().y;
		
		BoundingBox aabb = new BoundingBox( new Vector3(entX, entY, 0f), 
											new Vector3(entX, entY, 0f));
		for(Fixture fix : ent.body().getFixtureList()){
			Shape shape = fix.getShape();
			Vector2 min = new Vector2(entX, entY);
			Vector2 max = new Vector2(entX, entY);
			
			if(shape instanceof CircleShape){
				min = new Vector2(-shape.getRadius() + entX, -shape.getRadius() + entY);
				max = new Vector2(shape.getRadius() + entX, shape.getRadius() + entY);
				//TODO: The method for determining the min/max of PolygonShape is the same as ChainShape, should try to find a way to combine the two
			}else if(shape instanceof PolygonShape){
				PolygonShape pgon = (PolygonShape)shape;
				for(int i = 0; i < pgon.getVertexCount(); i++){
					Vector2 vert = new Vector2();
					pgon.getVertex(i, vert);
					
					//Transform to world position
					vert.x = vert.x + entX;
					vert.y = vert.y + entY;
					
					if(vert.x < min.x){
						min.x = vert.x;
					}else if(vert.x > max.x){
						max.x = vert.x;
					}
					
					if(vert.y < min.y){
						min.y = vert.y;
					}else if(vert.y > max.y){
						max.y = vert.y;
					}
				}
			}else if(shape instanceof ChainShape){
				ChainShape chain = (ChainShape)shape;
				for(int i = 0; i < chain.getVertexCount(); i++){
					Vector2 vert = new Vector2();
					chain.getVertex(i, vert);
					
					//Transform to world position
					vert.x = vert.x + entX;
					vert.y = vert.y + entY;
					
					if(vert.x < min.x){
						min.x = vert.x;
					}else if(vert.x > max.x){
						max.x = vert.x;
					}
					
					if(vert.y < min.y){
						min.y = vert.y;
					}else if(vert.y > max.y){
						max.y = vert.y;
					}
				}
				
			}else if(shape instanceof EdgeShape){
				EdgeShape edge = (EdgeShape)shape;
				Vector2 vert1 = new Vector2();
				Vector2 vert2 = new Vector2();
				

				//Transform to world position
				vert1.x = vert1.x + entX;
				vert1.y = vert1.y + entY;
				vert2.x = vert2.x + entX;
				vert2.y = vert2.y + entY;
				
				edge.getVertex1(vert1);
				edge.getVertex2(vert2);
				
				if(vert1.x < vert2.x){
					min.x = vert1.x;
					max.x = vert2.x;
				}else{
					min.x = vert2.x;
					max.x = vert1.x;
				}
				
				if(vert1.y < vert2.y){
					min.y = vert1.y;
					max.y = vert2.y;
				}else{
					min.y = vert2.y;
					max.y = vert1.y;
				}
			}
			
			BoundingBox thisBox = new BoundingBox(new Vector3(min.x, min.y, 0f), new Vector3(max.x, max.y, 0f));
			
			if(thisBox.min.x < aabb.min.x){
				aabb.min.x = thisBox.min.x;
			}
			
			if(thisBox.min.y < aabb.min.y){
				aabb.min.y = thisBox.min.y;
			}
			
			if(thisBox.max.x > aabb.max.x){
				aabb.max.x = thisBox.max.x;
			}
			
			if(thisBox.max.y > aabb.min.y){
				aabb.max.y = thisBox.max.y;
			}
		}
		
		//Calculate half size
		Vector3 hSize = new Vector3( (aabb.max.x - aabb.min.x) / 2, 
									 (aabb.max.y - aabb.min.y) / 2, 0f);
		
		aabb.min.sub(hSize);
		aabb.max.sub(hSize);
		
		return aabb;
	}
	*/
	
	//Should do rotations also
	public static BoundingBox calculateBoundingBox(Entity ent){
		BoundingBox aabb = new BoundingBox( new Vector3(), 
											new Vector3());
		for(Fixture fix : ent.body().getFixtureList()){
			Shape shape = fix.getShape();
			Vector2 min = new Vector2();
			Vector2 max = new Vector2();
			
			if(shape instanceof CircleShape){
				min = new Vector2(-shape.getRadius(), -shape.getRadius());
				max = new Vector2(shape.getRadius(), shape.getRadius());
				//TODO: The method for determining the min/max of PolygonShape is the same as ChainShape, should try to find a way to combine the two
			}else if(shape instanceof PolygonShape){
				PolygonShape pgon = (PolygonShape)shape;
				for(int i = 0; i < pgon.getVertexCount(); i++){
					Vector2 vert = new Vector2();
					pgon.getVertex(i, vert);
					
					//Transform to world position
					vert.x = vert.x;
					vert.y = vert.y;
					
					if(vert.x < min.x){
						min.x = vert.x;
					}else if(vert.x > max.x){
						max.x = vert.x;
					}
					
					if(vert.y < min.y){
						min.y = vert.y;
					}else if(vert.y > max.y){
						max.y = vert.y;
					}
				}
			}else if(shape instanceof ChainShape){
				ChainShape chain = (ChainShape)shape;
				for(int i = 0; i < chain.getVertexCount(); i++){
					Vector2 vert = new Vector2();
					chain.getVertex(i, vert);
					
					//Transform to world position
					vert.x = vert.x;
					vert.y = vert.y;
					
					if(vert.x < min.x){
						min.x = vert.x;
					}else if(vert.x > max.x){
						max.x = vert.x;
					}
					
					if(vert.y < min.y){
						min.y = vert.y;
					}else if(vert.y > max.y){
						max.y = vert.y;
					}
				}
				
			}else if(shape instanceof EdgeShape){
				EdgeShape edge = (EdgeShape)shape;
				Vector2 vert1 = new Vector2();
				Vector2 vert2 = new Vector2();
				

				//Transform to world position
				vert1.x = vert1.x;
				vert1.y = vert1.y;
				vert2.x = vert2.x;
				vert2.y = vert2.y;
				
				edge.getVertex1(vert1);
				edge.getVertex2(vert2);
				
				if(vert1.x < vert2.x){
					min.x = vert1.x;
					max.x = vert2.x;
				}else{
					min.x = vert2.x;
					max.x = vert1.x;
				}
				
				if(vert1.y < vert2.y){
					min.y = vert1.y;
					max.y = vert2.y;
				}else{
					min.y = vert2.y;
					max.y = vert1.y;
				}
			}
			
			BoundingBox thisBox = new BoundingBox(new Vector3(min.x, min.y, 0f), new Vector3(max.x, max.y, 0f));
			
			if(thisBox.min.x < aabb.min.x){
				aabb.min.x = thisBox.min.x;
			}
			
			if(thisBox.min.y < aabb.min.y){
				aabb.min.y = thisBox.min.y;
			}
			
			if(thisBox.max.x > aabb.max.x){
				aabb.max.x = thisBox.max.x;
			}
			
			if(thisBox.max.y > aabb.min.y){
				aabb.max.y = thisBox.max.y;
			}
		}
		
		//Calculate half size
		Vector3 hSize = new Vector3( (aabb.max.x - aabb.min.x ) / 2 , 
									 (aabb.max.y - aabb.min.y ) / 2, 0f);
		
		aabb.min.sub(hSize);
		aabb.max.sub(hSize);
		
		return aabb;
	}
	
	public static void drawRect(ShapeRenderer r, Color color, float x, float y, float w, float h){
		r.identity();
		r.begin(ShapeType.Line);
			r.setColor(color);
			r.translate(x, y, 0f);
			r.rect(0, 0, w, h);
		r.end();
	}
	
	public static void drawLine(ShapeRenderer r, Color color, float x1, float y1, float x2, float y2){
		r.identity();
		r.begin(ShapeType.Line);
			r.setColor(color);
			r.line(x1,  y1, x2, y2);
		r.end();
	}
	
	public static boolean isWithin(Vector2 v, Vector2 test, float e){
		//System.out.println(v + ", " + test);
		return (	v.x < test.x + e && v.x > test.x - e &&
					v.y < test.y + e && v.y > test.y - e );
	}
}
