package xsked.level;

import java.util.HashMap;

import time.api.debug.Debug;
import time.api.entity.Entity;
import time.api.gfx.QuadRenderer;
import time.api.gfx.texture.Animation;
import time.api.gfx.texture.DynamicTexture;
import time.api.gfx.texture.Texture;
import time.api.input.InputManager;
import time.api.math.Vector2f;
import time.api.math.VectorXf;
import time.api.physics.Body;
import xsked.Main;
import xsked.level.Spell.SpellType;
import xsked.net.LevelSender;
import xsked.net.PacketPlayerDeath;

public class Player extends Entity{
	
	public static final int MODE_APPRENTICE = 0;
	public static final int MODE_WIZARD = 1;
	
	public static final float SPELL_SPEED = 200f;
	
	public static final float MAX_SPEED = 300f;
	
	public static final float JUMP_SPEED = 500.0f;
	
	public static final float CAM_SPEED = 7.5f;
	
	private static final float WIDTH = Tile.SIZE * 1.0f;
	private static final float HEIGHT = Tile.SIZE * 2.0f;
	
	public final int MODE;
	
	private SpellType element = SpellType.FIRE;
	
	private boolean grounded;
	
	private boolean dead;
	
	private int direction = 1;
	private VectorXf flatVector = new VectorXf(1, 0);
	
	private HashMap<String, Animation> animations;
	
	private Animation currentAnimation;
	
	private float speed;
	
	private boolean canMove = true;
	
	private QuadRenderer icon;
	
	private Level level;
	
	public Player(Level level, float x, float y, int mode) {
		setRenderer(new QuadRenderer(x, y, WIDTH, HEIGHT, Texture.getDT("player", true)));
		setBody(new Body(this.transform, WIDTH, HEIGHT).setEpsilon(0).setFriction(0f));
		
		this.MODE = mode;
		this.level = level;
		
		icon = new QuadRenderer(0, 0, WIDTH / 3, WIDTH / 3, Texture.get("element_" + element.name().toLowerCase()));
		
		initAnimations();
		
		body.addTag(Tag.PLAYER.name());
		grounded = false;
		dead = false;
	}
	
	public void switchElement(SpellType element) {
		this.element = element;
		icon.setTexture(Texture.get("element_" + element.name().toLowerCase()));
	}
	
	public SpellType getElement() {
		return element;
	}
	
	private void camMovement(float delta) {
		
		float dx = getX() - Camera.getX() - Main.WIDTH / 2;
		float dy = getY() - Camera.getY() - Main.HEIGHT / 2;
		
		float camMoveX = dx * delta * CAM_SPEED;
		float camMoveY = dy * delta * CAM_SPEED;
		
		if(dx < camMoveX)
			camMoveX = dx;
		if(dy < camMoveY)
			camMoveY = dy;
		
		Camera.translate(camMoveX, camMoveY);
	}
	
	public void p_update(float delta) {
		
		//CameraMovement
		camMovement(delta);
		
		if(dead) {
			currentAnimation.update(delta);
			return;
		}
		
		//Ground Check
		if (body.isCollidingWith(Tag.GROUND.name())) {
			if (grounded == false) {
				Debug.log("Todo: Make landing slowdown, after you've fixed the physics engine.");
			}
			grounded = true;
		} else {
			grounded = false;
		}
		
		//Do all movement
		if(canMove) {
			if (InputManager.wasPressed("p_jump") && grounded) {
				body.addVel(new VectorXf(0, JUMP_SPEED));
			}
			
			if (InputManager.wasPressed("p_left")) {
				setSpeed(-MAX_SPEED);
			} else if(InputManager.wasReleased("p_left") && direction == -1) {
				setSpeed(0);
			}
			
			if (InputManager.wasPressed("p_right")) {
				setSpeed(MAX_SPEED);
			} else if(InputManager.wasReleased("p_right") && direction == 1) {
				setSpeed(0);
			}
		}
		
		body.setVel(new Vector2f(speed, body.getVel().getY()));
		
		if(!isGrounded()) {
			if(!isAnimation("jump"))
				setAnimation("jump");
			
			if(body.getVel().getY() > -50 && body.getVel().getY() < 50) {
				currentAnimation.setMark(5);
			} else if(body.getVel().getY() > 0 && currentAnimation.getMark() != 1)
				currentAnimation.setMark(1);
			else if(body.getVel().getY() < 0 && currentAnimation.getMark() != 3)
				currentAnimation.setMark(3);
		} else {
			if(isAnimation("jump")) {
				if(speed == 0)
					setAnimation("idle");
				else
					setAnimation("running");
			}
		}
		
		if(!dead && body.isCollidingWith(Tag.LEATHAL.name())) {
			LevelSender.sendPacket(new PacketPlayerDeath());
			kill();
		}
		
		if (MAX_SPEED < Math.abs(body.getVel().dot(flatVector))) {
			body.setVel(new Vector2f(direction * MAX_SPEED, body.getVel().getY()));
		}
		
		currentAnimation.update(delta);
	}
	
	public void draw() {
		super.draw();
		if(icon != null) {
			icon.setPosition(getX(), getY() + WIDTH);
			icon.draw();
		}
	}
	
	public void setCanMove(boolean canMove) {
		this.canMove = canMove;
	}
	
	public int getDirection() {
		return direction;
	}
	
	public boolean isGrounded() {
		return grounded;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
		if(speed < 0)
			direction = -1;
		else if(speed > 0)
			direction = 1;
		
		if(speed == 0)
			setAnimation("idle");
		else
			setAnimation("running");
	}
	
	public void setAnimation(String animation) {
		String prefix = direction == 1 ? "r_" : "l_";
		
		currentAnimation = animations.get(prefix + animation);
		
		currentAnimation.reset();
	}
	
	public boolean isAnimation(String animation) {
		String prefix = direction == 1 ? "r_" : "l_";
		
		return animations.get(prefix + animation) == currentAnimation;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public void kill() {
		if(dead)
			return;
		dead = true;
		setAnimation("death");
		body.freezeVelocity();
		level.deletePhysicsEngine();
		level.clearGhosts();
		level.clearSpells();
		icon.getMesh().destroy();
		icon = null;
	}
	
	private void initAnimations() {
		
		animations = new HashMap<>();
		
		animations.put("r_running", new Animation((DynamicTexture) renderer.getTexture(),
				48, 49, 50, 51, 52, 53, 54, 55, 56, 57).setSpeed(20));
		
		animations.put("l_running", new Animation((DynamicTexture) renderer.getTexture(),
				127, 126, 125, 124, 123, 122, 121, 120, 119, 118).setSpeed(20));
		
		animations.put("r_idle", new Animation((DynamicTexture) renderer.getTexture(),
				32, 33, 34, 35).setSpeed(2f));
		
		animations.put("l_idle", new Animation((DynamicTexture) renderer.getTexture(),
				111, 110, 109, 108).setSpeed(2f));
		
		animations.put("r_jump", new Animation((DynamicTexture) renderer.getTexture(),
				5, 6, -1, 7, -1, 8).setSpeed(10));
		
		animations.put("l_jump", new Animation((DynamicTexture) renderer.getTexture(),
				74, 73, -1, 72, -1, 71).setSpeed(10));
		
		animations.put("r_death", new Animation((DynamicTexture)renderer.getTexture(),
				16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, -1).setSpeed(10));
		
		animations.put("l_death", new Animation((DynamicTexture)renderer.getTexture(),
				95, 94, 93, 92, 91, 90, 89, 88, 87, 86, 85, -1).setSpeed(10));
		
		setAnimation("idle");
	}
}