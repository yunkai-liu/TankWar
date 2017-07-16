package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import constants.Constant;

public class TankServer 
{
	private int INIT_ID = 100;
	private List<Client> clients = new ArrayList<Client>();
	
	public void start()
	{
		new Thread(new UDPThread()).start();
		
		ServerSocket ss = null;
		try 
		{
			ss = new ServerSocket(Constant.TCP_PORT);
			
			while(true)
			{
				Socket s = ss.accept();
				DataInputStream dis = new DataInputStream(s.getInputStream());
				String ip = s.getInetAddress().getHostAddress();
				int udp_port = dis.readInt();
				Client c = new Client(ip, udp_port);
				clients.add(c);
				
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				dos.writeInt(INIT_ID++);
				
				if(dos != null)
				{
					dos.close();
					dos = null;
				}
				
				if(dis != null)
				{
					dis.close();
					dis = null;
				}
				
				if(s != null)
				{
					s.close();
					s = null;
				}
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				ss.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) 
	{
		new TankServer().start();
	}
	
	private class Client
	{
		private String ip;
		private int udp_port;
		
		public Client(String ip, int udp_port)
		{
			this.ip = ip;
			this.udp_port = udp_port;
		}
		
		public String getIp() 
		{
			return ip;
		}

		public int getUdp_port() 
		{
			return udp_port;
		}
	}
	
	private class UDPThread implements Runnable
	{
		byte[] buffer = new byte[1024];
		
		@Override
		public void run() 
		{
			DatagramSocket ds = null;
			
			try 
			{
				ds = new DatagramSocket(Constant.UDP_PORT);
			}
			catch (SocketException e) 
			{
				e.printStackTrace();
			}
			
			while(ds != null)
			{
				DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
				try
				{
					ds.receive(dp);
					for(int i=0; i<clients.size(); i++)
					{
						Client c = clients.get(i);
						dp.setSocketAddress(new InetSocketAddress(c.getIp(), c.getUdp_port()));
						ds.send(dp);
					}
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
}