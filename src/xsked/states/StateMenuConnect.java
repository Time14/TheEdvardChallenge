package xsked.states;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import time.api.Game;
import time.api.gamestate.GameState;
import time.api.gfx.gui.GUI;
import time.api.gfx.shader.OrthographicShaderProgram;
import xsked.Main;

public class StateMenuConnect extends GameState {
	
	private Game game;
	
	private GUI gui;
	
	public StateMenuConnect(Game game) {
		super("Connect Menu");
		
		this.game = game;
	}
	
	@Override
	public void init() {
		
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