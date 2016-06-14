package nl.cubix.scrabble.config;

public class CropRectangle {
	private int x;
	private int y;
	private int width;
	private int height;
	
	public CropRectangle(int x, int y, int width, int height) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result
			.append("x: ").append(x)
			.append(", y: ").append(y)
			.append(", width: ").append(width)
			.append(", height: ").append(height);
		
		return result.toString();
		
	}
}
