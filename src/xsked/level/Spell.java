package xsked.level;

import java.util.HashMap;

import time.api.debug.Debug;
import time.api.entity.Entity;
import time.api.gfx.QuadRenderer;
import time.api.gfx.texture.Animation;
import time.api.gfx.texture.DynamicTexture;
import time.api.gfx.texture.Texture;
import time.api.math.Vector2f;
import time.api.physics.Body;

public class Spell extends Entity {
	
	public static final float MAX_HEALTH = 3f;
	
	public static final float SIZE = Tile.SIZE / 2;
	
	public final SpellType TYPE;
	
	private Level level;
	
	private float speed;
	
	private Vector2f dir;
	
	private HashMap<String, Animation> animations;
	private Animation currentAnimation;
	
	private float health = MAX_HEALTH;
	
	public Spell(Level level, float x, float  y, SpellType type, float speed, Vector2f dir) {
		this.level = level;
		this.TYPE = type;
		this.speed = speed;
		this.dir = dir;
		
		setRenderer(new QuadRenderer(x, y, SIZE, SIZE, Texture.getDT("spells", true)));
		body = new Body(transform, SIZE, SIZE).setTrigger(true).setAbsolute(true);
		body.addTag(type.name());
		level.getPhysicsEngine().addBody(body);
		
//		transform.setRotation((float) Math.toDegrees(Math.asin(Math.abs(dir.getY() / dir.getX()))));
		
		initAnimations();
	}
	
	public void update(float delta) {
		body.getPos().add(dir.clone().scale(speed * delta));
		health -= delta;
		
		if(body.isCollidingWith(Tag.LETHAL.name()))
			health = 0;
		
		currentAnimation.update(delta);
		
		if(health <= 0) {
			renderer.getMesh().destroy();
			Debug.log("TODO: remove spell bodies from physics engine");
			level.removeSpell(this);
			level.getPhysicsEngine().removeBody(body);
		}
	}
	
	public void setAnimation(String animation) {
		String prefix = dir.getX() > 0 ? "r_" : "l_";
		
		currentAnimation = animations.get(prefix + animation + "_" + TYPE.name().toLowerCase());
		
		currentAnimation.reset();
	}
	
	public boolean isAnimation(String animation) {
		String prefix = dir.getX() > 0 ? "r_" : "l_";
		
		return animations.get(prefix + animation + "_" + TYPE.name().toLowerCase()) == currentAnimation;
	}
	
	private void initAnimations() {
		
		animations = new HashMap<>();
		
		//earth
		animations.put("r_idle_earth", new Animation((DynamicTexture) renderer.getTexture(),
				0, 1, 2, 3).setSpeed(8));
		
		animations.put("l_idle_earth", new Animation((DynamicTexture) renderer.getTexture(),
				143, 142, 141, 140).setSpeed(8));
		
		//fire
		animations.put("r_idle_fire", new Animation((DynamicTexture) renderer.getTexture(),
				16, 17, 18, 19, 20, 21, 22, 23).setSpeed(14));
		
		animations.put("l_idle_fire", new Animation((DynamicTexture) renderer.getTexture(),
				159, 158, 157, 156, 155, 154, 153, 152).setSpeed(14));
		//wind
		animations.put("r_idle_wind", new Animation((DynamicTexture) renderer.getTexture(),
				48, 49, 50, 51).setSpeed(8));
		
		animations.put("l_idle_wind", new Animation((DynamicTexture) renderer.getTexture(),
				191, 190, 189, 188).setSpeed(8));
		//water
		animations.put("r_idle_water", new Animation((DynamicTexture) renderer.getTexture(),
				32, 33, 34, 35).setSpeed(8));
		
		animations.put("l_idle_water", new Animation((DynamicTexture) renderer.getTexture(),
				175, 174, 173, 172).setSpeed(8));
		
		
		setAnimation("idle");
	}
	
	public enum SpellType {
		EARTH("spell_earth"),
		WIND("spell_wind"),
		WATER("spell_water"),
		FIRE("spell_fire");
		
		public final String TEXTURE_NAME;
		
		SpellType(String textureName) {
			this.TEXTURE_NAME = textureName;
		}
	}
}
