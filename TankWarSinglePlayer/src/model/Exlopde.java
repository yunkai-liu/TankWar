package model;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import main.TankClient;

public class Exlopde 
{
	private int x;
	private int y;
	private boolean live;
	private int step;
	
	private static boolean init = false;
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image[] images = {
		tk.getImage(Exlopde.class.getClassLoader().getResource("images/0.gif")),
		tk.getImage(Exlopde.class.getClassLoader().getResource("images/1.gif")),
		tk.getImage(Exlopde.class.getClassLoader().getResource("images/2.gif")),
		tk.getImage(Exlopde.class.getClassLoader().getResource("images/3.gif")),
		tk.getImage(Exlopde.class.getClassLoader().getResource("images/4.gif")),
		tk.getImage(Exlopde.class.getClassLoader().getResource("images/5.gif")),
		tk.getImage(Exlopde.class.getClassLoader().getResource("images/6.gif")),
		tk.getImage(Exlopde.class.getClassLoader().getResource("images/7.gif")),
		tk.getImage(Exlopde.class.getClassLoader().getResource("images/8.gif")),
		tk.getImage(Exlopde.class.getClassLoader().getResource("images/9.gif")),
		tk.getImage(Exlopde.class.getClassLoader().getResource("images/10.gif"))
	};
	
	private TankClient tc;
	
	public Exlopde(int x, int y)
	{
		this.x = x;
		this.y = y;
		this.live = true;
		this.step = 0;
	}
	
	public Exlopde(int x, int y, TankClient tc)
	{
		this(x, y);
		this.tc = tc;
	}
	
	public void draw(Graphics g) 
	{
		if(!init)
		{
			for(int i=0; i<images.length; i++)
			{
				g.drawImage(images[i], Integer.MIN_VALUE, Integer.MIN_VALUE, null);
			}
			init = true;
		}
		
		if(!live)
		{
			tc.getExlopdes().remove(this);
			return ;
		}
		
		if(step == images.length)
		{
			live = false;
			step = 0;
			return ;
		}
		
		g.drawImage(images[step], x, y, null);
		step++;
	}
}