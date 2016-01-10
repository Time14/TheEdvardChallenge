package xsked.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import sk.net.SKClient;
import sk.net.SKServer;

public class NetworkManager {
	
	public static final int PORT = 6969;
	
	public static final int TYPE_CLIENT = 0;
	public static final int TYPE_SERVER = 1;
	
	public static final int ERR_NONE = 0;
	public static final int ERR_CLIENT = 1;
	public static final int ERR_SERVER = 2;
	public static final int ERR_IO = 3;
	public static final int ERR_UNKNOWN_HOST = 4;
	
	private static GameplayListener listener;
	private static SKServer server = new SKServer();
	private static SKClient client = new SKClient();
	
	private static volatile boolean connected = false;
	
	static {
		server.setMaxConnections(1);
		server.setTimeout(1000);
		
		listener = new GameplayListener();
		
		server.addPacketListener(listener);
		client.addPacketListener(listener);
	}
	
	private static int type;
	
	public static final int connect(String address) {
		if(type == TYPE_SERVER)
			return ERR_SERVER;
		
		try {
			client.connect(address, PORT);
		} catch (UnknownHostException e) {
			return ERR_UNKNOWN_HOST;
		} catch (IOException e) {
			return ERR_IO;
		}
		
		return ERR_NONE;
	}
	
	public static final int host() {
		if(type == TYPE_CLIENT)
			return ERR_CLIENT;
		
		try {
			server.start();
			server.bind("localhost", PORT);
		} catch (UnknownHostException e) {
			return ERR_UNKNOWN_HOST;
		} catch (IOException e) {
			return ERR_IO;
		}
		
		return ERR_NONE;
	}
	
	public static final SKServer getServer() {
		return server;
	}
	
	public static final SKClient getClient() {
		return client;
	}
	
	public static final boolean isClient() {
		return type == TYPE_CLIENT;
	}
	
	public static final boolean isServer() {
		return type == TYPE_SERVER;
	}
	
	public static final int getType() {
		return type;
	}
	
	public static final void stop() {
		if(type == TYPE_SERVER)
			server.stop("Disconnected by server");
		else if(type == TYPE_CLIENT)
			client.disconnect("Disconnected by client");
	}
	
	public static final void setConnected(boolean connected) {
		NetworkManager.connected = connected;
	}
	
	public static final boolean isConnected() {
		return connected;
	}
	
	public static final void setType(int type) {
		NetworkManager.type = type;
	}
}