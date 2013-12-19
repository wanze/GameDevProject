package gamedev.scenes;

import gamedev.game.SceneManager;
import gamedev.game.SceneManager.SceneType;
import gamedev.game.TmxLevelLoader;
import gamedev.objects.Player;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.util.debug.Debug;

/**
 * Base class for all levels
 * 
 */
public class LevelScene extends BaseScene {

	// TMX Map containing the Level
	protected TMXTiledMap mTMXTiledMap;
	protected String tmxFileName;

	// Player. Each level has to create the Player and its position in the world
	protected Player player;
	private int levelId;

	private Sprite gameEndPortal;

	private static final int MIN_DINOS_TO_KILL = 1;
	private int dinosKilled = 0;

	public LevelScene(int levelId) {
		// Call BaseScene without calling createScene because here we need some
		// stuff initialized before
		super(false);

		this.player = this.resourcesManager.player;
		this.levelId = levelId;

		// Load map from tmx-file.
		this.tmxFileName = "level" + levelId + ".tmx";

		// CreateScene creates the world and its objects defined in the TMX-Map.
		// TODO We need to check which method to use. Here, we have a
		// "graphical Editor" to place objects which is very easy.
		this.createScene();
	}

	@Override
	public void createScene() {
		this.createMap();
		this.connectPhysics();

		// Load level-rules from xml.
		// this.loadLevel(this.levelId);

		assert (player != null);
		// Check if the player is already has a parent (avoid
		// assertEntityHasNoParent IllegalStateException)
		if (player.hasParent()) {
			IEntity parentEntity = player.getParent();
			parentEntity.detachChild(player);
		}
		// 32 is the PIXEL_TO_METER_RATIO_DEFAULT from AndEngine
		player.body.setTransform(100 / 32, 100 / 32, 0);

		this.attachChild(player);

		// TODO: Define player and portal positions as constant.
		gameEndPortal = new Sprite(1000, 300,
				resourcesManager.gameEndPortalRegion, vbom) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				if (player.collidesWith(this) && areLevelRulesCompleted()) {
					SceneManager.getInstance().loadGameEndScene(engine);
				}
			}
		};
		gameEndPortal.setAlpha(0.9f);
		gameEndPortal.setScale(0.1f);
		gameEndPortal.registerEntityModifier(new LoopEntityModifier(
				new ScaleModifier(2, 0.95f, 1.05f)));

		this.attachChild(gameEndPortal);

	}

	protected void connectPhysics() {
		this.registerUpdateHandler(this.resourcesManager.physicsWorld);
	}

	protected void createMap() {
		// Try to load the tmx file
		try {
			final TMXLoader tmxLoader = new TMXLoader(
					this.activity.getAssets(), this.engine.getTextureManager(),
					TextureOptions.BILINEAR_PREMULTIPLYALPHA, this.vbom,
					new ITMXTilePropertiesListener() {
						@Override
						public void onTMXTileWithPropertiesCreated(
								final TMXTiledMap pTMXTiledMap,
								final TMXLayer pTMXLayer,
								final TMXTile pTMXTile,
								final TMXProperties<TMXTileProperty> pTMXTileProperties) {
						}
					});

			this.mTMXTiledMap = tmxLoader.loadFromAsset("tmx/"
					+ this.tmxFileName);

		} catch (final TMXLoadException e) {
			Debug.e(e);
		}

		// Set camera bounds
		TMXLayer tmxLayerZero = this.mTMXTiledMap.getTMXLayers().get(0);
		this.camera.setBounds(0, 0, tmxLayerZero.getWidth(),
				tmxLayerZero.getHeight());
		this.camera.setBoundsEnabled(true);

		// Load all the objects, boundaries of our level. This is handled in a
		// new class
		TmxLevelLoader loader = new TmxLevelLoader(this.mTMXTiledMap, this);
		loader.createWorldAndObjects();
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LEVEL;
	}

	@Override
	public void disposeScene() {
		this.detachSelf();
		this.dispose();
	}

	public void killedDino() {
		this.dinosKilled++;
	}

	private boolean areLevelRulesCompleted() {
		if (dinosKilled >= MIN_DINOS_TO_KILL) {
			return true;
		}
		return false;
	}

}
