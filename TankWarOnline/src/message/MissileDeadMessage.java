package message;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import main.TankClient;
import model.Exlopde;
import model.Missile;
import model.Tank;

public class MissileDeadMessage implements Message 
{
	private int message_type = Message.MISSILE_DEAD_MESSAGE;
	
	private int id;
	private int tankID;
	private int tankIdShotted;
	private int tankLifeShotted;
	private TankClient tc;
	
	public MissileDeadMessage(TankClient tc)
	{
		this.tc = tc;
	}
	
	public MissileDeadMessage(int id, int tankID, int tankIdShotted, int tankLifeShotted) 
	{
		this.id = id;
		this.tankID = tankID;
		this.tankIdShotted = tankIdShotted;
		this.tankLifeShotted = tankLifeShotted;
	}
	
	@Override
	public void send(DatagramSocket ds, String ip, int udp_port) 
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		try
		{
			dos.writeInt(message_type);
			dos.writeInt(tankIdShotted);
			dos.writeInt(tankLifeShotted);
			dos.writeInt(tankID);
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
			int tankIdShotted = dis.readInt();
			
			if(tc.getMyTank().getId() == tankIdShotted)
			{
				return ;
			}
			
			int tankLifeShotted = dis.readInt();
			int tankID = dis.readInt();
			int id = dis.readInt();
			
			for(int i=0; i<tc.getMissiles().size(); i++)
			{
				Missile m  = tc.getMissiles().get(i);
				if(m.getMissileID() == id && m.getTankID() == tankID)
				{
					m.setLive(false);
					tc.getExlopdes().add(new Exlopde(m.getX(), m.getY(), tc));
					
					break;
				}
			}
			
			for(int i=0; i<tc.getEnemyTanks().size(); i++)
			{
				Tank t = tc.getEnemyTanks().get(i);
				if(tankIdShotted == t.getId())
				{
					t.setLife(tankLifeShotted);
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