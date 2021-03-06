package gamedev.game;

import gamedev.scenes.BaseScene;
import gamedev.scenes.GameEndScene;
import gamedev.scenes.GameIntroScene;
import gamedev.scenes.GameMapScene;
import gamedev.scenes.LoadingScene;
import gamedev.scenes.MainMenuScene;
import gamedev.scenes.SplashScene;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

public class SceneManager {
	// ---------------------------------------------
	// SCENES
	// ---------------------------------------------

	private BaseScene splashScene;
	private BaseScene menuScene;
	private GameMapScene gameMapScene;
	private BaseScene gameShopScene;
	private BaseScene gameEndScene;
	private BaseScene loadingScene;
	private BaseScene introScene;

	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------

	private static final SceneManager INSTANCE = new SceneManager();

	private SceneType currentSceneType = SceneType.SCENE_SPLASH;

	private BaseScene currentScene;

	private ResourcesManager resourcesManager = ResourcesManager.getInstance();
	private Engine engine = resourcesManager.engine;

	public enum SceneType {
		SCENE_SPLASH, SCENE_MENU, SCENE_GAME_MAP, SCENE_GAME_SHOP, SCENE_GAME_END, SCENE_LOADING, SCENE_INTRO
	}

	// ---------------------------------------------
	// CLASS LOGIC
	// ---------------------------------------------

	public void setScene(BaseScene scene) {
		engine.setScene(scene);
		currentScene = scene;
		currentSceneType = scene.getSceneType();
	}

	public void setScene(SceneType sceneType) {
		switch (sceneType) {
		case SCENE_SPLASH:
			setScene(splashScene);
			break;
		case SCENE_LOADING:
			setScene(loadingScene);
			break;
		case SCENE_INTRO:
			setScene(introScene);
			break;
		case SCENE_MENU:
			setScene(menuScene);
			break;
		case SCENE_GAME_MAP:
			setScene(gameMapScene);
			break;
		case SCENE_GAME_SHOP:
			setScene(gameShopScene);
			break;
		case SCENE_GAME_END:
			setScene(gameEndScene);
			break;
		default:
			break;
		}
	}

	// ---------------------------------------------
	// Creating and Disposing of the different scenes
	// ---------------------------------------------

	// ---------------------------------------------
	// SplashScene
	// ---------------------------------------------

