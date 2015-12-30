package xsked.net;

import sk.net.SKPacket;
import xsked.level.Spell.SpellType;

public class PacketSummonGhost extends SKPacket {
	
	public final float X;
	public final float Y;
	public final SpellType TYPE;
	
	public PacketSummonGhost(float x, float y, SpellType type) {
		this.X = x;
		this.Y = y;
		this.TYPE = type;
	}
	
	@Override
	public String getName() {
		return "Summon Ghost Packet";
	}
}