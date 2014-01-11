package gamedev.game;

import gamedev.hud.SceneHUD;
import gamedev.objects.Avatar;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseActivity;

import android.graphics.Color;

import com.badlogic.gdx.math.Vector2;

public class ResourcesManager {
	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------

	private static final ResourcesManager INSTANCE = new ResourcesManager();

	public Engine engine;
	public BaseActivity activity;
	public BoundCamera camera;
	public VertexBufferObjectManager vbom;
	public TextureManager textureManager;
	public Avatar avatar;
	public SceneHUD hud;

	private static boolean gameGraphicsCreated = false;

	// ---------------------------------------------
	// TEXTURES & TEXTURE REGIONS
	// ---------------------------------------------

	// Textures for player, dinosaurs and environment
	public BitmapTextureAtlas playerAtlas;
	public ITiledTextureRegion playerRegion;
	public BitmapTextureAtlas dinosaurGreenAtlas;
	public ITiledTextureRegion dinosaurGreenRegion;
	public BitmapTextureAtlas treesAtlas;
	public ITextureRegion[] treeRegions = new ITextureRegion[20];

	// Textures for fight scene
	public BitmapTextureAtlas spearAtlas;
	public ITextureRegion spearRegion;
	public BitmapTextureAtlas fightDinoAtlas;
	public ITextureRegion fightDinoRegion;

	public BitmapTextureAtlas gameEndPortalAtlas;
	public ITextureRegion gameEndPortalRegion;

	// Textures for HUD and controls
	public BitmapTextureAtlas controlTextureAtlas;
	public TextureRegion controlBaseTextureRegion;
	public TextureRegion controlKnobTextureRegion;
	private BitmapTextureAtlas hudBerryAtlas;
	public TextureRegion hudBerryRegion;

	private BitmapTextureAtlas hudHelpIconAtlas;
	public TextureRegion hudHelpIconRegion;
	private BitmapTextureAtlas hudQuestListIconAtlas;
	public TextureRegion hudQuestListIconRegion;
	private BitmapTextureAtlas hudShopIconAtlas;
	public TextureRegion hudShopIconRegion;

	// Textures for splash scene
	public ITextureRegion splash_region;
	private BitmapTextureAtlas splashTextureAtlas;

	// Textures for current quest scene
	public ITextureRegion questFrameRegion;
	private BitmapTextureAtlas questFrameTextureAtlas;

	// Textures for game shop scene
	public ITextureRegion shopRegion;
	private BitmapTextureAtlas shopTextureAtlas;

	// Textures for menu scene
	public ITextureRegion menu_background_region;
	public ITiledTextureRegion menu_buttons_region;
	private BitmapTextureAtlas menuBackgroundTextureAtlas;
	private BitmapTextureAtlas menuButtonsTextureAtlas;
	public Font font;

	// Textures for game end scene
	public ITextureRegion game_end_region;
	public BitmapTextureAtlas game_end_atlas;

	// ---------------------------------------------
	// Physic
	// ---------------------------------------------

	public PhysicsWorld physicsWorld;

	// ---------------------------------------------
	// CLASS LOGIC
	// ---------------------------------------------

	// ---------------------------------------------
	// Splash resources
	// ---------------------------------------------

	public void loadSplashScreen() {
		if (splashTextureAtlas == null) {
			createSplashScreen();
		}

		splashTextureAtlas.load();
	}

	public void unloadSplashScreen() {
		splashTextureAtlas.unload();
		// The splash screen is no more used after the game has started.
		splash_region = null;
	}

