package xsked.level;

import time.api.entity.Entity;
import time.api.gfx.QuadRenderer;
import time.api.gfx.texture.Texture;
import time.api.physics.Body;

public class Border extends Entity {
	
	public Border(float x, float y, float width, float height) {
		setRenderer(new QuadRenderer(x, y, width, height, Texture.DEFAULT_TEXTURE));
		body = new Body(transform, width, height).setAbsolute(true);
	}
}