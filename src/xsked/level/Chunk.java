package xsked.level;

import org.lwjgl.opengl.GL15;

import time.api.debug.Debug;
import time.api.entity.Entity;
import time.api.gfx.Mesh;
import time.api.gfx.Renderer;
import time.api.gfx.VertexTex;
import time.api.gfx.texture.Texture;

public class Chunk extends Entity {
	public static final int TILES = 5;
	public static final float SIZE = TILES * Tile.SIZE;
	
	private Tile[][] tiles;
	
	private Level level;
	
	protected int x, y;
	
	public Chunk(int x, int y, Level level) {
		this.level = level;
		
		this.x = x;
		this.y = y;
		
		
		int[] indices = new int[TILES * TILES * 6];
		VertexTex[] vertices = new VertexTex[TILES * TILES * 4];
		
		tiles = new Tile[TILES][TILES];
		for(int j = 0; j < TILES; j++) {
			for(int i = 0; i < TILES; i++) {
				Tile t = new Tile(level, this, i, j, 0, false, true);
				tiles[j][i] = t;
				VertexTex[] v = t.getVertices();
				
				indices[(i + TILES * j) * 6 + 0] = (i + TILES * j) * 4 + 0;
				indices[(i + TILES * j) * 6 + 1] = (i + TILES * j) * 4 + 1;
				indices[(i + TILES * j) * 6 + 2] = (i + TILES * j) * 4 + 2;
				indices[(i + TILES * j) * 6 + 3] = (i + TILES * j) * 4 + 0;
				indices[(i + TILES * j) * 6 + 4] = (i + TILES * j) * 4 + 2;
				indices[(i + TILES * j) * 6 + 5] = (i + TILES * j) * 4 + 3;
				
				for(int k = 0; k < v.length; k++)
					vertices[(i + TILES * j) * 4 + k] = v[k];
			}
		}
		
		setRenderer(new Renderer(new Mesh(GL15.GL_DYNAMIC_DRAW, vertices, indices), Texture.get("tilesheet")));
		transform.setPosition(x * SIZE - Tile.SIZE / 2, y * SIZE - Tile.SIZE / 2);
	}
	
	public Chunk setTile(int x, int y, Tile tile) {
		tile.x = x;
		tile.y = y;
		tiles[tile.y][tile.x] = tile;
		renderer.getMesh().changeData(tile.getVertices(), (tile.x + tile.y * TILES) * 4);
		tile.updateCollider();
		return this;
	}
	
	public Tile getTile(int x, int y) {
		return tiles[y][x];
	}
}
