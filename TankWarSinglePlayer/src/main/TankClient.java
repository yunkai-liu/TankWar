package main;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import constants.Constant;
import enums.Direction;
import model.Exlopde;
import model.Missile;
import model.Tank;
import model.Wall;

public class TankClient extends Frame
{
	private static final long serialVersionUID = 1L;

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
			m.hitTank(enemyTanks);
			m.hitTank(myTank);
			
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
		
		myTank.collidesWithWall(hw);
		myTank.collidesWithWall(vw);
		myTank.collidesWithTanks(enemyTanks);
		myTank.draw(g);
		
		hw.draw(g);
		vw.draw(g);
	}
	
	public void lauchFrame()
	{	
		for(int i=0; i<Constant.INIT_ENEMY_TANK_NUMBER; i++)
		{
			enemyTanks.add(new Tank(Constant.INIT_ENEMY_TANK_LOCATION + i * Constant.INIT_ENEMY_TANK_SAPCE, Constant.INIT_ENEMY_TANK_LOCATION, false, Direction.DOWN, this));
		}
		
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
			myTank.keyPressed(e);
		}
		
		public void keyReleased(KeyEvent e)
		{
			myTank.keyReleased(e);
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
}