package xsked.states;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import time.api.Game;
import time.api.gamestate.GameState;
import time.api.gamestate.GameStateManager;
import time.api.gfx.font.FontType;
import time.api.gfx.gui.Button;
import time.api.gfx.gui.GUI;
import time.api.gfx.shader.OrthographicShaderProgram;
import time.api.gfx.texture.Texture;
import xsked.Main;

public class StateMenuPlay extends GameState {
	
	private FontType font;
	
	private Game game;
	
	private GUI gui;
	
	public StateMenuPlay(Game game) {
		super("Play Menu");
		
		this.game = game;
	}
	
	@Override
	public void init() {
		
		font = FontType.FNT_ARIAL;
		
		gui = new GUI();
		
		Button host = new Button(Main.WIDTH / 2, (Main.HEIGHT / 4) * 3, 400, 100,
				Texture.getDT("button_wood1", true)).setFont("Host Game", font, .5f);
		
		host.setClickEvent(() -> GameStateManager.enterState("Host Menu"));
		
		Button connect = new Button(Main.WIDTH / 2, (Main.HEIGHT / 4) * 2, 400, 100,
				Texture.getDT("button_wood2", true)).setFont("Connect", font, .5f);
		
		connect.setClickEvent(() -> GameStateManager.enterState("Apprentice"));
		
		Button back = new Button(Main.WIDTH / 2, (Main.HEIGHT / 4) * 1, 400, 100,
				Texture.getDT("button_wood1", true)).setFont("Back", font, .5f);
		
		back.setClickEvent(() -> GameStateManager.enterState("Main"));
		
		
		gui.addElements(
			host,
			connect,
			back
		);
		
	}
	
	@Override
	public void draw() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		gui.draw();
	}
	
	@Override
	public void exit() {}
	
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
}