package xsked.level;

import java.util.ArrayList;
import java.util.Random;

import time.api.debug.Debug;
import time.api.entity.EntityManager;
import time.api.gamestate.GameStateManager;
import time.api.gfx.QuadRenderer;
import time.api.gfx.font.FontRenderer;
import time.api.gfx.font.FontType;
import time.api.gfx.texture.SpriteSheet;
import time.api.gfx.texture.Texture;
import time.api.input.InputManager;
import time.api.math.Vector2f;
import time.api.math.Vector3f;
import time.api.math.Vector4f;
import time.api.physics.Body;
import time.api.physics.Collision;
import time.api.physics.PhysicsEngine;
import time.api.util.Time;
import xsked.Main;
import xsked.level.Spell.SpellType;
import xsked.net.LevelSender;
import xsked.net.NetworkManager;
import xsked.net.PacketInitLevel;
import xsked.net.PacketSummonGhost;

public class Level {
	
	public static final int DRAW_DISTANCE = 2;
	
	public static final int BORDER_WIDTH = Tile.SIZE * 10;
	
	private static int currentFloor = 0;
	private static int currentSeed = 0;
	
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
	
	private Door door;
	
	private QuadRenderer background;
	
	//HUD - START
	
	private FontRenderer floorDisplay;
	
	private TextFader floorPrompt;
	private TextFader gameOverPrompt;
	private TextFader pressPrompt;
	
	//HUD - END
	private float ghostSpawnTimer;
	
	private Random rand;
	
	private boolean completed;
	
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
		this.seed = currentSeed;
		this.floor = currentFloor;
		
		player = new Player(this, 0, 200, mode);
		
		spells = new ArrayList<>();
		spellTrash = new ArrayList<>();
		
		ghosts = new ArrayList<>();
		ghostTrash = new ArrayList<>();
		
		floorDisplay = new FontRenderer(0, 0, "Floor: " + floor, FontType.FNT_ARIAL, .3f);
		floorDisplay.setPosition(Main.WIDTH - floorDisplay.getWidth(), Main.HEIGHT - floorDisplay.getAverageHeight());
		
		floorPrompt = new TextFader(Main.WIDTH / 2, Main.HEIGHT / 2,
				1, 1, 1, "Floor: " + floor, 2f);
		
		gameOverPrompt = new TextFader(Main.WIDTH / 2, Main.HEIGHT / 2,
				1, -1, 0, "Game Over", 2f);
		
		pressPrompt = new TextFader(Main.WIDTH / 2, Main.HEIGHT / 2 - 100,
				1, -1, 0, (NetworkManager.isServer() ? "Press 'R' to restart or" : "Press") + " 'E' to exit", .3f);
		
		background = new QuadRenderer(Main.WIDTH / 2, Main.HEIGHT / 2,
				Main.WIDTH, Main.HEIGHT,
				0, 0, .0625f, .0625f, Texture.get("tilesheet")
		);
		
		rand = new Random();
		
		pe = new PhysicsEngine();
		
		pe.useStep(false);
		
		pe.setGravity(0, -800);
		Collision.setMoveConstant(.1f);
		
		pe.addBody(player.getBody());
		
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
		
		//INITALIZING AWESOME LEVEL CRAETION ALGORITHM
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
		
		for(int i = 0; i < moves.length; i++) {
			
			Debug.log(currentX, currentY, "Stuff");

			currentX += dir * moves[i].getX();
			currentY += moves[i].getY();
			makePlatform(currentX, currentY, (int) moves[i].getZ(), dir);
			currentX += dir * moves[i].getZ();
			
			if(i == moves.length - 1)
				door = new Door(this, (currentX + 2) * Tile.SIZE, (currentY + 1.5f) * Tile.SIZE);
		}
		
		generateBorders();
	}
	
	private void generateBorders() {
		
		//Horizontal
		for(int i = 0; i < chunks.length * Chunk.TILES; i++) {
			setTile(i, 0, 12, true, true);
			((GroundTile) getTile(i, 0)).addTag(Tag.LETHAL);
			setTile(i, chunks[0].length * Chunk.TILES - 1, 12, false, true);
		}
		
		//Vertical
		for(int i = 0; i < chunks[0].length * Chunk.TILES; i++) {
			setTile(0, i, 12, false, true);
			setTile(chunks.length * Chunk.TILES - 1, i, 12, false, true);
		}
	}
	
	public void update(float delta) {
		
		if(!player.isDead()) {
			floorPrompt.update(delta);
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
			
			door.update(delta);
		} else {
			gameOverPrompt.update(delta);
			pressPrompt.update(delta);
			
			if(InputManager.wasPressed("restart") && NetworkManager.isServer()) {
				nextFloor(Player.MODE_WIZARD, 0, 0);
			}
			
			if(InputManager.wasPressed("exit")) {
				NetworkManager.stop();
			}
		}
		
		//Physics
		if(pe != null)
			pe.update(delta);
		player.p_update(delta);
	}
	
	public void setTile(int x, int y, int spriteOffset, boolean isGround, boolean solid) {
		Chunk c = getChunkByTile(x, y);
		if (isGround)
			c.setTile(x % Chunk.TILES, y % Chunk.TILES, new GroundTile(this, c, spriteOffset));
		else
			c.setTile(x % Chunk.TILES, y % Chunk.TILES, new Tile(this, c, spriteOffset, solid));
	}
	
	public void setTile(int x, int y, int spriteOffset, boolean isGround) {
		setTile(x, y, spriteOffset, isGround, false);
	}
	
	public Tile getTile(int x, int y) {
		return getChunkByTile(x, y).getTile(x % Chunk.TILES, y % Chunk.TILES);
	}
	
	public void makePlatform(int x, int y, int l, int dir) {
		for (int o = 0; o < l; o++) {
			Debug.log("Platform", (x + dir * o), y);
			setTile((int) (x + dir * o), y, 2, true);
		}
	}
	
	public void draw() {
		
		background.draw();
		Camera.push();
		
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
		
		door.draw();
		
		//HUD
		
		floorDisplay.draw();
		floorPrompt.draw();
		gameOverPrompt.draw();
		pressPrompt.draw();
		
		Camera.pop();
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
	
	public void nextFloor(int mode, int floor, int seed) {
		if(completed)
			return;
		
		completed = true;
		
		currentFloor = floor;
		currentSeed = seed;
		
		GameStateManager.enterState(mode == Player.MODE_APPRENTICE ? "Apprentice" : "Wizard");
		
		if(NetworkManager.isClient())
			return;
		
		LevelSender.sendPacket(new PacketInitLevel(
				mode == Player.MODE_APPRENTICE ?
						Player.MODE_WIZARD : Player.MODE_APPRENTICE, currentFloor, seed));
	}
	
	public Chunk getChunkByTile(int x, int y) {
		int cx = (int)Math.floorDiv(x, (Chunk.TILES));
		int cy = (int)Math.floorDiv(y, (Chunk.TILES));
		Debug.log("Cunk", x, y, cx, cy);
		return chunks[cx][cy];
	}
	
	public int getFloor() {
		return floor;
	}
	
	public Chunk getChunk(int x, int y) {
		return chunks[y][x];
	}
}