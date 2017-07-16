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
import model.Missile;

public class MissileNewMessage implements Message 
{
	private int message_type = Message.MISSILE_NEW_MESSAGE;
	
	private Missile missile;
	
	private TankClient tc;
	
	public MissileNewMessage(Missile missile)
	{
		this.missile = missile;
	}
	
	public MissileNewMessage(TankClient tc)
	{
		this.tc = tc;
	}
	
	@Override
	public void send(DatagramSocket ds, String ip, int udp_port) 
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		try
		{
			dos.writeInt(message_type);
			dos.writeInt(missile.getTankID());
			dos.writeInt(missile.getMissileID());
			dos.writeInt(missile.getX());
			dos.writeInt(missile.getY());
			dos.writeInt(missile.getDirection().ordinal());
			dos.writeBoolean(missile.isGood());
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
			int tankID = dis.readInt();
			
			if(tankID == tc.getMyTank().getId())
			{
				return ;
			}
			
			int id = dis.readInt();
			int x = dis.readInt();
			int y = dis.readInt();
			Direction direction = Direction.values()[dis.readInt()];
			boolean good = dis.readBoolean();
			
			Missile m = new Missile(tankID, x, y, good, direction, tc);
			m.setMissileID(id);
			tc.getMissiles().add(m);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}