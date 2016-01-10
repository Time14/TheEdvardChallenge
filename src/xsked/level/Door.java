package xsked.level;

import java.util.Random;

import time.api.entity.Entity;
import time.api.gfx.QuadRenderer;
import time.api.gfx.texture.Texture;
import time.api.physics.Body;
import xsked.net.NetworkManager;

public class Door extends Entity {
	
	public static final int SIZE = Tile.SIZE * 2;
	
	private Level level;
	
	public Door(Level level, float x, float y) {
		this.level = level;
		
		setRenderer(new QuadRenderer(x, y, SIZE, SIZE, Texture.get("door")));
		
		body = new Body(transform, SIZE, SIZE).setTrigger(true).setAbsolute(true);
		body.addTag(Tag.DOOR.name());
		
		level.getPhysicsEngine().addBody(body);
	}
	
	@Override
	public void update(float delta) {
		if(body.isCollidingWith(Tag.PLAYER.name()) && NetworkManager.isServer()) {
			level.nextFloor(level.getPlayer().MODE == Player.MODE_APPRENTICE ?
			Player.MODE_WIZARD : Player.MODE_APPRENTICE,
			level.getFloor() + 1, new Random().nextInt());
		}
	}
}