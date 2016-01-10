package xsked.states;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import time.api.debug.Debug;
import time.api.gamestate.GameState;
import time.api.gamestate.GameStateManager;
import time.api.input.InputManager;
import time.api.math.Vector2f;
import time.api.util.Time;
import xsked.level.Camera;
import xsked.level.Chunk;
import xsked.level.Level;
import xsked.level.Player;
import xsked.level.Spell.SpellType;
import xsked.level.Tile;
import xsked.net.EventQueue;
import xsked.net.LevelSender;
import xsked.net.NetworkManager;
import xsked.net.PacketSummonGhost;
import xsked.net.PacketSwitchElement;

public class StateApprentice extends GameState {
	
	private Level level;
	
	public StateApprentice() {
		super("Apprentice");
	}
	
	@Override
	public void init() {
		
		level = new Level(Player.MODE_APPRENTICE);
		
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
		if(!NetworkManager.isConnected())
			GameStateManager.enterState("Main");
		
		if(dt > .3f)
			return;
		
//		Debug.log(Time.getFPS());
		
		if(!level.getPlayer().isDead()) {
			if(InputManager.wasPressed("e_fire")) {
				level.getPlayer().switchElement(SpellType.FIRE);
				LevelSender.sendPacket(new PacketSwitchElement(SpellType.FIRE));
			} else if(InputManager.wasPressed("e_wind")) {
				level.getPlayer().switchElement(SpellType.WIND);
				LevelSender.sendPacket(new PacketSwitchElement(SpellType.WIND));
			} else if(InputManager.wasPressed("e_earth")) {
				level.getPlayer().switchElement(SpellType.EARTH);
				LevelSender.sendPacket(new PacketSwitchElement(SpellType.EARTH));
			} else if(InputManager.wasPressed("e_water")) {
				level.getPlayer().switchElement(SpellType.WATER);
				LevelSender.sendPacket(new PacketSwitchElement(SpellType.WATER));
			}
		}
		level.update(dt);
		LevelSender.updateApprentice(dt);
		EventQueue.process();
	}
}