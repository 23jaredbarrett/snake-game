import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Entity {
	private int x, y, size;
	
	public Entity(int size) {
		this.size = size;
	}

	public int getX() {
		return x; 
		} // end method getX()

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	public void setPosition(int x, int y) {
		setX(x);
		setY(y);
	}
	public void move(int dx, int dy) {
		x += dx;
		y += dy;
	}
	// gets the rectangle/bounds of another entity
	public Rectangle getBound() {
		return new Rectangle(x, y, size, size);
	}
	// returns whether collision happens
	public boolean isCollision(Entity o) {
		if(o == this) return false;
		// checks whether the rectangles intersect each other, returns true or false
		return getBound().intersects(o.getBound());
	}
	public void render(Graphics2D g2d) {
		// we subtract so we get the typical black lines in between each snake piece
		g2d.fillRect(x + 1, y + 1, size - 2, size - 2);
	}

}
