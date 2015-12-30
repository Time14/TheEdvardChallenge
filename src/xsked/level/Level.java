package xsked.level;

import java.util.ArrayList;
import java.util.Random;

import time.api.debug.Debug;
import time.api.entity.EntityManager;
import time.api.gfx.texture.Texture;
import time.api.input.InputManager;
import time.api.math.Vector2f;
import time.api.math.Vector3f;
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
		player = new Player(this, 0, 200, mode);
		
		spells = new ArrayList<>();
		spellTrash = new ArrayList<>();
		
		ghosts = new ArrayList<>();
		ghostTrash = new ArrayList<>();
		
		rand = new Random();
		
		pe = new PhysicsEngine();
		
		pe.useStep(false);
		Collision.setMoveConstant(0.3f);
		
		pe.setGravity(0, -800);
		Collision.setMoveConstant(0.3f);
		
		pe.addBody(player.getBody());
	}
	
	public Level(int floor, int seed, int mode) {
		this(mode);
		this.floor = floor;
		this.seed = seed;
		
		generateLevel();
	}
	
	public void deletePhysicsEngine() {
		pe = null;
	}
	
	public PhysicsEngine getPhysicsEngine() {
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
		
		//Probs static later
		int startWidth = 3;
		
		int length = Math.min(floor * 2 + 2, 10);
		Vector3f[] moves = new Vector3f[length];
		int dir = ((floor % 2) * 2) - 1;
//		dir = 1;

		for (int i = 0; i < moves.length; i++) {
			int r = psuedoRandom(2, Math.min(floor + 4, 5));
			int l = psuedoRandom(1, 3);
			if (i == moves.length - 1) l = startWidth;
			int dx = psuedoRandom(l, Math.min(floor + 1 + l, 5));
			int dy = (int) ((psuedoRandom(0, 1) * 2 - 1) * Math.sqrt(Math.abs(r * r - dx * dx)));
			
			moves[i] = new Vector3f(dx, dy, l);
		}
		
		//INITALIZING AWESOME LEVEL CRAETION ALGORYTHM
		//BOTTING INTO THE MATRIX
		
		
		
		
		int minY = 0;
		int maxY = 0;
		
		int lastTileX = 0;
		int lastTileY = 0;
		for (Vector3f m : moves) {
			lastTileX += m.getX() + m.getZ();
			
			lastTileY += m.getY();
			
			if (lastTileY < minY) minY = lastTileY;
			if (maxY < lastTileY) maxY = lastTileY;
		}
		
		//Probs static later.
		int padding = 2;
		
		tilesX = Math.abs(lastTileX);
		tilesY = Math.abs(minY) + Math.abs(maxY);
		
		chunks = new Chunk[(int) Math.ceil((float)tilesX / (float)Chunk.TILES) + 2 * padding][(int) Math.ceil((float)tilesY / (float)Chunk.TILES) + 2 * padding];
		
		for(int a = 0; a < chunks.length; a++) {
			for(int b = 0; b < chunks[a].length; b++) {
				chunks[a][b] = new Chunk(a, b, this);
			}
		}
		
		int currentX = (int) (Chunk.TILES * padding);
		int currentY = (int) (Math.abs(minY) + (Chunk.TILES * padding));
		
		if (dir < 0) {
			currentX += lastTileX;
		}
		
		makePlatform(currentX - dir * startWidth, currentY, startWidth, dir);
		
		player.setPosition(((currentX - dir * startWidth * 0.5f + 0.5f) * Tile.SIZE), (currentY + 2) * Tile.SIZE);
		Camera.setPosition(player.getX(), player.getY());
		
		for(Vector3f move : moves) {
			
			Debug.log(currentX, currentY, "Stuff");

			currentX += dir * move.getX();
			currentY += move.getY();
			makePlatform(currentX, currentY, (int) move.getZ(), dir);
			currentX += dir * move.getZ();
		}
	}
	
	public void update(float delta) {		
		
		//Summon ghosts
		if(!player.isDead()) {
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
		}
		
		//Physics
		if(pe != null)
			pe.update(delta);
		player.p_update(delta);
	}
	
	public void setTile(int x, int y, int spriteOffset, boolean isGround) {
		Chunk c = getChunkByTile(x, y);
		if (isGround)
			c.setTile(x % Chunk.TILES, y % Chunk.TILES, new GroundTile(this, c, spriteOffset));
		else
			c.setTile(x % Chunk.TILES, y % Chunk.TILES, new Tile(this, c, spriteOffset, false));
	}
	
	public void makePlatform(int x, int y, int l, int dir) {
		for (int o = 0; o < l; o++) {
			Debug.log("Platform", (x + dir * o), y);
			setTile((int) (x + dir * o), y, 2, true);
		}
	}
	
	public void draw() {
		int x = (int)Math.floor(player.getX() / (Chunk.TILES * Tile.SIZE));
		int y = (int)Math.floor(player.getY() / (Chunk.TILES * Tile.SIZE));
		for (int i = -DRAW_DISTANCE; i < DRAW_DISTANCE + 1; i++) {
			for (int j = -DRAW_DISTANCE; j < DRAW_DISTANCE + 1; j++) {
				try {
					chunks[x + i][y + j].draw();
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
	
	public void clearSpells() {
		spells.clear();
	}
	
	public void summonGhost(float x, float y, SpellType element) {
		ghosts.add(new Ghost(this, x, y, element, player.MODE == Player.MODE_WIZARD));
	}
	
	public void removeGhost(Ghost ghost) {
		ghostTrash.add(ghost);
	}
	
	public void clearGhosts() {
		ghosts.clear();
	}
	
	public Chunk getChunkByTile(int x, int y) {
		int cx = (int)Math.floorDiv(x, (Chunk.TILES));
		int cy = (int)Math.floorDiv(y, (Chunk.TILES));
		Debug.log("Cunk", x, y, cx, cy);
		return chunks[cx][cy];
	}
	
	public Chunk getChunk(int x, int y) {
		return chunks[y][x];
	}
}