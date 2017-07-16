package main;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import constants.Constant;
import enums.Direction;
import message.Message;
import message.MissileDeadMessage;
import message.TankDeadMessage;
import model.Exlopde;
import model.Missile;
import model.Tank;
import model.Wall;

public class TankClient extends Frame
{
	private static final long serialVersionUID = 1L;
	
	private NetClient nc = new NetClient(this);
	private ConnDialog dialog = new ConnDialog();

	private Image offScreenImage = null;
	
	private Tank myTank = new Tank(Constant.INIT_MY_TANK_LOCATION_X, Constant.INIT_MY_TANK_LOCATION_Y, true, Direction.STOP, this);
	private List<Tank> enemyTanks = new ArrayList<Tank>();
	private List<Missile> missiles = new ArrayList<Missile>();
	private List<Exlopde> exlopdes = new ArrayList<Exlopde>();
	
	private Wall vw = new Wall(Constant.INIT_VERTICAL_WALL_LOCATION_X, Constant.INIT_VERTICAL_WALL_LOCATION_Y, Constant.INIT_VERTICAL_WALL_WIDTH, Constant.INIT_VERTICAL_WALL_HEIGHT);
	private Wall hw = new Wall(Constant.INIT_HORIZONTAL_WALL_LOCATION_X, Constant.INIT_HORIZONTAL_WALL_LOCATION_Y, Constant.INIT_HORIZONTAL_WALL_WIDTH, Constant.INIT_HORIZONTAL_WALL_HEIGHT);

	public static void main(String[] args) 
	{
		TankClient tc = new TankClient();
		tc.lauchFrame();
	}
	
	public void update(Graphics g) 
	{
		if(offScreenImage == null)
		{
			offScreenImage = this.createImage(Constant.GAME_WIDTH, Constant.GAME_HEIGHT);
		}
		
		Graphics gOffScreen = offScreenImage.getGraphics();
		
		Color c = g.getColor();
		gOffScreen.setColor(Color.BLACK);
		gOffScreen.fillRect(0, 0, Constant.GAME_WIDTH, Constant.GAME_HEIGHT);
		gOffScreen.setColor(c);
		
		paint(gOffScreen);
		g.drawImage(offScreenImage, 0, 0, null);
	}
	
	public void paint(Graphics g) 
	{
		for(int i=0; i<missiles.size(); i++)
		{
			Missile m = missiles.get(i);
			if(m.hitTank(myTank))
			{
				if(!myTank.isLive())
				{
					Message mess = new TankDeadMessage(myTank.getId());
					nc.send(mess);
				}
				
				Message messMessile = new MissileDeadMessage(m.getMissileID(), m.getTankID(), myTank.getId(), myTank.getLife());
				nc.send(messMessile);
			}
			
			m.hitWall(hw);
			m.hitWall(vw);
			m.draw(g);
		}
		
		for(int i=0; i<exlopdes.size(); i++)
		{
			Exlopde e = exlopdes.get(i);
			e.draw(g);
		}
		
		for(int i=0; i<enemyTanks.size(); i++)
		{
			Tank t = enemyTanks.get(i);
			t.collidesWithWall(hw);
			t.collidesWithWall(vw);
			t.collidesWithTanks(enemyTanks);
			t.draw(g);
		}
		
		myTank.draw(g);
		myTank.collidesWithWall(hw);
		myTank.collidesWithWall(vw);
		myTank.collidesWithTanks(enemyTanks);
		
		hw.draw(g);
		vw.draw(g);
	}
	
	public void lauchFrame()
	{	
		setLocation(Constant.INIT_LOCATION_X, Constant.INIT_LOCATION_Y);
		setSize(Constant.GAME_WIDTH, Constant.GAME_HEIGHT);
		setTitle("TankWar");
		setVisible(true);
		setResizable(false);
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(Constant.EXIT_CODE);
			}
		});
		
		setBackground(Color.GREEN);
		
		addKeyListener(new KeyMonitor());
		new Thread(new PaintThread()).start();
	}
	
	private class PaintThread implements Runnable
	{
		@Override
		public void run()
		{
			while(true)
			{
				repaint();
				
				try 
				{
					Thread.sleep(Constant.SLEEP_TIME);
				}
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	private class KeyMonitor extends KeyAdapter
	{
		public void keyPressed(KeyEvent e) 
		{
			int key = e.getKeyChar();
			if(key == Constant.KEY_CONNECTION_GAME)
			{
				dialog.setVisible(true);
			}
			else
			{
				myTank.keyPressed(e);
			}
		}
		
		public void keyReleased(KeyEvent e)
		{
			myTank.keyReleased(e);
		}
	}
	
	private class ConnDialog extends Dialog
	{
		private static final long serialVersionUID = 1L;

		Button b = new Button("确定");
		TextField tfIP = new TextField(Constant.IP, 12);
		TextField tfPort = new TextField(Constant.TCP_PORT + "", 4);
		TextField tfMyUDPPort = new TextField(4);
		
		public ConnDialog()
		{
			super(TankClient.this, true);
			
			setLayout(new FlowLayout());
			add(new Label("IP: "));
			add(tfIP);
			add(new Label("Port: "));
			add(tfPort);
			add(new Label("My UDP Port: "));
			add(tfMyUDPPort);
			add(b);
			setLocation(300, 300);
			
			pack();
			addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					dialog.setVisible(false);
				}
			});
			
			b.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e) 
				{
					String ip = tfIP.getText().trim();
					int port = Integer.parseInt(tfPort.getText().trim());
					int myPort = Integer.parseInt(tfMyUDPPort.getText().trim());
					
					nc.setUdp_port(myPort);
					nc.connect(ip, port);
					
					setVisible(false);
				}
			});
		}
	}
	
	public List<Missile> getMissiles() 
	{
		return missiles;
	}
	
	public List<Exlopde> getExlopdes() 
	{
		return exlopdes;
	}
	
	public List<Tank> getEnemyTanks() 
	{
		return enemyTanks;
	}
	
	public Tank getMyTank()
	{
		return myTank;
	}
	
	public NetClient getNc() 
	{
		return nc;
	}
}