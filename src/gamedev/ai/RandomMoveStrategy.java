package gamedev.ai;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;

import gamedev.objects.AnimatedObject;
import gamedev.objects.AnimatedObject.GameState;

public class RandomMoveStrategy extends MoveStrategy {
	
	/** min Distance in meters to walk */
	protected float minDistance;
	
	/** max Distance in meters to walk */
	protected float maxDistance;
	
	/** Time in seconds to wait between walking to the next point */
	protected float waitBetweenTime = 0;
	
	/** Time needed to walk to goal position */
	protected float duration = 0;
	
	/** Set to true if a point is reached */
	protected boolean reachedGoal = false;
	
	protected float minX;
	protected float maxX;
	protected float minY;
	protected float maxY;
	
	protected boolean isBounded = false;
	
	public RandomMoveStrategy(AnimatedObject object, float minDistance, float maxDistance) {
		super(object);
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
		this.init();
	}

	public RandomMoveStrategy(AnimatedObject object, float minDistance, float maxDistance, float waitBetweenTime) {
		super(object);
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
		this.waitBetweenTime = waitBetweenTime;
		this.init();
	}

	public RandomMoveStrategy(AnimatedObject object, float minDistance, float maxDistance, float waitBetweenTime, float minX, float maxX, float minY, float maxY) {
		super(object);
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
		this.waitBetweenTime = waitBetweenTime;
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		this.isBounded = true;
		this.init();
	}
	
	protected void init() {
		Vector2 rPoint = this.getRandomPoint();
		Vector2 bodyPos = this.object.getBody().getPosition();
		this.object.moveTo(rPoint, GameState.WALKING);
		this.duration = calculateDuration(bodyPos, rPoint, this.object.getBody().getLinearVelocity());
	}
	
	protected Vector2 getRandomPoint() {
		Random r = new Random();
		Vector2 bodyPos = this.object.getBody().getPosition();
		float x = bodyPos.x + this.minDistance + (r.nextFloat() * (this.maxDistance - this.minDistance));
		float y = bodyPos.y + this.minDistance + (r.nextFloat() * (this.maxDistance - this.minDistance));
		if (r.nextInt(2) == 0) x = -x;
		if (r.nextInt(2) == 0) y = -y;
		if (this.isBounded) {
			x = Math.min(x, maxX);
			x = Math.max(x, minX);
			y = Math.min(y, maxY);
			y = Math.max(y, minY);			
		}
		return new Vector2(bodyPos.x + x, bodyPos.y + y);
	}
	
	
	@Override
	public boolean update(float time) {
		this.time += time;
		if (this.reachedGoal) {
			// We are waiting till the seconds are passed...
			if (this.time > this.waitBetweenTime) {
				this.reachedGoal = false;
				this.init();
			}
		} else {
			// Still walking...
			if (this.time >= this.duration) {
				// Reached goal. Check if we should wait some seconds...
				this.time = 0;
				if (this.waitBetweenTime > 0) {
					this.reachedGoal = true;
					this.object.setState(GameState.LOOKING, -1);
				} else {
					this.init();
				}
			}
		}
		return true;
	}

}
