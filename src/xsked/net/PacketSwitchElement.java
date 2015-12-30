package xsked.net;

import sk.net.SKPacket;
import xsked.level.Spell.SpellType;

public class PacketSwitchElement extends SKPacket {
	
	public final SpellType ELEMENT;
	
	public PacketSwitchElement(SpellType element) {
		this.ELEMENT = element;
	}
	
	@Override
	public String getName() {
		return "Switch Element Packet";
	}
}