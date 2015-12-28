package xsked.states;

import time.api.gamestate.GameState;

public class StateApprentice extends GameState {
	
	public StateApprentice() {
		super("Apprentice");
	}
	
	@Override
	public void draw() {}
	
	@Override
	public void exit() {}
	
	@Override
	public void init() {}
	
	@Override
	public void onKeyboard(long window, int key, int scancode, int action, int mods) {}
	
	@Override
	public void onMouse(long window, int button, int action, int mods) {}
	
	@Override
	public void update(float dt) {}
}