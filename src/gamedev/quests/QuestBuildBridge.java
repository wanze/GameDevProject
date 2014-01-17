package gamedev.quests;

import gamedev.game.ResourcesManager;
import gamedev.objects.Inventory;
import gamedev.objects.Wood;
import gamedev.scenes.GameMapScene;

import org.andengine.entity.primitive.Rectangle;
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

	protected Body body;
	protected Rectangle rectangle;
	protected Wood wood1;
	protected Wood wood2;
	protected Wood wood3;

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

		this.wood1 = new Wood(200, 200);
		this.wood2 = new Wood(400, 600);
		this.wood3 = new Wood(500, 500);
		map.attachChild(wood1);
		map.attachChild(wood2);
		map.attachChild(wood3);
	}

	public void setActive(boolean active) {
		super.setActive(active);
		ResourcesManager.getInstance().activity.toastOnUIThread(
				this.description, Toast.LENGTH_SHORT);
	}

	@Override
	public void onFinish() {
		ResourcesManager.getInstance().removeSpriteAndBody(rectangle);
		this.setFinished(true);
	}

	@Override
	public String getStatus() {
		Inventory inventory = ResourcesManager.getInstance().avatar
				.getInventory();
		int count = 0;
		if (inventory.contains(wood1))
			count++;
		if (inventory.contains(wood2))
			count++;
		if (inventory.contains(wood3))
			count++;
		return "I found " + Integer.toString(count) + "/3 wood... I need more!";
	}

	@Override
	public boolean isCompleted() {
		Inventory inventory = ResourcesManager.getInstance().avatar
				.getInventory();
		return (inventory.contains(wood1) && inventory.contains(wood2) && inventory
				.contains(wood3));
	}

	public Rectangle getRectangle() {
		return this.rectangle;
	}

}
