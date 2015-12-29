package xsked.net;

import sk.net.SKPacket;
import time.api.math.Vector2f;

public class PacketPosition extends SKPacket {
	
	public final float X;
	public final float Y;
	public final float ID;
	public final String TYPE;
	public final float VX;
	public final float VY;
	
	
	public PacketPosition(float x, float y, int id, String type, float vx, float vy) {
		this.X = x;
		this.Y = y;
		this.ID = id;
		this.TYPE = type;
		this.VX = vx;
		this.VY = vy;
	}
	
	@Override
	public String getName() {
		return "Position Packet";
	}
}