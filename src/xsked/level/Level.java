package xsked.level;

import time.api.entity.EntityManager;
import time.api.gfx.texture.Texture;
import time.api.input.InputManager;

public class Level {
	
	private Tile[][] tiles;
	
	private int tilesX, tilesY;
	
	private int floor;
	
	private EntityManager em;
	
	public Level(int tilesX, int tilesY, int floor) {
		this.tilesX = tilesX;
		this.tilesY = tilesY;
		this.floor = floor;
		
		em = new EntityManager();
		
		generateLevel();
	}
	
	private void generateLevel() {
		tiles = new Tile[tilesY][tilesX];
		
		em.addGroups("background", "floor");
		
		//Generate background
		for(int y = 0; y < tilesY; y++) {
			for(int x = 0; x < tilesX; x++) {
				Tile t = new Tile(x, y, Texture.get("tile_background"), false, true);
				em.addToGroup("background", t);
				tiles[y][x] = t;
			}
		}
		
		//Generate floor
		for(int x = 0; x < tilesX; x++) {
			Tile t = new Tile(x, 0, Texture.get("tile_stonebricks"), true, true);
			em.addToGroup("floor", t);
			tiles[0][x] = t;
		}
	}
	
	public void update(float delta) {
		em.update(delta);
		
		if(InputManager.isDown("up")) {
			Camera.translate(0, delta * Player.SPEED);
		}
		if(InputManager.isDown("down")) {
			Camera.translate(0, -delta * Player.SPEED);
		}
		if(InputManager.isDown("left")) {
			Camera.translate(-delta * Player.SPEED, 0);
		}
		if(InputManager.isDown("right")) {
			Camera.translate(delta * Player.SPEED, 0);
		}
	}
	
	public void draw() {
		em.draw();
	}
}