package xsked.level;

import time.api.debug.Debug;
import time.api.entity.Entity;
import time.api.gfx.QuadRenderer;
import time.api.gfx.VertexTex;
import time.api.gfx.texture.Texture;
import time.api.math.Transform;
import time.api.math.Vector2f;
import time.api.math.Vector3f;
import time.api.physics.Body;

public class Tile extends Entity {
	
	public static final int TILES_PER_SHEET_DIM = 16;
	
	public static final int SIZE = 100;
	
	protected Level level;
	protected Chunk chunk;
	
	protected int x, y, spriteOffset;
	
	public Tile(Level level, Chunk chunk, int spriteOffset, boolean solid) {
		this(level, chunk, 0, 0, spriteOffset, solid, true);
	}
	
	public Tile(Level level, Chunk chunk,  int x, int y, int spriteOffset, boolean solid, boolean absolute) {
		this.level = level;
		this.chunk = chunk;
		this.spriteOffset = spriteOffset;
		this.x = x;
		this.y = y;
		
		transform = new Transform(0, 0);
		level.getPhysicsEngine().addBody(body);
		
		updateCollider();
	}
	
	public int getSpriteOffset() {
		return spriteOffset;
	}
	
	public VertexTex[] getVertices() {
		VertexTex[] data = new VertexTex[4];
		
		float sMin = (1f / TILES_PER_SHEET_DIM) * (spriteOffset % TILES_PER_SHEET_DIM);
		float sMax = sMin + 1f / TILES_PER_SHEET_DIM;
		float tMin = (1f / TILES_PER_SHEET_DIM) * Math.floorDiv(spriteOffset, TILES_PER_SHEET_DIM);
		float tMax = tMin + 1f / TILES_PER_SHEET_DIM;
		
		data[0] = new VertexTex(
			new Vector3f(x * Tile.SIZE, y * Tile.SIZE, 0), 
			new Vector2f(sMin, tMax)
		);
		
		data[1] = new VertexTex(
			new Vector3f(x * Tile.SIZE, (y + 1) * Tile.SIZE, 0), 
			new Vector2f(sMin, tMin)
		);
		
		data[2] = new VertexTex(
			new Vector3f((x + 1) * Tile.SIZE, (y + 1) * Tile.SIZE, 0), 
			new Vector2f(sMax, tMin)
		);
		
		data[3] = new VertexTex(
			new Vector3f((x + 1) * Tile.SIZE, y * Tile.SIZE, 0), 
			new Vector2f(sMax, tMax)
		);
		
		return data;
	}
	
	public void updateCollider() {
		transform.setPosition((x + chunk.x * Chunk.TILES) * SIZE, (y + chunk.y * Chunk.TILES) * SIZE);
	}
}