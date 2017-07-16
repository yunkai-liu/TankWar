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

public class TankNewMessage implements Message
{
	private int message_type = Message.TANK_NEW_MESSAGE;

	private Tank t;
	private TankClient tc;

	public TankNewMessage(TankClient tc)
	{
		this.tc = tc;
	}
	
	public TankNewMessage(Tank t)
	{
		this.t = t;
	}
	
	public void send(DatagramSocket ds, String ip, int udp_port)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		try
		{
			dos.writeInt(message_type);
			dos.writeInt(t.getId());
			dos.writeInt(t.getX());
			dos.writeInt(t.getY());
			dos.writeInt(t.getDirection().ordinal());
			dos.writeInt(t.getShotDirection().ordinal());
			dos.writeBoolean(t.isGood());
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
			boolean good = dis.readBoolean();
			
			boolean exist = false;
			for(int i=0; i<tc.getEnemyTanks().size(); i++)
			{
				Tank t = tc.getEnemyTanks().get(i);
				if(t.getId() == id)
				{
					exist = true;
					break;
				}
			}
			
			if(!exist)
			{
				Message mess = new TankNewMessage(tc.getMyTank());
				tc.getNc().send(mess);
				
				Tank t = new Tank(x, y, good, direction, tc);
				t.setId(id);
				t.setShotDirection(shotDirection);
				tc.getEnemyTanks().add(t);
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