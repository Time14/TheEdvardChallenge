package xsked.level;

import java.util.ArrayList;
import java.util.Random;

import time.api.debug.Debug;
import time.api.entity.EntityManager;
import time.api.gfx.texture.Texture;
import time.api.input.InputManager;
import time.api.math.Vector2f;
import time.api.physics.Collision;
import time.api.physics.PhysicsEngine;
import time.api.util.Time;
import xsked.Main;
import xsked.level.Spell.SpellType;
import xsked.net.LevelSender;
import xsked.net.PacketSummonGhost;

public class Level {
	
	public static final int DRAW_DISTANCE = 2;
	
	private Chunk[][] chunks;
	
	private int tilesX, tilesY;
	
	private int floor;
	private int seed;
	
	private Player player;
	private PhysicsEngine pe;
	
	private ArrayList<Spell> spells;
	private ArrayList<Spell> spellTrash;
	
	private ArrayList<Ghost> ghosts;
	private ArrayList<Ghost> ghostTrash;
	private float ghostSpawnTimer;
	
	private Random rand;
	
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
	
	public Level(int mode) {
		player = new Player(0, 200, mode);
		
		spells = new ArrayList<>();
		spellTrash = new ArrayList<>();
		
		ghosts = new ArrayList<>();
		ghostTrash = new ArrayList<>();
		
		rand = new Random();
		
		pe = new PhysicsEngine();
		
		Collision.setMoveConstant(.3f);
		
		pe.useStep(false);
		
		pe.setGravity(0, -800);
		
		pe.addBody(player.getBody());
	}
	
	public Level(int floor, int seed, int mode) {
		this(mode);
		this.floor = floor;
		this.seed = seed;
		
		generateLevel();
	}
	
	public PhysicsEngine getPhysicsEngien() {
		return pe;
	}
	
