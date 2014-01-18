package gamedev.quests;

import gamedev.game.ResourcesManager;
import gamedev.objects.Inventory;
import gamedev.objects.Wood;
import gamedev.scenes.GameMapScene;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;

import android.widget.Toast;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class QuestBuildBridge extends Quest {

	private final static float RECTANGLE_X = 37 * 32;
	private final static float RECTANGLE_Y = 14 * 32;
	private final static float RECTANGLE_HEIGHT = 100;
	private final static float RECTANGLE_WIDTH = 20;
	private final static int N_WOOD = 5;
	
	protected Body body;
	protected Rectangle rectangle;
	protected Wood[] woods = new Wood[N_WOOD];
	protected Wood wood1;
	protected Wood wood2;
	protected Wood wood3;
	protected Sprite bridge;
	
	public QuestBuildBridge(GameMapScene map) {
		super(map);
		this.title = "Cross the River";
		this.description = "I need to find something so I can go to the other side of the river!";

		ResourcesManager res = ResourcesManager.getInstance();
		this.rectangle = new Rectangle(RECTANGLE_X, RECTANGLE_Y,
				RECTANGLE_WIDTH, RECTANGLE_HEIGHT, res.vbom);
		final FixtureDef boxFixtureDef = PhysicsFactory.createFixtureDef(0, 0,
				0);
		this.body = PhysicsFactory.createBoxBody(res.physicsWorld,
				this.rectangle, BodyType.StaticBody, boxFixtureDef);
		res.physicsWorld.registerPhysicsConnector(new PhysicsConnector(
				rectangle, body, false, false));
		map.attachChild(rectangle);
		
		this.woods[0] = new Wood(250, 250);
		this.woods[1] = new Wood(250, 600);
		this.woods[2] = new Wood(300, 700);
		this.woods[3] = new Wood(500, 900);
		this.woods[4] = new Wood(100, 1000);
		
		for (int i=0; i<N_WOOD; i++) {
			this.map.attachChild(this.woods[i]);
		}
		
		this.bridge = new Sprite(600, 600, ResourcesManager.getInstance().bridgeRegion, ResourcesManager.getInstance().vbom);
				
	}

	public void setActive(boolean active) {
		super.setActive(active);
		ResourcesManager.getInstance().activity.toastOnUIThread(
				this.description, Toast.LENGTH_SHORT);
	}

	@Override
	public void onFinish() {
		ResourcesManager.getInstance().removeSpriteAndBody(rectangle);
		this.map.attachChild(this.bridge);
	}

	@Override
	public String getStatus() {
		Inventory inventory = ResourcesManager.getInstance().avatar.getInventory();
		int count = 0;
		for (int i=0; i<N_WOOD; i++) {
			if (inventory.contains(this.woods[i])) count++;
		}
		return "I found " + Integer.toString(count) + "/" + N_WOOD +" wood... I need more!";
	}

	@Override
	public boolean isCompleted() {
		Inventory inventory = ResourcesManager.getInstance().avatar.getInventory();
		for (int i=0; i<N_WOOD; i++) {
			if (!inventory.contains(this.woods[i])) return false;
		}
		return true;
	}

	public Rectangle getRectangle() {
		return this.rectangle;
	}
	

}
