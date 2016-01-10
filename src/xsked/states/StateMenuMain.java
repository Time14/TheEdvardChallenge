package xsked.states;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import time.api.Game;
import time.api.audio.Audio;
import time.api.audio.AudioLibrary;
import time.api.audio.AudioManager;
import time.api.debug.Debug;
import time.api.gamestate.GameState;
import time.api.gamestate.GameStateManager;
import time.api.gfx.font.FontType;
import time.api.gfx.gui.Button;
import time.api.gfx.gui.GUI;
import time.api.gfx.shader.OrthographicShaderProgram;
import time.api.gfx.texture.DynamicTexture;
import time.api.gfx.texture.SpriteSheet;
import time.api.gfx.texture.Texture;
import time.api.input.InputManager;
import xsked.Main;
import xsked.level.Camera;

public class StateMenuMain extends GameState {
	
	private FontType font;
	
	private GUI gui;
	
	private Game game;
	
	private boolean initialized = false;
	
	private boolean fullscreen = true;
	
	public StateMenuMain(Game game) {
		super("Main");
		
		this.game = game;
	}
	
	@Override
	public void init() {
		if(!initialized)
			setup();

		
//		AudioManager.start();
//		
//		AudioLibrary.registerAudio("song", new Audio("res/sounds/EdvardsSong.wav"));
//		AudioLibrary.registerAudio("jump", new Audio("res/sounds/jump.wav"));
//		AudioLibrary.registerAudio("landing", new Audio("res/sounds/landing.wav"));
//		AudioLibrary.registerAudio("step", new Audio("res/sounds/step.wav"));
//		
//		AudioManager.playLoop(0, 0.5f, 1.0f, "song");
		
		gui = new GUI();
		
		Button play = new Button(Main.WIDTH / 2, (Main.HEIGHT / 4) * 3, 400, 100,
				Texture.getDT("button_wood1", true)).setFont("Play", font, .5f);
		
		play.setClickEvent(() -> GameStateManager.enterState("Play Menu"));
		
		Button hiScore = new Button(Main.WIDTH / 2, (Main.HEIGHT / 4) * 2, 400, 100,
				Texture.getDT("button_wood2", true)).setFont("Hi-Score", font, .5f);
		
		hiScore.setClickEvent(() -> Debug.log("Nothing yet!"));
		
		Button exit = new Button(Main.WIDTH / 2, (Main.HEIGHT / 4) * 1, 400, 100,
				Texture.getDT("button_wood1", true)).setFont("Exit", font, .5f);
		
		exit.setClickEvent(() -> game.stop());
		
		gui.addElements(
			play,
			hiScore,
			exit
		);
	}
	
	@Override
	public void draw() {
		Camera.pop();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		gui.draw();
	}
	
	@Override
	public void exit() {
		
	}
	
	@Override
	public void onKeyboard(long window, int key, int scancode, int action, int mods) {
		gui.triggerKey(key, mods, action);
	}
	
	@Override
	public void onMouse(long window, int button, int action, int mods) {
		if(action == GLFW.GLFW_PRESS && button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
			gui.click(OrthographicShaderProgram.INSTANCE.getMouseClipspaceCoordinates(game.getWindow(), Main.WIDTH, Main.HEIGHT));
	}
	
	@Override
	public void update(float dt) {
		gui.update(dt, OrthographicShaderProgram.INSTANCE.getMouseClipspaceCoordinates(game.getWindow(), Main.WIDTH, Main.HEIGHT));
	}
	
	private void setup() {
		
		//Fullscreen
		
		//Font
		font = FontType.FNT_ARIAL;
		
		//Setup projection
		OrthographicShaderProgram.INSTANCE.sendMatrix("m_projection",
				OrthographicShaderProgram.initProjection(0, Main.WIDTH, 0, Main.HEIGHT));
		Camera.push();
		
		GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
		GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_FASTEST);
		
		GLFW.glfwSwapInterval(1);
		
		//Sprite sheets
		SpriteSheet.register("button_wood1", new SpriteSheet(2, 1, 64, 16).loadTexture("res/texture/button_wood1.png"));
		SpriteSheet.register("button_wood2", new SpriteSheet(2, 1, 64, 16).loadTexture("res/texture/button_wood2.png"));
		
		SpriteSheet.register("tiles_platform", new SpriteSheet(2, 2, 16, 16).loadTexture("res/texture/tiles/tiles_platform.png"));
		
		SpriteSheet.register("player", new SpriteSheet(16, 8, 16, 32).loadTexture("res/texture/apprentice/apprenticeSheet.png"));
		
		SpriteSheet.register("spells", new SpriteSheet(16, 16, 16, 16).loadTexture("res/texture/apprentice/spells.png"));
		
		SpriteSheet.register("elements", new SpriteSheet(2, 2, 16, 16).loadTexture("res/texture/apprentice/elements.png"));
		
		//Textures
		Texture.register("button_wood1", new DynamicTexture(SpriteSheet.get("button_wood1")));
		Texture.register("button_wood2", new DynamicTexture(SpriteSheet.get("button_wood2")));
		
		Texture.register("tilesheet", new Texture("res/texture/tiles/fullSheet.png"));
		
		Texture.register("door", new Texture("res/texture/tiles/door.png"));
		
		Texture.register("player", new DynamicTexture(SpriteSheet.get("player")));
		
		Texture.register("spells", new DynamicTexture(SpriteSheet.get("spells")));
		
		Texture.register("element_wind", SpriteSheet.get("elements").getTexture(0, 0));
		Texture.register("element_water", SpriteSheet.get("elements").getTexture(1, 0));
		Texture.register("element_fire", SpriteSheet.get("elements").getTexture(0, 1));
		Texture.register("element_earth", SpriteSheet.get("elements").getTexture(1, 1));
		
		Texture.register("ghost", new Texture("res/texture/ghost.png"));
		
		//Register keys
		InputManager.registerKey(GLFW.GLFW_KEY_UP, 0, "up");
		InputManager.registerKey(GLFW.GLFW_KEY_DOWN, 0, "down");
		InputManager.registerKey(GLFW.GLFW_KEY_LEFT, 0, "left");
		InputManager.registerKey(GLFW.GLFW_KEY_RIGHT, 0, "right");
		
		//General
		InputManager.registerKey(GLFW.GLFW_KEY_R, 0, "restart");
		InputManager.registerKey(GLFW.GLFW_KEY_E, 0, "exit");
		
		//Player movement
		InputManager.registerKey(GLFW.GLFW_KEY_A, 0, "p_left");
		InputManager.registerKey(GLFW.GLFW_KEY_D, 0, "p_right");
		InputManager.registerKey(GLFW.GLFW_KEY_SPACE, 0, "p_jump");
		
		//Switch element
		InputManager.registerKey(GLFW.GLFW_KEY_1, 0, "e_fire");
		InputManager.registerKey(GLFW.GLFW_KEY_2, 0, "e_wind");
		InputManager.registerKey(GLFW.GLFW_KEY_3, 0, "e_earth");
		InputManager.registerKey(GLFW.GLFW_KEY_4, 0, "e_water");
		
		initialized = true;
	}
}