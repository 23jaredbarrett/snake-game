import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

public class GamePanel extends JPanel implements Runnable, KeyListener{
	public static final int WIDTH = 400;
	public static final int HEIGHT = 400;
	private static final int FPS = 10;
	// Render
	private Graphics2D g2d;
	private BufferedImage image;
	
	// Game loop variables
	private Thread thread;
	private boolean running;
	private long targetTime;
	// Game Variables
	private Entity head, apple;
	private final int SIZE_OF_ENTITY = 10;
	private ArrayList<Entity> snake;
	private int sizeOfSnake;
	private boolean gameOver;
	// Movement Variables
	private int dx, dy;
	// input
	private boolean up, down, right, left, start;
	
	// initializes GamePanel object
	public GamePanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		requestFocus();
		addKeyListener(this);
	}
	
	
	@Override
	public void addNotify() {
		super.addNotify();
		thread = new Thread(this);
		thread.start();
	}
	// defaults fps to 10
	private void setFPS() {
		targetTime = 1000 / FPS;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
		int k =  e.getKeyCode();
		if(k == KeyEvent.VK_UP) up = true;
		if(k == KeyEvent.VK_DOWN) down = true;
		if(k == KeyEvent.VK_LEFT) left = true;
		if(k == KeyEvent.VK_RIGHT) right = true;
		if(k == KeyEvent.VK_ENTER) start = true;
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int k =  e.getKeyCode();
		
		if(k == KeyEvent.VK_UP) up = false;
		if(k == KeyEvent.VK_DOWN) down = false;
		if(k == KeyEvent.VK_LEFT) left = false;
		if(k == KeyEvent.VK_RIGHT) right = false;
		if(k == KeyEvent.VK_ENTER) start = false;
		

		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
		
	}

	// loop to run the program for as long as needed
	@Override
	public void run() {
		if(running) return;
		init();
		long startTime, elapsed, wait;
		while(running) {
			startTime = System.nanoTime();
			update();
			requestRender();
			
			elapsed = System.nanoTime()- startTime;
			// the reason we divide by 1 million is because we want to convert to miliseconds
			wait = targetTime - elapsed / 1000000;
			if(wait >  0) {
				try {
					Thread.sleep(wait);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
		} // end while loop (program ends)
	}
	// initial setter
	private void init() {
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		g2d = image.createGraphics();
		running = true;
		setupLevel();
		 setFPS();
		 sizeOfSnake = 5;
	}
	private void setupLevel() {
		snake = new ArrayList<Entity>();
		head = new Entity(SIZE_OF_ENTITY);
		head.setPosition(WIDTH / 2, HEIGHT / 2);
		snake.add(head);
		// adds 5 more entitities to the snake
		for(int i = 1; i < 5; i++) {
			Entity e = new Entity(SIZE_OF_ENTITY);
			// the snake will start out as ----- horizontal, so we need to reflect this in the x-direction
			// and not the y-direction
			e.setPosition(head.getX() + (i*SIZE_OF_ENTITY), head.getY());
			snake.add(e);
		}
		
		apple = new Entity(SIZE_OF_ENTITY);
		setAppleLocation();
		
	}
	// checks whether the apple's location is valid and sets location if it is
	public void setAppleLocation() {
		// since the width is 400 pixels, there is 40 different possible positions for the apple (including 0)
		// since we have 39 rows and 39 columns, we have 40*40 possible apple locations (including 0)
		// first, we make a random row location and a random y location, then we shift through the
		// array list to see if it works
		boolean isValid = false;
		while(!isValid) {

			// formula: (int)(Math.random( )* (max-min)) + min; min is zero
			int tempX = (int)(Math.random()*39)*(SIZE_OF_ENTITY);
			int tempY = (int)(Math.random()*39)*(SIZE_OF_ENTITY);
			apple.setPosition(tempX, tempY);
			// setting isvalid to true, if we find it in the array, we set to false again
			isValid = true;
			for(Entity e: snake) {
				if(e.isCollision(apple))
					isValid = false;
			}
		} // end while loop
		
	}
	private void requestRender() {
		render(g2d);
		Graphics g = getGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		
		
	}
	// updates the position of every snake entity
	private void update() {
		// the reason for the and is because if the dy is not equal to zero, we don't want the snake to go left when it is going right
		if(up && dy == 0) {
			// up is negative because the x and y direction is in the upper left corner
			dy = -SIZE_OF_ENTITY;
			dx = 0;
		}
		if(down && dy == 0) {
			dy = SIZE_OF_ENTITY;
			dx = 0;
		}
		if(left && dx == 0) {
			dy = 0;
			dx = -SIZE_OF_ENTITY;
		}
		if(right && dx == 0) {
			dy = 0;
			dx = SIZE_OF_ENTITY;
		}
		
			
		
		
		// every snake position is moved down one, only the head is moved by dx and dy
		if(dx != 0 || dy != 0) {
			for(int i = snake.size() - 1; i > 0; i-- ) {
				snake.get(i).setPosition(snake.get(i-1).getX(), snake.get(i-1).getY());
			
			}
		}
		head.move(dx, dy);
		// checks if the head collides with the apple, if it does, we add an entity at the end
		if(apple.isCollision(head)) {
			sizeOfSnake++;
			setAppleLocation();
			
			Entity e = new Entity(SIZE_OF_ENTITY);
			// I literally cannot figure out why -100, -100 works
			e.setPosition(-100, -100);
			snake.add(e);
		}
		
		// if the head moves out of the page, we move the head to the opposite x direction or y direction
		if(head.getX() < 0) head.setX(WIDTH);
		if(head.getY() < 0) head.setY(HEIGHT);
		if(head.getX() > WIDTH) head.setX(0);
		if(head.getY() > HEIGHT) head.setY(0); 
	}
	
	public void render(Graphics2D g2d) {
		g2d.clearRect(0, 0, WIDTH, HEIGHT); 
		g2d.setColor(Color.GREEN);
		for(Entity e: snake) {
			// calls the render method in entity class
			e.render(g2d);
		}
		g2d.setColor(Color.RED);
		apple.render(g2d);
		
		g2d.drawString("Size : " + sizeOfSnake, 10, 10);
	}

	

}