	private void createSplashScreen() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(textureManager, 480, 320,
				TextureOptions.BILINEAR);
		splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				splashTextureAtlas, activity, "splash_andengine.png", 0, 0);
	}

	// ---------------------------------------------
	// Menu resources
	// ---------------------------------------------

	public void loadMenuResources() {
		loadMenuGraphics();
		// loadMenuAudio();
		loadMenuFonts();
	}

	public void unloadMenuResources() {
		unloadMenuGraphics();
		// unloadMenuAudio();
	}

	private void createMenuGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");

		// Menu background
		this.menuBackgroundTextureAtlas = new BitmapTextureAtlas(
				textureManager, 800, 600, TextureOptions.DEFAULT);
		this.menu_background_region = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(menuBackgroundTextureAtlas, activity,
						"menubackground.png", 0, 0, 1, 1);

		// Menu buttons
		this.menuButtonsTextureAtlas = new BitmapTextureAtlas(textureManager,
				800, 600, TextureOptions.DEFAULT);
		this.menu_buttons_region = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(menuButtonsTextureAtlas, activity,
						"menubuttons.png", 0, 0, 1, 3);
	}

	private void loadMenuGraphics() {
		if (menuButtonsTextureAtlas == null) {
			createMenuGraphics();
		}
		menuButtonsTextureAtlas.load();
		menuBackgroundTextureAtlas.load();
	}

	private void unloadMenuGraphics() {
		menuButtonsTextureAtlas.unload();
		menuBackgroundTextureAtlas.unload();
	}

	private void createMenuFonts() {
		FontFactory.setAssetBasePath("font/");
		final ITexture mainFontTexture = new BitmapTextureAtlas(textureManager,
				256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		font = FontFactory.createFromAsset(
				((GameActivity) activity).getFontManager(), mainFontTexture,
				activity.getAssets(), "font.ttf", 30f, true, Color.WHITE);
	}

	private void loadMenuFonts() {
		if (font == null) {
			createMenuFonts();
		}
		font.load();
	}

	private void unloadMenuFonts() {
		font.unload();
	}

	// ---------------------------------------------
	// Intro resources
	// ---------------------------------------------

	public void loadIntroResources() {
		// TODO
	}

	public void unloadIntroResources() {
		// TODO
	}

	private void createIntroResources() {
		// TODO
	}

	// ---------------------------------------------
	// Game resources
	// ---------------------------------------------

	public void loadGameResources() {
		loadGameGraphics();
		// TODO:
		loadMenuFonts();
		// loadGameAudio();
		loadHUDResources();
		loadGameShopResources();

		// TODO: Refactor. This should not be created here, rather in
		// GameMapScene.
		if (physicsWorld == null || avatar == null) {
			physicsWorld = new FixedStepPhysicsWorld(30, new Vector2(0, 0),
					false, 8, 1);
			physicsWorld.setContactListener(new BodiesContactListener());
			avatar = new Avatar();
		}
	}

	public void unloadGameResources() {
		unloadGameGraphics();
		// TODO:
		// unloadGameAudio();
		unloadHUDResources();
		unloadGameShopResources();
	}

	private void loadGameGraphics() {
		if (playerAtlas == null || gameGraphicsCreated == false) {
			createGameGraphics();
		}
		gameEndPortalAtlas.load();
		playerAtlas.load();
		dinosaurGreenAtlas.load();
		// treesAtlas.load();
		questFrameTextureAtlas.load();
//		spearAtlas.load();
		fightDinoAtlas.load();

	}

	private void unloadGameGraphics() {
		gameEndPortalAtlas.unload();
		playerAtlas.unload();
		dinosaurGreenAtlas.unload();
		// treesAtlas.unload();
		questFrameTextureAtlas.unload();
		spearAtlas.unload();
		fightDinoAtlas.unload();

	}

	private void createGameGraphics() {
		createGameEndPortalGraphics();
		createPlayerGraphics();
		createDinoGraphics();
		// createTreeGraphics();
		createQuestFrameGraphics();
//		createSpearGraphics();
		createFightSceneGraphics();
		gameGraphicsCreated = true;
	}

	private void createGameEndPortalGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");

		this.gameEndPortalAtlas = new BitmapTextureAtlas(textureManager, 280,
				296, TextureOptions.DEFAULT);

		this.gameEndPortalRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(gameEndPortalAtlas, activity, "portal.png", 0,
						0);
	}

	private void createPlayerGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");

		this.playerAtlas = new BitmapTextureAtlas(textureManager, 608, 760,
				TextureOptions.DEFAULT);

		this.playerRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.playerAtlas, activity,
						"caveman_0.5.png", 0, 0, 16, 20);
	}

	private void createDinoGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");

		this.dinosaurGreenAtlas = new BitmapTextureAtlas(textureManager, 832,
				1024, TextureOptions.DEFAULT);

		this.dinosaurGreenRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.dinosaurGreenAtlas, activity,
						"green_dino_0.25_asc.png", 0, 0, 26, 32);
	}

	private void createTreeGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");

		this.treesAtlas = new BitmapTextureAtlas(textureManager, 512, 640);

		BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.treesAtlas,
				activity, "trees.png", 0, 0);
		int x = 0;
		int y = 0;
		for (int i = 1; i <= 20; i++) {
			// this.treeRegions[i-1] =
			// BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.treesAtlas,
			// activity, "trees.png", x, y);
			this.treeRegions[i - 1] = TextureRegionFactory.extractFromTexture(
					this.treesAtlas, x, y, 128, 128);
			x = x + 128;
			if (i % 4 == 0) {
				x = 0;
				y = y + 128;
			}
		}
	}

	private void createQuestFrameGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");

		this.questFrameTextureAtlas = new BitmapTextureAtlas(textureManager,
				590, 480, TextureOptions.DEFAULT);

		this.questFrameRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(questFrameTextureAtlas, activity,
						"quest_frame.png", 0, 0);
	}

	// ---------------------------------------------
	// Fight resources
	// ---------------------------------------------

	private void createSpearGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");

		this.spearAtlas = new BitmapTextureAtlas(textureManager, 6, 45);
		this.spearRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.spearAtlas, activity, "spear.png", 0, 0);

		// BitmapTextureAtlasTextureRegionFactory.createFromAsset(
		// this.spearsAtlas, activity, "spear.png", 0, 0);
		//
		// for (int i = 0; i <= 7; i++) {
		// this.spearsRegions[i] = TextureRegionFactory.extractFromTexture(
		// this.spearsAtlas, i * 64, 0, 64, 48);
		// }
	}

	private void createFightSceneGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		this.fightDinoAtlas = new BitmapTextureAtlas(textureManager, 400, 355);
		this.fightDinoRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.fightDinoAtlas, activity, "dino_fight.png", 0, 0);
	}

	// ---------------------------------------------
	// Game shop resources
	// ---------------------------------------------

	public void loadGameShopResources() {
		loadGameShopGraphics();
	}

	public void unloadGameShopResources() {
		unloadGameShopGraphics();
	}

	private void createGameShopGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/shop/");

		this.shopTextureAtlas = new BitmapTextureAtlas(textureManager, 650,
				400, TextureOptions.DEFAULT);
		this.shopRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(shopTextureAtlas, activity,
						"shop_placeholder.png", 0, 0);
	}

	private void loadGameShopGraphics() {
		if (shopTextureAtlas == null) {
			createGameShopGraphics();
		}
		shopTextureAtlas.load();
	}

	private void unloadGameShopGraphics() {
		shopTextureAtlas.unload();
	}

	// ---------------------------------------------
	// HUD resources
	// ---------------------------------------------

	public void loadHUDResources() {
		loadHUDGraphics();
		this.hud = new SceneHUD();
		this.camera.setHUD(this.hud);
	}

	public void unloadHUDResources() {
		this.camera.setHUD(null);
		this.hud.detachSelf();
		if (!this.hud.isDisposed()) {
			this.hud.dispose();
		}
		unloadHUDGraphics();
	}

	private void createHUDGraphics() {
		BitmapTextureAtlasTextureRegionFactory
				.setAssetBasePath("gfx/game/hud/");

		this.controlTextureAtlas = new BitmapTextureAtlas(textureManager, 256,
				128, TextureOptions.BILINEAR);
		this.controlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(controlTextureAtlas, activity,
						"onscreen_control_base.png", 0, 0);
		this.controlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(controlTextureAtlas, activity,
						"onscreen_control_knob.png", 128, 0);

		this.hudBerryAtlas = new BitmapTextureAtlas(textureManager, 50, 39,
				TextureOptions.BILINEAR);
		this.hudBerryRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(hudBerryAtlas, activity, "berries_small.png",
						0, 0);
	}

	private void createHUDButtonIconGraphics() {
		BitmapTextureAtlasTextureRegionFactory
				.setAssetBasePath("gfx/game/hud/icons/");

		this.hudHelpIconAtlas = new BitmapTextureAtlas(textureManager, 50, 52,
				TextureOptions.BILINEAR);
		this.hudHelpIconRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(hudHelpIconAtlas, activity,
						"Game_Icons_0000_Help.png", 0, 0);

		this.hudQuestListIconAtlas = new BitmapTextureAtlas(textureManager, 44,
				44, TextureOptions.BILINEAR);
		this.hudQuestListIconRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(hudQuestListIconAtlas, activity,
						"Game_Icons_0008_List.png", 0, 0);

		this.hudShopIconAtlas = new BitmapTextureAtlas(textureManager, 44, 44,
				TextureOptions.BILINEAR);
		this.hudShopIconRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(hudShopIconAtlas, activity,
						"Game_Icons_0009_Shop.png", 0, 0);
	}

	private void loadHUDGraphics() {
		if (controlTextureAtlas == null || hudBerryAtlas == null) {
			createHUDGraphics();
			createHUDButtonIconGraphics();
		}
		controlTextureAtlas.load();
		hudBerryAtlas.load();

		hudHelpIconAtlas.load();
		hudQuestListIconAtlas.load();
		hudShopIconAtlas.load();
	}

	private void unloadHUDGraphics() {
		controlTextureAtlas.unload();
		hudBerryAtlas.unload();

		hudHelpIconAtlas.unload();
		hudQuestListIconAtlas.unload();
		hudShopIconAtlas.unload();
	}

	// ---------------------------------------------
	// GameEnd resources
	// ---------------------------------------------

	public void loadGameEndResources() {
		loadGameEndTextures();
	}

	public void unloadGameEndResources() {
		unloadGameEndTextures();
	}

	private void createGameEndGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");

		game_end_atlas = new BitmapTextureAtlas(textureManager, 1024, 576,
				TextureOptions.DEFAULT);
		game_end_region = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(game_end_atlas, activity, "game_end.png", 0, 0);
	}

	private void loadGameEndTextures() {
		if (game_end_atlas == null) {
			createGameEndGraphics();
		}
		game_end_atlas.load();
	}

	private void unloadGameEndTextures() {
		game_end_atlas.unload();
	}

	/**
	 * @param engine
	 * @param activity
	 * @param camera
	 * @param vbom
	 * <br>
	 * <br>
	 *            We use this method at beginning of game loading, to prepare
	 *            Resources Manager properly, setting all needed parameters, so
	 *            we can latter access them from different classes (eg. scenes)
	 */
	public static void prepareManager(Engine engine, BaseActivity activity,
			BoundCamera camera, VertexBufferObjectManager vbom,
			TextureManager textureManager) {
		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;
		getInstance().textureManager = textureManager;
	}

	// ---------------------------------------------
	// GETTERS AND SETTERS
	// ---------------------------------------------

	public static ResourcesManager getInstance() {
		return INSTANCE;
	}

	public boolean areGameResourcesCreated() {
		return gameGraphicsCreated;
	}
}