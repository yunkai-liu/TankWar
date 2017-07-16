package model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import constants.Constant;
import enums.Direction;
import main.TankClient;
import message.Message;
import message.MissileNewMessage;
import message.TankMoveMessage;

public class Tank 
{
	private int id;

	private int TANK_WIDTH;
	private int x;
	private int y;
	private int oldX;
	private int oldY;
	private boolean live;
	
	private boolean bl = false;
	private boolean br = false;
	private boolean bu = false;
	private boolean bd = false;
	
	private Direction direction;
	private Direction shotDirection;

	private TankClient tc;
	
	private boolean good;

	private int life;
	private BloodBar bb;
	
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Map<Direction, Image> imagesMap = new HashMap<Direction, Image>();
	private static Image[] images = null;
	static		
	{
		images = new Image[] {
			tk.getImage(Tank.class.getClassLoader().getResource("images/tankD.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/tankL.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/tankLD.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/tankLU.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/tankR.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/tankRD.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/tankRU.gif")),
			tk.getImage(Tank.class.getClassLoader().getResource("images/tankU.gif"))
		};
		
		imagesMap.put(Direction.DOWN, images[0]);
		imagesMap.put(Direction.LEFT, images[1]);
		imagesMap.put(Direction.LEFT_DOWN, images[2]);
		imagesMap.put(Direction.LEFT_UP, images[3]);
		imagesMap.put(Direction.RIGHT, images[4]);
		imagesMap.put(Direction.RIGHT_DOWN, images[5]);
		imagesMap.put(Direction.RIGHT_UP, images[6]);
		imagesMap.put(Direction.UP, images[7]);
	};
	
	public Tank(int x, int y, boolean good)
	{
		this.TANK_WIDTH = Constant.TANK_WIDTH_NORMAL;
		this.x = x;
		this.y = y;
		this.oldX = x;
		this.oldY = y;
		this.live = true;
		this.good = good;
		this.life = Constant.INIT_MY_TANK_LIFE;
		this.bb = new BloodBar();
		this.direction = Direction.STOP;
		this.shotDirection = Direction.UP;
	}
	
	public Tank(int x, int y, boolean good, Direction direction, TankClient tc)
	{
		this(x, y, good);
		this.direction = direction;
		this.tc = tc;
	}
	
	public void move()
	{
		oldX = x;
		oldY = y;
		
		switch(direction)
		{
			case LEFT :
				x -= Constant.TANK_SPEED;
				break;
			case RIGHT :
				x += Constant.TANK_SPEED;
				break;
			case UP :
				y -= Constant.TANK_SPEED;
				break;
			case DOWN :
				y += Constant.TANK_SPEED;
				break;
			case RIGHT_UP :
				x += Constant.TANK_SPEED;
				y -= Constant.TANK_SPEED;
				break;
			case LEFT_UP :
				x -= Constant.TANK_SPEED;
				y -= Constant.TANK_SPEED;
				break;
			case RIGHT_DOWN :
				x += Constant.TANK_SPEED;
				y += Constant.TANK_SPEED;
				break;
			case LEFT_DOWN :
				x -= Constant.TANK_SPEED;
				y += Constant.TANK_SPEED;
				break;
			case STOP :
				break;
		}
		
		if(direction != Direction.STOP)
		{
			shotDirection = direction;
		}
		
		if(x < 0)
		{
			x = 0;
		}
		if(y < Constant.SIZE_TITLE)
		{
			y = Constant.SIZE_TITLE;
		}
		if(x + TANK_WIDTH > Constant.GAME_WIDTH)
		{
			x = Constant.GAME_WIDTH - TANK_WIDTH;
		}
		if(y + Constant.TANK_HEIGHT > Constant.GAME_HEIGHT)
		{
			y = Constant.GAME_HEIGHT - Constant.TANK_HEIGHT;
		}
	}
	
