package xsked.level;

import time.api.gfx.texture.Texture;
import time.api.physics.Body;

public class GroundTile extends Tile{

	public static final float HEIGHT = 5.0f;
	
	public GroundTile(Level level, int x, int y, Texture texture) {
		super(level,x, y, texture, true, true);
		Body b = new Body(this.getX(), this.getY() + (Tile.SIZE + HEIGHT)/2.0f, Tile.SIZE * 0.9f, HEIGHT)
				.setTrigger(true).setAbsolute(true);
		b.addTag(Tag.GROUND.name());
		level.getPhysicsEngien().addBody(b);
	}
	
}
