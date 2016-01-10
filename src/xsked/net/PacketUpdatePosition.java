package xsked.net;

import sk.net.SKPacket;

public class PacketUpdatePosition extends SKPacket {
	
	public final String TYPE;
	public final float X;
	public final float Y;
	
	public PacketUpdatePosition(String type, float x, float y) {
		this.TYPE = type;
		this.X = x;
		this.Y = y;
	}
	
	@Override
	public String getName() {
		return "Update Position Packet";
	}
}