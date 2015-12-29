package xsked.states;

import org.lwjgl.opengl.GL11;

import time.api.gamestate.GameState;
import xsked.level.Level;
import xsked.net.EventQueue;
import xsked.net.LevelSender;

public class StateApprentice extends GameState {
	
	private Level level;
	
	public StateApprentice() {
		super("Apprentice");
	}
	
	@Override
	public void init() {
		
		level = new Level(10, 10, 0);
		
		LevelSender.setLevel(level);
		EventQueue.setLevel(level);
	}
	
	@Override
	public void draw() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		level.draw();
	}
	
	@Override
	public void exit() {}
	
	@Override
	public void onKeyboard(long window, int key, int scancode, int action, int mods) {}
	
	@Override
	public void onMouse(long window, int button, int action, int mods) {}
	
	@Override
	public void update(float dt) {
		level.update(dt);
		LevelSender.updateApprentice(dt);
		EventQueue.process();
	}
}