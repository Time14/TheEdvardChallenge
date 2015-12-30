package xsked.states;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import time.api.gamestate.GameState;
import time.api.math.Vector2f;
import xsked.level.Camera;
import xsked.level.Level;
import xsked.level.Player;
import xsked.net.EventQueue;
import xsked.net.LevelSender;
import xsked.net.PacketSummonSpell;

public class StateWizard extends GameState {
	
	private Level level;
	
	public StateWizard() {
		super("Wizard");
	}
	
	@Override
	public void init() {
		level = new Level(2, 2, Player.MODE_WIZARD);
		
		level.getPlayer().setCanMove(false);
		
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
	public void onMouse(long window, int button, int action, int mods) {
		if(action == GLFW.GLFW_PRESS && button == GLFW.GLFW_MOUSE_BUTTON_LEFT && !level.getPlayer().isDead()) {
			Vector2f mousePos = Camera.getMouseCoords();
			
			float atan2 = (float) Math.atan2(mousePos.getY() - level.getPlayer().getY(), mousePos.getX() - level.getPlayer().getX());
			
			Vector2f dir = new Vector2f((float) Math.cos(atan2), (float) Math.sin(atan2));
			
			level.summonSpell(level.getPlayer().getX(), level.getPlayer().getY(), level.getPlayer().getElement(), dir);
			LevelSender.sendPacket(new PacketSummonSpell(level.getPlayer().getX(), level.getPlayer().getY(),
					level.getPlayer().getElement(), Player.SPELL_SPEED, dir.getX(), dir.getY()));
		}
	}
	
	@Override
	public void update(float dt) {
		if(dt > .3f)
			return;
		level.update(dt);
		LevelSender.updateWizard(dt);
		EventQueue.process();
	}
}