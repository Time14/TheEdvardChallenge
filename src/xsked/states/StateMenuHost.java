package xsked.states;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.PriorityQueue;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import time.api.Game;
import time.api.debug.Debug;
import time.api.gamestate.GameState;
import time.api.gamestate.GameStateManager;
import time.api.gfx.font.FontRenderer;
import time.api.gfx.font.FontType;
import time.api.gfx.gui.Button;
import time.api.gfx.gui.GUI;
import time.api.gfx.shader.OrthographicShaderProgram;
import time.api.gfx.texture.Texture;
import xsked.Main;
import xsked.net.NetworkManager;
import xsked.net.PacketState;

public class StateMenuHost extends GameState {
	
	public static final String DISPLAY_WAITING = "Looking for an apprentice...";
	public static final String DISPLAY_CONNECTED = "Apprentice found!";
	
	private FontType font;
	
	private Game game;
	
	private GUI gui;
	
	private FontRenderer display;
	
	private PriorityQueue<String> queue;
	
	public StateMenuHost(Game game) {
		super("Host Menu");
		
		this.game = game;
	}
	
	@Override
	public void init() {
		
		NetworkManager.setType(NetworkManager.TYPE_SERVER);
		
		queue = new PriorityQueue<>();
		
		font = FontType.FNT_ARIAL;
		
		display = new FontRenderer(0, 0, DISPLAY_WAITING, font, 1f);
		display.setPosition(Main.WIDTH / 2 - display.getWidth() / 2, (Main.HEIGHT / 4) * 3 + display.getAverageHeight());
		
		switch(NetworkManager.host()) {
		case NetworkManager.ERR_CLIENT:
			setDisplay("How did you even get here?");
			break;
		case NetworkManager.ERR_IO:
			setDisplay("IO Error");
			break;
		case NetworkManager.ERR_UNKNOWN_HOST:
			setDisplay("Could not determine local host");
			break;
		}
		
		
		gui = new GUI();
		
		Button back = new Button(Main.WIDTH / 2, (Main.HEIGHT / 4) * 1, 400, 100,
				Texture.getDT("button_wood2", true)).setFont("Back", font, .5f);
		
		back.setClickEvent(() -> {
			GameStateManager.enterState("Play Menu");
			NetworkManager.stop();
		});
		
		gui.addElements(
			back
		);
	}
	
	@Override
	public void draw() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		gui.draw();
		display.draw();
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
		while(!queue.isEmpty()) {
			String s = queue.poll();
			switch(s) {
			default:
				setDisplay(s);
				break;
			case "start":
				addStartButton();
				break;
			}
		}
		gui.update(dt, OrthographicShaderProgram.INSTANCE.getMouseClipspaceCoordinates(game.getWindow(), Main.WIDTH, Main.HEIGHT));
	}
	
	private synchronized void addStartButton() {
		Button start = new Button(Main.WIDTH / 2, (Main.HEIGHT / 4) * 2, 400, 100,
				Texture.getDT("button_wood1", true)).setFont("Start", font, .5f);
		
		start.setClickEvent(() -> {
			if(!NetworkManager.getServer().send(0, new PacketState("Apprentice"))) {
				Debug.log("Failed to send packet");
			}
			GameStateManager.enterState("Wizard");
		});
		
		gui.addElements(start);
	}
	
	public void setQueue(String text) {
		queue.add(text);
	}
	
	public void setDisplay(String text) {
		display.reallocateText(text);
		display.setPosition(Main.WIDTH / 2 - display.getWidth() / 2, (Main.HEIGHT / 4) * 3 + display.getAverageHeight());
	}
}