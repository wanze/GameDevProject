package gamedev.game;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.shape.Shape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.Constants;
import org.andengine.util.debug.Debug;
import org.andengine.util.math.MathUtils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import android.opengl.GLES20;
import android.view.Display;
import android.widget.Toast;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga
 *
 * @author Nicolas Gramlich
 * @since 13:58:48 - 19.07.2010
 */
public class Game extends SimpleBaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static int CAMERA_WIDTH = 600;
	private static int CAMERA_HEIGHT = 480;

	// ===========================================================
	// Fields
	// ===========================================================

	private BoundCamera mBoundChaseCamera;
	private AnimatedSprite player;
	// private Body mPlayerBody;

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mPlayerTextureRegion;
	private TMXTiledMap mTMXTiledMap;
	private PhysicsWorld mPhysicsWorld;
	private Scene mScene;
	protected int mCactusCount;
	
	// Controls
	private BitmapTextureAtlas mOnScreenControlTexture;
	private TextureRegion mOnScreenControlBaseTextureRegion;
	private TextureRegion mOnScreenControlKnobTextureRegion;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
		// WindowManager wm = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);
		// Display display = wm.getDefaultDisplay();
		// Point displaySize = new Point();
		// display.getSize(displaySize);
		// CAMERA_WIDTH = displaySize.x;
		// CAMERA_HEIGHT = displaySize.y;
		
		Display display = getWindowManager().getDefaultDisplay(); 
		CAMERA_WIDTH = display.getWidth();
		CAMERA_HEIGHT = display.getHeight();
		
		this.mBoundChaseCamera = new BoundCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mBoundChaseCamera);
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		// Load Caveman from tiled asset.
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 982, 688, TextureOptions.DEFAULT);
		this.mPlayerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "caveman_walking.png", 0, 0, 10, 7);
		this.mBitmapTextureAtlas.load();
		
//		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 72, 128, TextureOptions.DEFAULT);
//		this.mPlayerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "player.png", 0, 0, 3, 4);
//		this.mBitmapTextureAtlas.load();
		
		// Load controls from asset.
		this.mOnScreenControlTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_knob.png", 128, 0);
		this.mOnScreenControlTexture.load();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		// Create physics world
        this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, 0), false, 8, 1);
		
		mScene = new Scene();
		mScene.registerUpdateHandler(this.mPhysicsWorld);

		try {
			// ===========================================================
			// Try to use properties of tmx tile. Idea is to prevent the player from moving beyond the border (rocks).
			final TMXLoader tmxLoader = new TMXLoader(this.getAssets(), this.mEngine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, this.getVertexBufferObjectManager(), new ITMXTilePropertiesListener() {
				@Override
				public void onTMXTileWithPropertiesCreated(final TMXTiledMap pTMXTiledMap, final TMXLayer pTMXLayer, final TMXTile pTMXTile, final TMXProperties<TMXTileProperty> pTMXTileProperties) {
//					/* We are going to count the tiles that have the property "cactus=true" set. */
//					if(pTMXTileProperties.containsTMXProperty("cactus", "true")) {
//						Game.this.mCactusCount++;
//					}
					if(pTMXTileProperties.containsTMXProperty("walkable", "false")) {
						Toast.makeText(getBaseContext(), "Cannot walk here!", Toast.LENGTH_LONG).show();
						System.out.println("Cannot walk here!");
					}
				}
			});
			// ===========================================================
			
			// Load the TMXTiledMap from tmx asset.
			this.mTMXTiledMap = tmxLoader.loadFromAsset("tmx/Game_Map_Level_1.tmx");
			
//			this.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					Toast.makeText(Game.this, "Cactus count in this TMXTiledMap: " + Game.this.mCactusCount, Toast.LENGTH_LONG).show();
//				}
//			});
		} catch (final TMXLoadException e) {
			Debug.e(e);
		}

		// Attach the first layer from the TMXTiledMap.
		final TMXLayer tmxLayerZero = this.mTMXTiledMap.getTMXLayers().get(0);
		mScene.attachChild(tmxLayerZero);
	
		// Attach other layers from the TMXTiledMap, if it has more than one.
		for (int i = 1; i < this.mTMXTiledMap.getTMXLayers().size(); i++) {
			TMXLayer tmxLayer = this.mTMXTiledMap.getTMXLayers().get(i);
			//if (!tmxLayer.getTMXLayerProperties().containsTMXProperty("walkable", "false")) {
				mScene.attachChild(tmxLayer);
			//}
		}
		
