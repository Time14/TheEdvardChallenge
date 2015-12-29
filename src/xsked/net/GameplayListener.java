package xsked.net;

import sk.net.SKConnection;
import sk.net.SKPacket;
import sk.net.SKPacketListener;
import time.api.gamestate.GameStateManager;
import time.api.math.Vector2f;
import xsked.states.StateMenuHost;

public class GameplayListener implements SKPacketListener {
	
	@Override
	public void connected(SKConnection connection) {
		if(NetworkManager.isServer()) {
			((StateMenuHost) GameStateManager.getGameState("Host Menu")).setQueue(StateMenuHost.DISPLAY_CONNECTED);
			((StateMenuHost) GameStateManager.getGameState("Host Menu")).setQueue("start");
		}
		NetworkManager.setConnected(true);
	}
	
	@Override
	public void disconnected(SKConnection connection, boolean local, String msg) {
		
	}
	
	@Override
	public void received(SKConnection connection, SKPacket packet) {
		if(packet instanceof PacketState) {
			EventQueue.queue(packet);
		} else if(packet instanceof PacketPosition) {
			if(((PacketPosition) packet).TYPE.equals("player")) {
				EventQueue.getLevel().getPlayer().setPosition(((PacketPosition) packet).X, ((PacketPosition) packet).Y);
				EventQueue.getLevel().getPlayer().getBody().setVel(new Vector2f(((PacketPosition) packet).VX, ((PacketPosition) packet).VY));
			}
		}
	}
}