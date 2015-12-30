package xsked.level;

import time.api.entity.Entity;
import time.api.gfx.QuadRenderer;
import time.api.gfx.texture.Texture;
import time.api.math.Vector2f;
import time.api.physics.Body;
import xsked.level.Spell.SpellType;

public class Ghost extends Entity{
	
	public static final float SIZE = Tile.SIZE;
	
	public static final float SPEED = 100.0f;
	
	private Level level;
	private Player player;
	private SpellType weakness;
	
	private QuadRenderer icon;
	
	public Ghost(Level level, float x, float y, SpellType weakness, boolean showIcon) {
		this.level = level;
		this.player = level.getPlayer();
		this.weakness = weakness;
		
		this.setRenderer(new QuadRenderer(x, y, SIZE, SIZE, Texture.get("ghost")));
		body = new Body(transform, SIZE, SIZE).setTrigger(true).setAbsolute(true);
		body.addTag(Tag.LEATHAL.name());
		level.getPhysicsEngine().addBody(body);
		if(showIcon)
			icon = new QuadRenderer(0, 0, SIZE / 3, SIZE / 3, Texture.get("element_" + weakness.name().toLowerCase()));
	}
	
	public void setWeakness(SpellType element) {
		weakness = element;
	}
	
	public void update(float delta) {
		
		if(body.isCollidingWith(weakness.name())) {
			die();
		}
		
		Vector2f dp = new Vector2f(player.getX() - getX(), player.getY() - getY());
		dp.scale(((float) (1.0/dp.getMagnitude()) * SPEED));
		
		body.getPos().add(dp.scale(delta));
	}
	
	private void die() {
		this.renderer.getMesh().destroy();
		if(icon != null)
			icon.getMesh().destroy();
		level.removeGhost(this);
		level.getPhysicsEngine().removeBody(body);
	}
	
	public void draw() {
		super.draw();
		if(icon != null) {
			icon.setPosition(renderer.getX(), renderer.getY() + Tile.SIZE);
			icon.draw();
		}
	}
}	