package xsked.level;

import time.api.debug.Debug;
import time.api.entity.EntityManager;
import time.api.gfx.texture.Texture;
import time.api.input.InputManager;
import time.api.math.Vector2f;
import time.api.physics.PhysicsEngine;
import time.api.util.Time;

public class Level {
	
	public static final int DRAW_DISTANCE = 1;
	
	private Chunk[][] chunks;
	
	private int tilesX, tilesY;
	
	private int floor;
	private int seed;
	
	private Player player;
	private PhysicsEngine pe;
	
//	public Level(int tilesX, int tilesY, int floor) {
//		this.tilesX = tilesX;
//		this.tilesY = tilesY;
//		this.floor = floor;
//		this.seed = 0;
//
//		player = new Player(200, 400);
//		
//		pe = new PhysicsEngine();
//		em = new EntityManager();
//		
//		pe.setGravity(0, -900);
//		
//		em.addEntity("Player", player);
//		pe.addBody(player.getBody());
//		
//		generateLevel();
//	}
	
	public Level(int floor, int seed) {
		this.floor = floor;
		this.seed = seed;

		player = new Player(0, 0);
		
		pe = new PhysicsEngine();
		
		pe.setGravity(0, -800);
		
		pe.addBody(player.getBody());
		
		generateLevel();
	}
	
	public PhysicsEngine getPhysicsEngien() {
		return pe;
	}
	
	private int psuedoRandom(int min, int max) {
		int range = max - min;
		seed %= 101;
		seed = (int) Math.floor(101 * 0.5 * (Math.sin(Math.pow(seed, 2)) + 1));
		return (int) (Math.floor(seed % range) + min);
	}
	
	private void generateLevel() {
		
		//Generate path
		//Jump events
		//Walk, left/Right
		
		int length = Math.min(floor * 2, 20);
		Vector2f[] moves = new Vector2f[length];
		int dir = ((floor % 2) * 2) - 1;
		
		int f = 0;
		for(int i = 0; i < 100000; i++)
			f += psuedoRandom(0, 100);
		
		Debug.log("f_avg: ", f / 100000.0);
		
		int i = 0;
		while(true) {
			//Terminate the loop, I'm to tierd to write it differently.
			if (length == i) break;
			
			//Generate Moves
			switch(psuedoRandom(0, Math.min(floor * 2, 12))) {
			case(0):
				moves[i] = new Vector2f(dir * 1, 0);
				break;	
			case(1):
				moves[i] = new Vector2f(dir * 2, 1);
				break;
			case(2):
				moves[i] = new Vector2f(dir * 1, -1);
				break;
			case(3):
				moves[i] = new Vector2f(dir * 4, 0);
				break;
			case(4):
				moves[i] = new Vector2f(dir * 2, 2);
				break;
			case(5):
				moves[i] = new Vector2f(dir * 3, 2);
				break;
			case(6):
				moves[i] = new Vector2f(dir * 4, -1);
				break;
			case(7):
				moves[i] = new Vector2f(dir * 2, 2);
				break;
			case(8):
				moves[i] = new Vector2f(dir * 5, -4);
				break;
			case(9):
				moves[i] = new Vector2f(dir * 3, 2);
				break;
			case(10):
				moves[i] = new Vector2f(dir * 1, -3);
				break;
			case(11):
				moves[i] = new Vector2f(dir * 5, -6);
				break;
			default:
				moves[i] = new Vector2f(0, 0);
				break;
			}
			i++;
		}
		
		tilesY = 0;
		tilesX = 0;
		for (Vector2f v : moves) {
			tilesX += Math.floor(v.getX());
			tilesY += Math.floor(v.getY());
		}
		
		tilesY += 4 + Chunk.TILES * 2;
		tilesX += 4 + Chunk.TILES * 2;
		
		chunks = new Chunk[(int) Math.floor(tilesX / Chunk.SIZE)][(int) Math.floor(tilesY / Chunk.SIZE)];
		
		Debug.log("Tiles", tilesX, tilesY);
		Debug.log("I am setting the tile dimensionns to something else, remove this when not debugging.");
		
		//Generate background
		for(int y = 0; y < tilesY; y++) {
			for(int x = 0; x < tilesX; x++) {
				Tile t = new Tile(this, x, y, 0, false, true);
//				tiles[y][x] = t;
			}
		}
		
		//Generate floor
		for(int x = 0; x < tilesX; x++) {
			Tile t = new GroundTile(this, x, 0, 1);
			
//			tiles[0][x] = t;
			
			//Add Bodies
			pe.addBody(t.getBody());
		}
		
		//remeber to set the player position to the start value!
		player.setPosition(200, 400);
	}
	
	public void update(float delta) {
		player.p_update(delta);
		pe.update(delta);
	}
	
	public void setTile() {
		
	}
	
	public void draw() {
		int x = Math.round(player.getX() / (Chunk.TILES * Tile.SIZE));
		int y = Math.round(player.getY() / (Chunk.TILES * Tile.SIZE));
		for (int i = -DRAW_DISTANCE; i < DRAW_DISTANCE + 1; i++) {
			for (int j = -DRAW_DISTANCE; j < DRAW_DISTANCE + 1; j++) {
				chunks[i + x][j + y].draw();
			}
		}
		player.draw();
	}
}