package xsked.level;

import time.api.debug.Debug;
import time.api.gfx.texture.Texture;
import time.api.math.Vector2f;
import time.api.physics.Body;

public class GroundTile extends Tile{

	public static final float HEIGHT = 5.0f;
	
	private Body groundTrigger;
	
	public GroundTile(Level level, Chunk chunk, int spriteOffset) {
		this(level, chunk, 0, 0, spriteOffset);
	}
	
	public GroundTile(Level level, Chunk chunk, int x, int y, int spriteOffset) {
		super(level, chunk, x, y, spriteOffset, true, true);
		body = new Body(transform, Tile.SIZE, Tile.SIZE);
		body.setAbsolute(true);
		groundTrigger = new Body(0, 0, Tile.SIZE * 0.9f, HEIGHT)
				.setTrigger(true).setAbsolute(true);
		groundTrigger.addTag(Tag.GROUND.name());
		level.getPhysicsEngine().addBody(groundTrigger);
		level.getPhysicsEngine().addBody(body);
		
		updateCollider();
	}
	
	public void updateCollider() {
		super.updateCollider();
		
		if(groundTrigger == null) return;
		
		groundTrigger.setPos(new Vector2f((x + chunk.x * Chunk.TILES) * Tile.SIZE,
				y * Tile.SIZE + (Tile.SIZE + HEIGHT)/2.0f + chunk.y * Chunk.SIZE));
	}
	
}
