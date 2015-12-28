package xsked.level;

import time.api.entity.Entity;
import time.api.gfx.QuadRenderer;
import time.api.gfx.texture.Texture;
import time.api.physics.Body;

public class Tile extends Entity {
	
	public static final int SIZE = 100;
	
	private boolean solid;
	
	protected Level level;
	
	public Tile(Level level, int x, int y, Texture texture, boolean solid, boolean absolute) {
		setRenderer(new QuadRenderer(x * SIZE, y * SIZE, SIZE, SIZE, texture));
		body = new Body(transform, SIZE, SIZE).setTrigger(!solid).setAbsolute(absolute).setEpsilon(0).setFriction(10);
		this.level = level;
	}
}