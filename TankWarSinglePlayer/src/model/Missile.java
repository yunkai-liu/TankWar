package model;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import constants.Constant;
import enums.Direction;
import main.TankClient;

public class Missile 
{
	private int x;
	private int y;
	private boolean good;
	private boolean live;
	
	private Direction direction;

	private TankClient tc;
	
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Map<Direction, Image> imagesMap = new HashMap<Direction, Image>();
	private static Image[] images = null;
	static		
	{
		images = new Image[] {
			tk.getImage(Missile.class.getClassLoader().getResource("images/missileD.gif")),
			tk.getImage(Missile.class.getClassLoader().getResource("images/missileL.gif")),
			tk.getImage(Missile.class.getClassLoader().getResource("images/missileLD.gif")),
			tk.getImage(Missile.class.getClassLoader().getResource("images/missileLU.gif")),
			tk.getImage(Missile.class.getClassLoader().getResource("images/missileR.gif")),
			tk.getImage(Missile.class.getClassLoader().getResource("images/missileRD.gif")),
			tk.getImage(Missile.class.getClassLoader().getResource("images/missileRU.gif")),
			tk.getImage(Missile.class.getClassLoader().getResource("images/missileU.gif"))
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
	
	public Missile(int x, int y, Direction direction) 
	{
		this.x = x;
		this.y = y;
		this.live = true;
		this.direction = direction;
	}
	
	public Missile(int x, int y, boolean good, Direction direction, TankClient tc) 
	{
		this(x, y, direction);
		this.good = good;
		this.tc = tc;
	}
	
	public void draw(Graphics g) 
	{
		if(!live)
		{
			tc.getMissiles().remove(this);
			return ;
		}
		
		switch(direction)
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
	
	public boolean hitWall(Wall w)
	{
		if(live && getRect().intersects(w.getRect()))
		{
			live = false;
			
			return true;
		}
		
		return false;
	}
	
	public boolean hitTank(Tank t)
	{
		if(live && getRect().intersects(t.getRect()) && t.isLive() && good != t.isGood())
		{	
			if(t.isGood())
			{
				t.setLife(t.getLife() - Constant.HARM);
				if(t.getLife() <= 0)
				{
					t.setLive(false);
				}
			}
			else
			{
				t.setLive(false);
			}
			
			live = false;
			
			Exlopde  e = new Exlopde(x, y, tc);
			tc.getExlopdes().add(e);
			
			return true;
		}
		
		return false;
	}
	
	public boolean hitTank(List<Tank> tanks)
	{
		for(int i=0; i<tanks.size(); i++)
		{
			if(hitTank(tanks.get(i)))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public Rectangle getRect()
	{
		return new Rectangle(x, y, Constant.MISSILE_WIDTH, Constant.MISSILE_HEIGHT);
	}
	
	public void move()
	{
		switch(direction)
		{
			case LEFT :
				x -= Constant.MISSILE_SPEED;
				break;
			case RIGHT :
				x += Constant.MISSILE_SPEED;
				break;
			case UP :
				y -= Constant.MISSILE_SPEED;
				break;
			case DOWN :
				y += Constant.MISSILE_SPEED;
				break;
			case RIGHT_UP :
				x += Constant.MISSILE_SPEED;
				y -= Constant.MISSILE_SPEED;
				break;
			case LEFT_UP :
				x -= Constant.MISSILE_SPEED;
				y -= Constant.MISSILE_SPEED;
				break;
			case RIGHT_DOWN :
				x += Constant.MISSILE_SPEED;
				y += Constant.MISSILE_SPEED;
				break;
			case LEFT_DOWN :
				x -= Constant.MISSILE_SPEED;
				y += Constant.MISSILE_SPEED;
				break;
			case STOP :
				break;
		}
		
		if(x < 0 || y < 0 || x > Constant.GAME_WIDTH || y > Constant.GAME_HEIGHT)
		{
			live = false;
		}
	}
	
	public boolean isLive() 
	{
		return live;
	}

	public void setLive(boolean live)
	{
		this.live = live;
	}
	
	public int getX()
	{
		return x;
	}

	public void setX(int x) 
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public boolean isGood() 
	{
		return good;
	}

	public void setGood(boolean good)
	{
		this.good = good;
	}
	
	public Direction getDirection() 
	{
		return direction;
	}

	public void setDirection(Direction direction)
	{
		this.direction = direction;
	}
}