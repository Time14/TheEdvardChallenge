package xsked.states;

import org.lwjgl.opengl.GL11;

import time.api.gamestate.GameState;
import xsked.level.Chunk;
import xsked.level.Level;

public class StateApprentice extends GameState {
	
	private Level level;
	
	private Chunk temp;
	
	public StateApprentice() {
		super("Apprentice");
	}
	
	@Override
	public void init() {
//		level = new Level(100, 100);
		temp = new Chunk(0, 0, null);
	}
	
	@Override
	public void draw() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		temp.draw();
//		level.draw();
	}
	
	@Override
	public void exit() {}
	
	@Override
	public void onKeyboard(long window, int key, int scancode, int action, int mods) {}
	
	@Override
	public void onMouse(long window, int button, int action, int mods) {}
	
	@Override
	public void update(float dt) {
//		level.update(dt);
	}
}