//		// Read in the unwalkable blocks from the object layer and create boxes for each
//      this.createUnwalkableObjects(mTMXTiledMap);
		
//      // Add outer walls
//      this.addBounds(tmxLayerZero.getWidth(), tmxLayerZero.getHeight());
		
		/* Make the camera not exceed the bounds of the TMXEntity. */
		this.mBoundChaseCamera.setBounds(0, 0, tmxLayerZero.getWidth(), tmxLayerZero.getHeight());
		this.mBoundChaseCamera.setBoundsEnabled(true);

		/* Calculate the coordinates for the face, so its centered on the camera. */
		final float centerX = (CAMERA_WIDTH - this.mPlayerTextureRegion.getWidth()) / 2;
		final float centerY = (CAMERA_HEIGHT - this.mPlayerTextureRegion.getHeight()) / 2;

		/* Create the sprite and add it to the scene. */
		player = new AnimatedSprite(centerX, centerY, this.mPlayerTextureRegion, this.getVertexBufferObjectManager());		
		this.mBoundChaseCamera.setChaseEntity(player);
		
//		// Add body to the player. This is needed for collision detection.
//		final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0.5f);
//        mPlayerBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, player, BodyType.DynamicBody, playerFixtureDef);
//        this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(player, mPlayerBody, true, false){
//                @Override
//                public void onUpdate(float pSecondsElapsed){
//                        super.onUpdate(pSecondsElapsed);
//                        mBoundChaseCamera.updateChaseEntity();
//                }
//        });
		
		// Add a PhysicsHandler to the player. Used for different velocities of player when using the control knob.
		final PhysicsHandler physicsHandler = new PhysicsHandler(player);
		player.registerUpdateHandler(physicsHandler);

		final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl(0, CAMERA_HEIGHT - this.mOnScreenControlBaseTextureRegion.getHeight(), this.mBoundChaseCamera, this.mOnScreenControlBaseTextureRegion, this.mOnScreenControlKnobTextureRegion, 0.1f, 200, this.getVertexBufferObjectManager(), new IAnalogOnScreenControlListener() {
			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
				// Velocity could be used to check if the animation of the player should be walking or running.
				physicsHandler.setVelocity(pValueX * 100, pValueY * 100);
				
				// Compute direction in degree (from -180� to +180�).
				float degree = MathUtils.radToDeg((float)Math.atan2(pValueX, pValueY));

				// Stop animation if the controls are not used.
				if(player.isAnimationRunning() && degree == 0) {
					player.stopAnimation();
				}
				
				// Animate the player with respect to one of the 8 possible directions.
				if (!player.isAnimationRunning()) {
					if (-22.5 <= degree && degree <= 22.5 && degree != 0) {
						// Direction: S
						player.animate(new long[]{50, 50, 50, 50, 50, 50, 50, 50}, 32, 39, false);
					} else if (22.5 <= degree && degree <= 67.5) {
						// Direction: SE
						player.animate(new long[]{50, 50, 50, 50, 50, 50, 50, 50}, 40, 47, false);
					} else if (67.5 <= degree && degree <= 112.5) {
						// Direction: E
						player.animate(new long[]{50, 50, 50, 50, 50, 50, 50, 50}, 0, 7, false);
					} else if (112.5 <= degree && degree <= 157.5) {
						// Direction: NE
						player.animate(new long[]{50, 50, 50, 50, 50, 50, 50, 50}, 16, 23, false);
					} else if (157.5 <= degree || degree <= -157.5) {
						// Direction: N
						player.animate(new long[]{50, 50, 50, 50, 50, 50, 50, 50}, 8, 15, false);
					} else if (-157.5 <= degree && degree <= -112.5) {
						// Direction: NW
						player.animate(new long[]{50, 50, 50, 50, 50, 50, 50, 50}, 24, 31, false);
					} else if (-112.5 <= degree && degree <= -67.5) {
						// Direction: W
						player.animate(new long[]{50, 50, 50, 50, 50, 50, 50, 50}, 56, 63, false);
					} else if (-67.5 <= degree && degree <= -22.5) {
						// Direction: SW
						player.animate(new long[]{50, 50, 50, 50, 50, 50, 50, 50}, 48, 55, false);
					}
				}
				
//				// Player animation
//				if (pValueX > 0 && (pValueY > -0.5 && pValueY < 0.5)) {
//					player.animate(new long[]{200, 200, 200}, 3, 5, true);					
//				} else if ((pValueX > -0.5 && pValueX < 0.5) && pValueY > 0) {
//					player.animate(new long[]{200, 200, 200}, 6, 8, true);										
//				} else if (pValueX < 0 && (pValueY > -0.5 && pValueY < 0.5)) {
//					player.animate(new long[]{200, 200, 200}, 9, 11, true);					
//				} else {
//					player.animate(new long[]{200, 200, 200}, 0, 2, true);										
//				}
				
			}

			@Override
			public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {
				// Simulate a jump when clicking on the control knob.
				player.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(0.25f, 1, 1.5f), new ScaleModifier(0.25f, 1.5f, 1)));
			}
		});
		
		analogOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		analogOnScreenControl.getControlBase().setAlpha(0.5f);
		analogOnScreenControl.getControlBase().setScaleCenter(0, 128);
		analogOnScreenControl.getControlBase().setScale(1.25f);
		analogOnScreenControl.getControlKnob().setScale(1.25f);
		analogOnScreenControl.refreshControlKnobPosition();

		mScene.setChildScene(analogOnScreenControl);
		
		/* Now we are going to create a rectangle that will  always highlight the tile below the feet of the pEntity. */
