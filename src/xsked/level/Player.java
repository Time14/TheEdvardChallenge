package xsked.level;

import time.api.debug.Debug;
import time.api.entity.Entity;
import time.api.gfx.QuadRenderer;
import time.api.gfx.texture.Texture;
import time.api.input.InputManager;
import time.api.math.VectorXf;
import time.api.physics.Body;

public class Player extends Entity{
	
	public static final float SPEED = 200f;
	
	public static final float JUMP_SPEED = 200.0f;
	
	private static final float WIDTH = Tile.SIZE * 1.0f;
	private static final float HEIGHT = Tile.SIZE * 2.0f;
	
	public Player(float x, float y) {
		setRenderer(new QuadRenderer(x, y, WIDTH, HEIGHT, Texture.get("player")));
		setBody(new Body(this.transform, WIDTH, HEIGHT).setEpsilon(0).setFriction(0));
		
		body.addTag(Tag.PLAYER.name());
	}
	
	public void p_update(float delta) {
		if (body.isCollidingWith(Tag.GROUND.name()))
			Debug.log("Yo");
		if (InputManager.wasPressed("p_jump") && body.isCollidingWith(Tag.GROUND.name())) {
			Debug.log("Hello world!");
			body.addVel(new VectorXf(0, JUMP_SPEED));
		}
	}
	
}