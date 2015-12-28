package xsked.level;

import time.api.debug.Debug;
import time.api.entity.EntityManager;
import time.api.gfx.texture.Texture;
import time.api.input.InputManager;
import time.api.physics.PhysicsEngine;

public class Level {
	
	private Tile[][] tiles;
	
	private int tilesX, tilesY;
	
	private int floor;
	private int seed;
	
	private Player player;
	private PhysicsEngine pe;
	private EntityManager em;
	
	public Level(int tilesX, int tilesY, int floor) {
		this.tilesX = tilesX;
		this.tilesY = tilesY;
		this.floor = floor;
		this.seed = 0;

		player = new Player(200, 400);
		
		pe = new PhysicsEngine();
		em = new EntityManager();
		
		pe.setGravity(0, -900);
		
		em.addEntity("Player", player);
		pe.addBody(player.getBody());
		
		generateLevel();
	}
	
//	public Level(int floor, int seed) {
//		this.floor = floor;
//		this.seed = seed;
//
//		player = new Player(0, 0);
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
	
	public PhysicsEngine getPhysicsEngien() {
		return pe;
	}
	
	public EntityManager getEntityManager() {
		return em;
	}
	
	private int psuedoRandom(int min, int max) {
		int range = max - min;
		seed %= 99;
		seed = (int) Math.pow((double)seed, 2.0);
		return (seed % range) + min;
	}
	
	private void generateLevel() {
		tiles = new Tile[tilesY][tilesX];
		
		em.addGroups("background", "floor");
		
		//Generate background
		for(int y = 0; y < tilesY; y++) {
			for(int x = 0; x < tilesX; x++) {
				Tile t = new Tile(this, x, y, Texture.get("tile_background"), false, true);
				em.addToGroup("background", t);
				tiles[y][x] = t;
			}
		}
		
		//Generate floor
		for(int x = 0; x < tilesX; x++) {
			Tile t = new GroundTile(this, x, 0, Texture.get("tile_stonebricks"));
			
			em.addToGroup("floor", t);			
			tiles[0][x] = t;
			
			//Add Bodies
			pe.addBody(t.getBody());
		}
		
		//remeber to set the player position to the start value!
//		player.setPosition(0, 0);
	}
	
	public void update(float delta) {
		em.update(delta);
		player.p_update(delta);
		pe.update(delta);
	}
	
	public void draw() {
		em.draw();
		player.draw();
	}
}