package xsked.net;

import sk.net.SKPacket;

public class PacketState extends SKPacket {
	
	public final String STATE;
	
	public PacketState(String state) {
		STATE = state;
	}
	
	@Override
	public String getName() {
		return "State Packet";
	}
}