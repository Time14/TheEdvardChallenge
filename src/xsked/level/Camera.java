package xsked.level;

import time.api.gamestate.GameStateManager;
import time.api.gfx.shader.OrthographicShaderProgram;
import time.api.math.Matrix4f;
import time.api.math.Vector2f;
import xsked.Main;

public class Camera {
	
	private static float x, y;
	
	public static final float getX() {
		return x;
	}
	
	public static final float getY() {
		return y;
	}
	
	public static final void translate(float x, float y) {
		Camera.x += x;
		Camera.y += y;
		push();
	}
	
	public static final void setPosition(float x, float y) {
		Camera.x = x;
		Camera.y = y;
		push();
	}
	
	public static final void push() {
		OrthographicShaderProgram.INSTANCE.sendMatrix("m_view", new Matrix4f().loadIdentity().set(3, 0, -x).set(3, 1, -y));
	}
	
	public static final void pop() {
		OrthographicShaderProgram.INSTANCE.sendMatrix("m_view", new Matrix4f().loadIdentity());
	}
	
	public static final Vector2f getMouseCoords() {
		return (Vector2f) OrthographicShaderProgram.INSTANCE.getMouseClipspaceCoordinates(GameStateManager.getGame().getWindow(), Main.WIDTH, Main.HEIGHT).add(new Vector2f(x, y));
	}
}