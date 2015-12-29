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
			PacketPosition p = (PacketPosition) packet;
			if(p.TYPE.equals("player")) {
				EventQueue.getLevel().getPlayer().getBody().setPos(new Vector2f(p.X, p.Y));
				EventQueue.getLevel().getPlayer().getBody().setVel(
						new Vector2f(p.VX, p.VY));
				EventQueue.getLevel().getPlayer().setSpeed(p.VX);
			}
		}
	}
}