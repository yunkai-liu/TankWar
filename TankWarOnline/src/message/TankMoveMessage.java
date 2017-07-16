package message;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import enums.Direction;
import main.TankClient;
import model.Tank;

public class TankMoveMessage implements Message 
{
	private int message_type = Message.TANK_MOVE_MESSAGE;
	
	private int id;
	private Direction shotDirection;
	private Direction direction;
	private int x;
	private int y;
	
	private TankClient tc;
	
	public TankMoveMessage(TankClient tc)
	{
		this.tc = tc;
	}
	
	public TankMoveMessage(int id, Direction direction, int x, int y, Direction shotDirection) 
	{
		this.id = id;
		this.direction = direction;
		this.x = x;
		this.y = y;
		this.shotDirection = shotDirection;
	}
	
	@Override
	public void send(DatagramSocket ds, String ip, int udp_port) 
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		try
		{
			dos.writeInt(message_type);
			dos.writeInt(id);
			dos.writeInt(x);
			dos.writeInt(y);
			dos.writeInt(direction.ordinal());
			dos.writeInt(shotDirection.ordinal());
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		byte[] buffer = baos.toByteArray();
		DatagramPacket dp = new DatagramPacket(buffer, buffer.length, new InetSocketAddress(ip, udp_port));
		try 
		{
			ds.send(dp);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void parse(DataInputStream dis) 
	{
		try 
		{
			int id = dis.readInt();
			
			if(tc.getMyTank().getId() == id)
			{
				return ;
			}
			
			int x = dis.readInt();
			int y = dis.readInt();
			Direction direction = Direction.values()[dis.readInt()];
			Direction shotDirection = Direction.values()[dis.readInt()];
			
			for(int i=0; i<tc.getEnemyTanks().size(); i++)
			{
				Tank t  = tc.getEnemyTanks().get(i);
				if(t.getId() == id)
				{
					t.setX(x);
					t.setY(y);
					t.setDirection(direction);
					t.setShotDirection(shotDirection);
					break;
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}	
	
	public int getMessage_type() 
	{
		return message_type;
	}
}