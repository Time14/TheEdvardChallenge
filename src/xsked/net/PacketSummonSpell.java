package xsked.net;

import sk.net.SKPacket;
import xsked.level.Spell.SpellType;

public class PacketSummonSpell extends SKPacket {
	
	public final float X;
	public final float Y;
	public final SpellType TYPE;
	public final float SPEED;
	public final float DX;
	public final float DY;
	
	public PacketSummonSpell(float x, float y, SpellType type, float speed, float dx, float dy) {
		this.X = x;
		this.Y = y;
		this.TYPE = type;
		this.SPEED = speed;
		this.DX = dx;
		this.DY = dy;
	}
	
	@Override
	public String getName() {
		return "Summon Spell Packet";
	}
	
}