	public void fire()
	{
		if(!live)
		{
			return ;
		}
		
		int mx = x + TANK_WIDTH / 2 - Constant.MISSILE_WIDTH / 2;
		int my = y + Constant.TANK_HEIGHT / 2 - Constant.MISSILE_HEIGHT / 2;
		
		Missile m = new Missile(id, mx, my, good, shotDirection, tc);
		tc.getMissiles().add(m);
		
		Message message = new MissileNewMessage(m);
		tc.getNc().send(message);
	}
	
	public void fire(Direction d)
	{
		if(!live)
		{
			return ;
		}
		
		int mx = x + TANK_WIDTH / 2 - Constant.MISSILE_WIDTH / 2;
		int my = y + Constant.TANK_HEIGHT / 2 - Constant.MISSILE_HEIGHT / 2;
		
		Missile m = new Missile(id, mx, my, good, d, tc);
		tc.getMissiles().add(m);
		
		Message message = new MissileNewMessage(m);
		tc.getNc().send(message);
	}
	
	public void superFire()
	{
		Direction[] dirs = Direction.values();
		
		for(int i=0; i<dirs.length-1; i++)
		{
			fire(dirs[i]);
		}
	}
	
	public boolean collidesWithWall(Wall w)
	{	
		if(live && getRect().intersects(w.getRect()))
		{
			stay();
			direction = Direction.STOP;
			
			return true;
		}
		
		return false;
	}
	
	public boolean collidesWithTanks(List<Tank> tanks)
	{
		for(int i=0; i<tanks.size(); i++)
		{
			Tank t = tanks.get(i);
			
			if(this != t && live && t.isLive() && getRect().intersects(t.getRect()))
			{
				stay();
				t.stay();
				direction = Direction.STOP;
				
				return true;
			}
		}
		
		return false;
	}
	
	public void stay()
	{
		x = oldX;
		y = oldY;
	}
	
	public void draw(Graphics g) 
	{
		if(!live)
		{
			if(id != tc.getMyTank().getId())
			{
				tc.getEnemyTanks().remove(this);
			}
			return ;
		}
		
		bb.draw(g);
		
		switch(shotDirection)
		{
			case LEFT :
				g.drawImage(imagesMap.get(Direction.LEFT), x, y, null);
				break;
			case RIGHT :
				g.drawImage(imagesMap.get(Direction.RIGHT), x, y, null);				
				break;
			case UP :
				g.drawImage(imagesMap.get(Direction.UP), x, y, null);
				break;
			case DOWN :
				g.drawImage(imagesMap.get(Direction.DOWN), x, y, null);
				break;
			case RIGHT_UP :
				g.drawImage(imagesMap.get(Direction.RIGHT_UP), x, y, null);
				break;
			case LEFT_UP :
				g.drawImage(imagesMap.get(Direction.LEFT_UP), x, y, null);
				break;
			case RIGHT_DOWN :
				g.drawImage(imagesMap.get(Direction.RIGHT_DOWN), x, y, null);
				break;
			case LEFT_DOWN :
				g.drawImage(imagesMap.get(Direction.LEFT_DOWN), x, y, null);
				break;
			case STOP :
				break;
		}
		
		move();
	}
	
	public void keyPressed(KeyEvent e) 
	{
		int key = e.getKeyChar();
		
		switch(key)
		{
			case Constant.KEY_LEFT :
				bl = true;
				break;
			case Constant.KEY_RIGHT :
				br = true;
				break;
			case Constant.KEY_UP :
				bu = true;
				break;
			case Constant.KEY_DOWN :
				bd = true;
				break;
			case Constant.KEY_FIRE :
				fire();
				break;
			case Constant.KEY_SUPER_FIRE :
				superFire();
				break;
		}
		
		locateDirection();
	}
	
	public void keyReleased(KeyEvent e)
	{
		int key = e.getKeyChar();
		
		switch(key)
		{
			case Constant.KEY_LEFT :
				bl = false;
				break;
			case Constant.KEY_RIGHT :
				br = false;
				break;
			case Constant.KEY_UP :
				bu = false;
				break;
			case Constant.KEY_DOWN :
				bd = false;
				break;
		}
		
		locateDirection();
	}
	
