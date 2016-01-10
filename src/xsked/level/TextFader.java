package xsked.level;

import time.api.entity.Entity;
import time.api.gfx.font.FontRenderer;
import time.api.gfx.font.FontType;
import time.api.math.Vector4f;

public class TextFader extends Entity {
	
	private boolean floorPromptTurnPoint = false;
	private boolean floorPromptDone = false;
	private float floorPromptTimer = 0;
	
	private float fadeInDuration;
	private float stayDuration;
	private float fadeOutDuration;
	
	public TextFader(float x, float y, float fadeInDuration, float stayDuration, float fadeOutDuration, String text, float size) {
		this.fadeInDuration = fadeInDuration;
		this.stayDuration = stayDuration;
		this.fadeOutDuration = fadeOutDuration;
		
		setRenderer(new FontRenderer(0, 0, text, FontType.FNT_ARIAL, size));
		transform.setPosition(x - ((FontRenderer)renderer).getWidth() / 2,
				y + ((FontRenderer)renderer).getAverageHeight());
		
		((FontRenderer)renderer).setColor(1, 1, 1, 0);
	}
	
	@Override
	public void update(float delta) {
		if(!floorPromptDone) {
			if(!floorPromptTurnPoint) {
				((FontRenderer)renderer).setColor((Vector4f) ((FontRenderer)renderer).getColor().add(new Vector4f(0, 0, 0, delta / fadeInDuration)));
				
				if(((FontRenderer)renderer).getColor().getW() >= 1) {
					if(stayDuration == -1)
						floorPromptDone = true;
					else
						floorPromptTurnPoint = true;
				}
			} else if(floorPromptTimer <= stayDuration){
				floorPromptTimer += delta;
			} else {
				((FontRenderer)renderer).setColor((Vector4f) ((FontRenderer)renderer).getColor().add(new Vector4f(0, 0, 0, -delta / fadeOutDuration)));
				
				if(((FontRenderer)renderer).getColor().getW() <= 0)
					floorPromptDone = true;
			}
		}
	}
}