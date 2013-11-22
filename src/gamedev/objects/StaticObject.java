package gamedev.objects;

import gamedev.game.ResourcesManager;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

abstract class StaticObject extends Sprite {
		
	protected ResourcesManager resourcesManager;
	protected Body body;
	protected String bodyUserData;
	
	public StaticObject(float pX, float pY, ITextureRegion textureRegion) {
		super(pX, pY, textureRegion, ResourcesManager.getInstance().vbom);
		this.resourcesManager = ResourcesManager.getInstance();
		this.bodyUserData = "StaticObject";
		this.createPhysics();
	}
	
	/**
	 * Each static object must define its body
	 */
	abstract protected void createPhysics();
	
}
