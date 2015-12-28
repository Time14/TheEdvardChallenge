package xsked.states;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import time.api.Game;
import time.api.gamestate.GameState;
import time.api.gamestate.GameStateManager;
import time.api.gfx.font.FontType;
import time.api.gfx.gui.Button;
import time.api.gfx.gui.GUI;
import time.api.gfx.gui.InputBox;
import time.api.gfx.shader.OrthographicShaderProgram;
import time.api.gfx.texture.Texture;
import xsked.Main;
import xsked.net.EventQueue;
import xsked.net.NetworkManager;

public class StateMenuConnect extends GameState {
	
	private Game game;
	
	private GUI gui;
	
	private GUI inputGUI;
	
	private FontType font;
	
	public StateMenuConnect(Game game) {
		super("Connect Menu");
		
		this.game = game;
	}
	
	@Override
	public void init() {
		
		NetworkManager.setType(NetworkManager.TYPE_CLIENT);
		
		font = FontType.FNT_ARIAL;
		
		inputGUI = new GUI();
		
		InputBox input = new InputBox(Main.WIDTH / 2, (Main.HEIGHT / 4) * 3, 400, 100, Texture.getDT("button_wood1", true), font, .5f);
		
		input.setText("Enter host IP here");
		input.setTextCap(3 * 4 + 4);
		
		inputGUI.addElements(
			input
		);
		
		gui = new GUI();
		
		Button connect = new Button(Main.WIDTH / 2, (Main.HEIGHT / 4) * 2, 400, 100,
				Texture.getDT("button_wood2", true)).setFont("Connect", font, .5f);
		
		connect.setClickEvent(() -> {
			
			int err = NetworkManager.connect(input.getText());
			
			if(err == NetworkManager.ERR_NONE) {
				input.setText("You were hired!");
			} else {
				System.out.println("ERROR!");
				try {
					Thread.sleep(1000);
				} catch (Exception e) {}
				GameStateManager.enterState("Connect Menu");
			}
		});
		
		Button back = new Button(Main.WIDTH / 2, (Main.HEIGHT / 4) * 1, 400, 100,
				Texture.getDT("button_wood2", true)).setFont("Back", font, .5f);
		
		back.setClickEvent(() -> GameStateManager.enterState("Play Menu"));
		
		gui.addElements(
			connect,
			back
		);
		
	}
	
	@Override
	public void draw() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		gui.draw();
		inputGUI.draw();
	}
	
	@Override
	public void exit() {}
	
	@Override
	public void onKeyboard(long window, int key, int scancode, int action, int mods) {
		gui.triggerKey(key, mods, action);
		inputGUI.triggerKey(key, mods, action);
	}
	
	@Override
	public void onMouse(long window, int button, int action, int mods) {
		if(action == GLFW.GLFW_PRESS && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			gui.click(OrthographicShaderProgram.INSTANCE.getMouseClipspaceCoordinates(game.getWindow(), Main.WIDTH, Main.HEIGHT));
			inputGUI.click(OrthographicShaderProgram.INSTANCE.getMouseClipspaceCoordinates(game.getWindow(), Main.WIDTH, Main.HEIGHT));
		}
	}
	
	@Override
	public void update(float dt) {
		gui.update(dt, OrthographicShaderProgram.INSTANCE.getMouseClipspaceCoordinates(game.getWindow(), Main.WIDTH, Main.HEIGHT));
		inputGUI.update(dt, OrthographicShaderProgram.INSTANCE.getMouseClipspaceCoordinates(game.getWindow(), Main.WIDTH, Main.HEIGHT));
		EventQueue.process();
	}
}