package main;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

import constants.Constant;
import message.Message;
import message.MissileDeadMessage;
import message.MissileNewMessage;
import message.TankDeadMessage;
import message.TankMoveMessage;
import message.TankNewMessage;

public class NetClient 
{
	private int udp_port;
	
	private String serverIP;

	private TankClient tc;
	
	private DatagramSocket ds = null;
	
	public NetClient(TankClient tc)
	{
		this.tc = tc;
	}
	
	public void connect(String ip, int port)
	{
		serverIP = ip;
		
		try 
		{
			ds = new DatagramSocket(udp_port);
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
		
		Socket s = null;
		DataOutputStream dos = null;
		DataInputStream dis = null;
		
		try 
		{
			s = new Socket(ip, port);
			dos = new DataOutputStream(s.getOutputStream());
			dos.writeInt(udp_port);
			
			dis = new DataInputStream(s.getInputStream());
			int tankID = dis.readInt();
			tc.getMyTank().setId(tankID);
			
			if(tankID % 2 == 0)
			{
				tc.getMyTank().setGood(false);
			}
			else
			{
				tc.getMyTank().setGood(true);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(dis != null)
			{
				try 
				{
					dis.close();
					dis = null;
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
			if(dos != null)
			{
				try 
				{
					dos.close();
					dos = null;
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
			if(s != null)
			{
				try 
				{
					s.close();
					s = null;
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		Message message = new TankNewMessage(tc.getMyTank());
		send(message);
		
		new Thread(new UDPThread()).start();
	}
	
	public void send(Message tnm)
	{
		tnm.send(ds, serverIP, Constant.UDP_PORT);
	}
	
	private class UDPThread implements Runnable
	{
		byte[] buffer = new byte[1024];
		
		@Override
		public void run() 
		{
			while(ds != null)
			{
				DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
				try
				{
					ds.receive(dp);
					parse(dp);
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
		private void parse(DatagramPacket dp)
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(buffer, 0, buffer.length);
			DataInputStream dis = new DataInputStream(bais);
			
			Message message = null;
			int message_type = 0;
			try
			{
				message_type = dis.readInt();
				
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			switch(message_type)
			{
				case Message.TANK_NEW_MESSAGE :
					message  = new TankNewMessage(tc);
					break;
				case Message.TANK_MOVE_MESSAGE :
					message = new TankMoveMessage(tc);
					break;
				case Message.MISSILE_NEW_MESSAGE :
					message = new MissileNewMessage(tc);
					break;
				case Message.TANK_DEAD_MESSAGE :
					message = new TankDeadMessage(tc);
					break;
				case Message.MISSILE_DEAD_MESSAGE :
					message = new MissileDeadMessage(tc);
					break;
			}
			
			message.parse(dis);
		}
	}
	
	public void setUdp_port(int udp_port) 
	{
		this.udp_port = udp_port;
	}
}