	public Level generateEmpty(int width, int height) {
		chunks = new Chunk[height][width];
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				chunks[j][i] = new Chunk(i, j, this);
			}
		}
		
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < Chunk.TILES; j++) {
				chunks[0][i].setTile(j, 0, new GroundTile(this, chunks[0][i], 1));
			}
		}
		
		return this;
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
		
		int i = 0;
		while(true) {
			//Terminate the loop, I'm too tired to write it differently.
			if (length == i) break;
			
			//Generate Moves
			switch(psuedoRandom(0, Math.min(floor * 2 + 2, 12))) {
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
		
		//INITALIZING AWESOME LEVEL CRAETION ALGORYTHM
		//BOTTING INTO THE MATRIX
		
		int lastTileX, lastTileY;
		int currentX, currentY;
		
		tilesY = 0;
		tilesX = 0;
		
		lastTileX = lastTileY = currentX = currentY = 0;
		
		int minY, maxY;	
		minY = maxY = 0;
		
		for (Vector2f v : moves) {
			lastTileY += Math.floor(v.getX());
			if (lastTileY < minY)
				minY = lastTileY;
			if (maxY < lastTileY)
				maxY = lastTileY;
			
			lastTileX += Math.floor(v.getY());
		}
		
		tilesX += Math.abs(lastTileX) + Chunk.TILES * 2;
		tilesY += Math.abs(minY) + Math.abs(maxY) + Chunk.TILES * 2;
		
		
		chunks = new Chunk[(int) Math.floorDiv(tilesX, (int)Chunk.TILES) + 1][(int) Math.floorDiv(tilesY, (int)Chunk.TILES) + 1];
		
		for(int x = 0; x < chunks.length; x++) {
			for(int y = 0; y < chunks.length; y++) {
				chunks[x][y] = new Chunk(x, y, this);
			}
		}
		
		currentX = Chunk.TILES;
		currentY = Math.abs(minY);
		
		if (dir < 0) {
			currentX += lastTileX;
		}
		
		player.setPosition(currentX * Tile.SIZE, currentY * Tile.SIZE + Tile.SIZE * 2);
		
		for(int m = 0; m < moves.length; m++) {
			int tile = 1;
			if (m != 0) {
				if (moves[m - 1].getMagnitude() != 1.0f) {
					tile += 1;
				}
			} else if (m + 1 < moves.length) {
				if (moves[m + 1].getMagnitude() != 1.0f) {
					tile = 2;
				}
			}
			Debug.log("Coords", currentX, currentY);
			setTile(currentX, currentY, new GroundTile(this, getChunkByTile(currentX, currentY), tile));
			currentX += moves[m].getX();
			currentY += moves[m].getY();
		}
		
		
		
		
		
		
		//remeber to set the player position to the start value!
//		
	}
	
	public void update(float delta) {
		
		//Summon ghosts
				ghostSpawnTimer += delta;
				if(ghostSpawnTimer > 5f){
					ghostSpawnTimer = 0;
					
					SpellType type = SpellType.FIRE;
					
					switch(rand.nextInt(4)) {
					case 0:
						type = SpellType.EARTH;
						break;
					case 1:
						type = SpellType.FIRE;
						break;
					case 2:
						type = SpellType.WIND;
						break;
					case 3:
						type = SpellType.WATER;
						break;
					}
					
					float dx = Math.round(rand.nextFloat()) * 2 - 1;
					float dy = Math.round(rand.nextFloat()) * 2 - 1;
					float x = player.getX() + dx * Main.WIDTH;
					float y = player.getY() + dy * Main.HEIGHT;
					summonGhost(x, y, type);
					LevelSender.sendPacket(new PacketSummonGhost(x, y, type));
				}
		
		//Updating and trashing entities
		for(Ghost ghost : ghostTrash)
			ghosts.remove(ghosts.indexOf(ghost));
		ghostTrash.clear();
		
		for(Ghost ghost : ghosts)
			ghost.update(delta);
		
		for(Spell spell : spellTrash)
			spells.remove(spells.indexOf(spell));
		spellTrash.clear();
		
		for(Spell spell : spells)
			spell.update(delta);
		
		//Physics
		pe.update(delta);
		player.p_update(delta);
	}
	
	public void setTile(int x, int y, Tile tile) {
		int cx = Math.floorDiv(x, Chunk.TILES);
		int tx = (x - cx * Chunk.TILES);
		int cy = Math.floorDiv(y, Chunk.TILES);
		int ty = (y - cy * Chunk.TILES);
		
		chunks[cy][cx].setTile(tx, ty, tile);
	}
	
	public void draw() {
		int x = (int)Math.floor(player.getX() / (Chunk.TILES * Tile.SIZE));
		int y = (int)Math.floor(player.getY() / (Chunk.TILES * Tile.SIZE));
		for (int i = -DRAW_DISTANCE; i < DRAW_DISTANCE + 1; i++) {
			for (int j = -DRAW_DISTANCE; j < DRAW_DISTANCE + 1; j++) {
				try {
					chunks[y + i][x + j].draw();
				} catch (IndexOutOfBoundsException e) {
					continue;
				} catch (NullPointerException e) {
					continue;
				}
			}
		}
		player.draw();
		
		for(Ghost ghost : ghosts)
			ghost.draw();
		
		for(Spell spell : spells)
			spell.draw();
 	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void summonSpell(float x, float y, SpellType element, Vector2f dir) {
		spells.add(new Spell(this, x, y, element, Player.SPELL_SPEED, dir));
	}
	
	public void removeSpell(Spell spell) {
		spellTrash.add(spell);
	}
	
	public void summonGhost(float x, float y, SpellType element) {
		ghosts.add(new Ghost(this, x, y, element, player.MODE == Player.MODE_WIZARD));
	}
	
	public void removeGhost(Ghost ghost) {
		ghostTrash.add(ghost);
	}
	
	public Chunk getChunkByTile(int x, int y) {
		int cx = Math.floorDiv(x, Chunk.TILES);
		int cy = Math.floorDiv(y, Chunk.TILES);
		Debug.log(x, y, cx, cy);
		return chunks[cy][cx];
	}
	
	public Chunk getChunk(int x, int y) {
		return chunks[y][x];
	}
}