package message;

import java.io.DataInputStream;
import java.net.DatagramSocket;

public interface Message 
{
	public static final int TANK_NEW_MESSAGE = 1;
	public static final int TANK_MOVE_MESSAGE = 2;
	public static final int MISSILE_NEW_MESSAGE = 3;
	public static final int TANK_DEAD_MESSAGE = 4;
	public static final int MISSILE_DEAD_MESSAGE = 5;
	
	public void send(DatagramSocket ds, String ip, int udp_port);
	public void parse(DataInputStream dis);
}