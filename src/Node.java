
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
	
	public void print() {
		System.out.println("Node: " + this.x + " " + this.y);
	}
	
	public boolean isAdjacent(Node n) {
	    if (this.up == n || this.right == n || this.down == n || this.left == n) {
	        return true;
	    } else {
	        return false;
	    }
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof Node)) {
			return false;
		}
		
		if(o == this) {
			return true;
		}
		
		Node node = (Node) o;
		if(this.x == node.getX() && this.y == node.getY()) {
			return true;
		} else {
			return false;
		}
	}
}