	private void locateDirection()
	{
		Direction old = direction;
		
		if(bl && !br && !bu && !bd)
		{
			direction = Direction.LEFT;
			TANK_WIDTH = Constant.TANK_WIDTH_NORMAL;
		}
		else if(!bl && br && !bu && !bd)
		{
			direction = Direction.RIGHT;
			TANK_WIDTH = Constant.TANK_WIDTH_NORMAL;
		}	
		else if(!bl && !br && bu && !bd)
		{
			direction = Direction.UP;
			TANK_WIDTH = Constant.TANK_WIDTH_NORMAL;
		}
		else if(!bl && !br && !bu && bd)
		{
			direction = Direction.DOWN;
			TANK_WIDTH = Constant.TANK_WIDTH_NORMAL;
		}
		else if(bl && !br && bu && !bd)
		{
			direction = Direction.LEFT_UP;
			TANK_WIDTH = Constant.TANK_WIDTH_SPECIAL;
		}
		else if(bl && !br && !bu && bd)
		{
			direction = Direction.LEFT_DOWN;
			TANK_WIDTH = Constant.TANK_WIDTH_SPECIAL;
		}
		else if(!bl && br && bu && !bd)
		{
			direction = Direction.RIGHT_UP;
			TANK_WIDTH = Constant.TANK_WIDTH_SPECIAL;
		}
		else if(!bl && br && !bu && bd)
		{
			direction = Direction.RIGHT_DOWN;
			TANK_WIDTH = Constant.TANK_WIDTH_SPECIAL;
		}
		else if(!bl && !br && !bu && !bd)
		{
			direction = Direction.STOP;
		}
		
		if(direction != old)
		{
			Message message = new TankMoveMessage(id, direction, x, y, shotDirection);
			tc.getNc().send(message);
		}
	}
	
	public Rectangle getRect()
	{
		return new Rectangle(x, y, TANK_WIDTH, Constant.TANK_HEIGHT);
	}
	
	public boolean isLive() 
	{
		return live;
	}

	public void setLive(boolean live)
	{
		this.live = live;
	} 
	
	public void setGood(boolean good)
	{
		this.good = good;
	}
	
	public boolean isGood() 
	{
		return good;
	}
	
	public void setLife(int life)
	{
		this.life = life;
	}
	
	public int getLife()
	{
		return life;
	}
	
	public int getId() 
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}
	
	public void setX(int x) 
	{
		this.x = x;
	}
	
	public int getX() 
	{
		return x;
	}
	
	public void setY(int y) 
	{
		this.y = y;
	}

	public int getY() 
	{
		return y;
	}
	
	public void setDirection(Direction direction) 
	{
		this.direction = direction;
	}
	
	public Direction getDirection()
	{
		return direction;
	}
	
	public Direction getShotDirection() 
	{
		return shotDirection;
	}

	public void setShotDirection(Direction shotDirection) 
	{
		this.shotDirection = shotDirection;
	}
	
	private class BloodBar
	{
		public void draw(Graphics g)
		{	
			Color c = g.getColor();
			
			if(good)
			{
				g.setColor(Color.RED);
			}
			else
			{
				g.setColor(Color.BLUE);
			}
			
			g.drawString(id+"", x, y-Constant.BLOOD_TANK_DISTANCE);
			g.drawRect(x, y-Constant.BLOOD_TANK_DISTANCE, Constant.BLOOD_WIDTH, Constant.BLOOD_HEIGHT);
			int w = Constant.BLOOD_WIDTH * life / Constant.HUNDRED;
			g.fillRect(x, y-Constant.BLOOD_TANK_DISTANCE, w, Constant.BLOOD_HEIGHT);
			
			g.setColor(c);
		}
	}
}