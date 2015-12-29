package xsked.level;

import time.api.debug.Debug;
import time.api.entity.Entity;
import time.api.gfx.QuadRenderer;
import time.api.gfx.texture.Texture;
import time.api.input.InputManager;
import time.api.math.Vector2f;
import time.api.math.VectorXf;
import time.api.physics.Body;
import xsked.Main;

public class Player extends Entity{
	
	public static final float MAX_SPEED = 300f;
	
	public static final float JUMP_SPEED = 500.0f;
	
	public static final float CAM_SPEED = 7.5f;
	
	private static final float WIDTH = Tile.SIZE * 1.0f;
	private static final float HEIGHT = Tile.SIZE * 2.0f;
	
	
	private static Tag element;
	
	private boolean grounded;
	
	private short direction;
	private VectorXf flatVector = new VectorXf(1, 0);
	
	public Player(float x, float y) {
		setRenderer(new QuadRenderer(x, y, WIDTH, HEIGHT, Texture.get("player")));
		setBody(new Body(this.transform, WIDTH, HEIGHT).setEpsilon(0).setFriction(0f));
		
		element = Tag.FIRE;
		
		body.addTag(Tag.PLAYER.name());
		grounded = false;
	}
	
	public Tag getElement() {
		return element;
	}
	
	private void camMovement(float delta) {
		float dx = transform.getX() - Camera.getX() - Main.WIDTH / 2.0f;
		float dy = transform.getY() - Camera.getY() - Main.HEIGHT / 2.0f;
		
		float camMoveX = dx * delta * CAM_SPEED;
		float camMoveY = dy * delta * CAM_SPEED;
		
		if (dx < camMoveX)
			camMoveX = dx;
		
		if (dy < camMoveY)
			camMoveY = dy;
		
		Camera.translate(camMoveX, camMoveY);
	}
	
	public void p_update(float delta) {
		
		//CameraMovement
		camMovement(delta);
		
		//Ground Check
		if (body.isCollidingWith(Tag.GROUND.name())) {
			if (grounded == false) {
				Debug.log("Todo: Make landing slowdown, after you've fixed the physics engine.");
			}
			grounded = true;
		} else {
			grounded = false;
		}
		
		//Do all movement
		if (InputManager.wasPressed("p_jump") && grounded) {
			body.addVel(new VectorXf(0, JUMP_SPEED));
		}
		if (InputManager.wasPressed("p_left") || InputManager.isDown("p_left")) {
			body.setVel(new Vector2f(-MAX_SPEED, body.getVel().getY()));

			direction = -1;
		} else if (InputManager.wasPressed("p_right") || InputManager.isDown("p_right")) {
			body.setVel(new Vector2f(MAX_SPEED, body.getVel().getY()));

			direction = 1;
		} else if (grounded) {
			body.setVel(new Vector2f(0, body.getVel().getY()));
		}
		if (MAX_SPEED < Math.abs(body.getVel().dot(flatVector))) {
			body.setVel(new Vector2f(direction * MAX_SPEED, body.getVel().getY()));
		}
	}
	
	public int getDirection() {
		return direction;
	}
	
	public boolean isGrounded() {
		return grounded;
	}
	
}