	public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		resourcesManager.loadSplashScreen();
		splashScene = new SplashScene();
		currentScene = splashScene;
		pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
	}

	private void disposeSplashScene() {
		if (!splashScene.isDisposed()) {
			splashScene.disposeScene();
		}
		resourcesManager.unloadSplashScreen();
		splashScene = null;
	}

	// ---------------------------------------------
	// MainMenuScene
	// ---------------------------------------------

	public void createMenuScene() {
		resourcesManager.loadMenuResources();
		menuScene = new MainMenuScene();
		resourcesManager.backgroundMusicMenu.play();
		setScene(menuScene);
		disposeSplashScene();
	}

	public void disposeMenuScene() {
		if (!menuScene.isDisposed()) {
			menuScene.disposeScene();
		}
		resourcesManager.unloadMenuResources();
	}

	public void loadMenuScene(final Engine mEngine) {
		disposeCurrentScene(true);

		resourcesManager.loadMenuResources();
		if (menuScene == null) {
			menuScene = new MainMenuScene();
		}

		mEngine.registerUpdateHandler(new TimerHandler(0.1f,
				new ITimerCallback() {
					public void onTimePassed(final TimerHandler pTimerHandler) {
						mEngine.unregisterUpdateHandler(pTimerHandler);
						setScene(menuScene);
						resourcesManager.backgroundMusicMenu.play();
						disposeLoadingScene();
					}
				}));
	}

	// ---------------------------------------------
	// GameIntroScene
	// ---------------------------------------------

	public void disposeGameIntroScene() {
		if (introScene != null) {
			if (!introScene.isDisposed()) {
				introScene.disposeScene();
			}
			resourcesManager.unloadGameIntroResources();
			introScene = null;
		}
	}

	public void loadGameIntroScene(final Engine mEngine) {
		disposeCurrentScene(true);

		resourcesManager.loadGameIntroResources();
		introScene = new GameIntroScene();

		mEngine.registerUpdateHandler(new TimerHandler(0.1f,
				new ITimerCallback() {
					public void onTimePassed(final TimerHandler pTimerHandler) {
						mEngine.unregisterUpdateHandler(pTimerHandler);
						setScene(introScene);
						resourcesManager.backgroundMusicGame.resume();
						disposeLoadingScene();
					}
				}));
	}

	// ---------------------------------------------
	// GameMapScene
	// ---------------------------------------------

	public void loadGameMapScene(final Engine mEngine) {
		if (!currentSceneType.equals(SceneType.SCENE_LOADING)) {
			disposeCurrentScene(true);
		}

		resourcesManager.loadGameResources();
		if (!isGameMapSceneCreated()) {
			gameMapScene = new GameMapScene();
		}

		mEngine.registerUpdateHandler(new TimerHandler(0.1f,
				new ITimerCallback() {
					public void onTimePassed(final TimerHandler pTimerHandler) {
						mEngine.unregisterUpdateHandler(pTimerHandler);
						setScene(gameMapScene);
						if (!resourcesManager.backgroundMusicGame.isPlaying()) {
							resourcesManager.backgroundMusicGame.resume();
						}
						resourcesManager.soundManager.setMasterVolume(1);
						disposeLoadingScene();
					}
				}));
	}

	public void disposeGameMapScene() {
		if (!gameMapScene.isDisposed()) {
			gameMapScene.disposeScene();
		}
		resourcesManager.unloadGameResources();
	}

	// ---------------------------------------------
	// GameEndScene
	// ---------------------------------------------

	public void loadGameEndScene(final Engine mEngine) {
		disposeCurrentScene(false);
		resourcesManager.loadGameEndResources();
		gameEndScene = new GameEndScene();

		mEngine.registerUpdateHandler(new TimerHandler(0.1f,
				new ITimerCallback() {
					public void onTimePassed(final TimerHandler pTimerHandler) {
						mEngine.unregisterUpdateHandler(pTimerHandler);
						setScene(gameEndScene);
						resourcesManager.backgroundMusicGame.resume();
					}
				}));
	}

	public void disposeGameEndScene() {
		if (gameEndScene != null) {
			if (!gameEndScene.isDisposed()) {
				gameEndScene.disposeScene();
			}
			resourcesManager.unloadGameEndResources();
			gameEndScene = null;
		}
	}

	// ---------------------------------------------
	// LoadingScene
	// ---------------------------------------------

	/**
	 * 
	 * @param setLoadingSceneNeeded
	 *            true, if you want to show the loading scene
	 */
	public void disposeCurrentScene(boolean setLoadingSceneNeeded) {
		if (currentSceneType.equals(SceneType.SCENE_GAME_MAP)) {
			disposeGameMapScene();
			resourcesManager.backgroundMusicGame.pause();
		} else if (currentSceneType.equals(SceneType.SCENE_GAME_END)) {
			disposeGameEndScene();
			resourcesManager.backgroundMusicGame.pause();
		} else if (currentSceneType.equals(SceneType.SCENE_MENU)) {
			disposeMenuScene();
			resourcesManager.backgroundMusicMenu.stop();
		} else if (currentSceneType.equals(SceneType.SCENE_INTRO)) {

			// resourcesManager.backgroundMusicGame.pause();
		}

		if (setLoadingSceneNeeded) {
			setLoadingScene();
		}
	}

	public void disposeAllGameScenes() {
		disposeGameIntroScene();
		disposeGameMapScene();
		disposeGameEndScene();
	}

	public void setLoadingScene() {
		if (loadingScene == null) {
			loadingScene = new LoadingScene();
		}
		setScene(loadingScene);
	}

	public void disposeLoadingScene() {
		if (!loadingScene.isDisposed()) {
			loadingScene.disposeScene();
		}
	}

	// ---------------------------------------------
	// GETTERS AND SETTERS
	// ---------------------------------------------

	public static SceneManager getInstance() {
		return INSTANCE;
	}

	public SceneType getCurrentSceneType() {
		return currentSceneType;
	}

	public BaseScene getCurrentScene() {
		return currentScene;
	}

	public GameMapScene getCurrentGameMapScene() {
		return gameMapScene;
	}

	public void deleteCurrentGameMapScene() {
		disposeAllGameScenes();
		gameMapScene = null;
	}

	public boolean isGameMapSceneCreated() {
		return (gameMapScene != null);
	}

}