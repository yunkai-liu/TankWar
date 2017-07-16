package message;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import main.TankClient;
import model.Tank;

public class TankDeadMessage implements Message 
{
	private int message_type = Message.TANK_DEAD_MESSAGE;
	
	private int id;
	private TankClient tc;
	
	public TankDeadMessage(TankClient tc)
	{
		this.tc = tc;
	}
	
	public TankDeadMessage(int id) 
	{
		this.id = id;
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
			
			for(int i=0; i<tc.getEnemyTanks().size(); i++)
			{
				Tank t  = tc.getEnemyTanks().get(i);
				if(t.getId() == id)
				{
					t.setLive(false);
					break;
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}