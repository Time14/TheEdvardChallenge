package xsked.states;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import time.api.Game;
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
import xsked.Main;

public class StateMenuMain extends GameState {
	
	private FontType font;
	
	private GUI gui;
	
	private Game game;
	
	private boolean initialized = false;
	
	public StateMenuMain(Game game) {
		super("Main");
		
		this.game = game;
	}
	
	@Override
	public void init() {
		if(!initialized)
			setup();
		
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
		
		//Font
		font = FontType.FNT_ARIAL;
		
		//Setup projection
		OrthographicShaderProgram.INSTANCE.sendMatrix("m_projection",
				OrthographicShaderProgram.initProjection(0, Main.WIDTH, 0, Main.HEIGHT));
		
		//Sprite sheets
		
		SpriteSheet.register("button_wood1", new SpriteSheet(2, 1, 64, 16).loadTexture("res/texture/button_wood1.png"));
		SpriteSheet.register("button_wood2", new SpriteSheet(2, 1, 64, 16).loadTexture("res/texture/button_wood2.png"));
		
		//Textures
		
		Texture.register("button_wood1", new DynamicTexture(SpriteSheet.get("button_wood1")));
		Texture.register("button_wood2", new DynamicTexture(SpriteSheet.get("button_wood2")));
		
		initialized = true;
	}
}