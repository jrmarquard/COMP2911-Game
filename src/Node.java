
public class Node {
	
	int x;
	int y;
	private Node up;
	private Node right;
	private Node down;
	private Node left;
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
		this.up = null;
		this.right = null;
		this.down = null;
		this.left = null;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public Node getUp() {
		return this.up;
	}
	
	public Node getRight() {
		return this.right;
	}
	
	public Node getDown() {
		return this.down;
	}
	
	public Node getLeft() {
		return this.left;
	}
	
	public void setUp(Node up) {
		this.up = up;
	}
	
	public void setRight(Node right) {
		this.right = right;
	}

	public void setDown(Node down) {
		this.down = down;
	}

	public void setLeft(Node left) {
		this.left = left;
	}
}