/*		final Rectangle currentTileRectangle = new Rectangle(0, 0, this.mTMXTiledMap.getTileWidth(), this.mTMXTiledMap.getTileHeight(), this.getVertexBufferObjectManager());
		currentTileRectangle.setColor(1, 0, 0, 0.25f);
		scene.attachChild(currentTileRectangle);

		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				 Get the scene-coordinates of the players feet. 
				final float[] playerFootCordinates = player.convertLocalToSceneCoordinates(12, 31);

				 Get the tile the feet of the player are currently waking on. 
				final TMXTile tmxTile = tmxLayer.getTMXTileAt(playerFootCordinates[Constants.VERTEX_INDEX_X], playerFootCordinates[Constants.VERTEX_INDEX_Y]);
				if(tmxTile != null) {
					// tmxTile.setTextureRegion(null); <-- Rubber-style removing of tiles =D
					currentTileRectangle.setPosition(tmxTile.getTileX(), tmxTile.getTileY());
				}
			}
		});
*/		
		mScene.attachChild(player);

		return mScene;
	}

	// ===========================================================
	// Methods
	// ===========================================================

//	private void createUnwalkableObjects(TMXTiledMap map){
//    // Loop through the object groups
//     for(final TMXLayer layer: this.mTMXTiledMap.getTMXLayers()) {
//             if(layer.getTMXLayerProperties().containsTMXProperty("walkable", "false")){
//                // This is our "wall" layer. Create the boxes from it
//                final Rectangle rect = new Rectangle(layer.getX(), layer.getY(), layer.getWidth(), layer.getHeight(), this.getVertexBufferObjectManager());
//                final FixtureDef boxFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 1f);
//                PhysicsFactory.createBoxBody(this.mPhysicsWorld, rect, BodyType.StaticBody, boxFixtureDef);
//                rect.setVisible(false);
//                mScene.attachChild(rect);
//             }
//     }
//}

private void addBounds(float width, float height){
    final Shape bottom = new Rectangle(0, height - 2, width, 2, this.getVertexBufferObjectManager());
    bottom.setVisible(false);
    final Shape top = new Rectangle(0, 0, width, 2, this.getVertexBufferObjectManager());
    top.setVisible(false);
    final Shape left = new Rectangle(0, 0, 2, height, this.getVertexBufferObjectManager());
    left.setVisible(false);
    final Shape right = new Rectangle(width - 2, 0, 2, height, this.getVertexBufferObjectManager());
    right.setVisible(false);

    final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 1f);
    PhysicsFactory.createBoxBody(this.mPhysicsWorld, (IAreaShape) bottom, BodyType.StaticBody, wallFixtureDef);
    PhysicsFactory.createBoxBody(this.mPhysicsWorld, (IAreaShape) top, BodyType.StaticBody, wallFixtureDef);
    PhysicsFactory.createBoxBody(this.mPhysicsWorld, (IAreaShape) left, BodyType.StaticBody, wallFixtureDef);
    PhysicsFactory.createBoxBody(this.mPhysicsWorld, (IAreaShape) right, BodyType.StaticBody, wallFixtureDef);

    this.mScene.attachChild(bottom);
    this.mScene.attachChild(top);
    this.mScene.attachChild(left);
    this.mScene.attachChild(right);
}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
