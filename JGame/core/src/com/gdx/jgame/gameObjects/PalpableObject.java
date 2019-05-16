package com.gdx.jgame.gameObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gdx.jgame.Camera;
import com.gdx.jgame.world.MapManager;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

/*
 * React with other PalpableObject.
 * Create as body with fixture.
 */

public class PalpableObject {
	private Sprite defaultSprite;
	
	private World world;
	private Body body;	// get access to give forces to body
	private Fixture m_fixture;
	private float m_scale;

	public PalpableObject(PalpableObjectPolygonDef objectDef) {
		world = objectDef.world;
		m_scale = objectDef.scale;
		
		body = world.createBody(objectDef.bodyDef);
		m_fixture = body.createFixture(objectDef.fixtureDef); 
		
		setTexture(objectDef.texture);
	}	

	private void setTexture(Texture defaultTexture) {
		defaultSprite = new Sprite(defaultTexture);		
		defaultSprite.setBounds(0, 0, defaultSprite.getWidth() * m_scale / MapManager.PIXELS_PER_METER, 
				defaultSprite.getHeight() * m_scale / MapManager.PIXELS_PER_METER);
		defaultSprite.setOriginCenter();
	}
	
	public static boolean overlaps(Polygon polygon, Rectangle rectangle) {
	    Polygon rPoly = new Polygon(new float[] { 0, 0, rectangle.width, 0, rectangle.width,
	            rectangle.height, 0, rectangle.height });
	    
	    rPoly.setPosition(rectangle.x, rectangle.y);
	    if (Intersector.overlapConvexPolygons(rPoly, polygon))
	        return true;
	    return false;
	}
	
	public void render(SpriteBatch batch) {
		defaultSprite.setRotation(body.getAngle()*(float)(180/Math.PI));
		defaultSprite.setPosition(m_fixture.getBody().getPosition().x - defaultSprite.getWidth()/2, 
				m_fixture.getBody().getPosition().y - defaultSprite.getHeight() / 2);
		defaultSprite.draw(batch);
	}
	
	public void rotate(float angle) {
		body.setTransform(body.getWorldCenter(), angle);
	}
	
	public Vector2 getPositionOnScreen(Camera camera) {
		//to be more transparent
		if(camera.follower != this) {
			throw new IllegalArgumentException("Camera is do not follow this object");
		}
		return camera.getPositionOnScreen();
	}
	
	public float angleRadOnScreen(Camera camera, Vector2 position) {
		Vector2 tmp = new Vector2(position);
		
		tmp.x = tmp.x - getPositionOnScreen(camera).x;
		tmp.y = tmp.y - getPositionOnScreen(camera).y;
		//System.out.println(position + " " + tmp);
		
		return tmp.angleRad() - ((float) Math.PI/2);
	}

	public World getWorld() {
		return world;
	}
	
	public Body getBody() {
		return body;
	}
	
	public Sprite getDefaultSprite() {
		return defaultSprite;
	}
}
