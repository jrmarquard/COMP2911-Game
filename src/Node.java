import java.util.ArrayList;


public class Node {
	
	private int x;
	private int y;
	private Node up;
	private Node right;
	private Node down;
	private Node left;
	
	/**
	 * 0 = Completely lit
	 * ...
	 * 100 = Completely dark
	 */
	private float visibility;
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
		this.up = null;
		this.right = null;
		this.down = null;
		this.left = null;
		this.visibility = 100f;		
	}
	
	public float getVisibility() {
        return visibility;
    }

    public void setVisibility(float visibility) {
        if (visibility > 100f) {
            this.visibility = 100f;
        } else {
            this.visibility = visibility;
        }
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
	
	public ArrayList<Node> getConnectedNodes() {
	    ArrayList<Node> nodes = new ArrayList<Node>();
	    if (this.up != null) nodes.add(this.up);
	    if (this.down != null) nodes.add(this.down);
	    if (this.left != null) nodes.add(this.left);
	    if (this.right != null) nodes.add(this.right);
	    return nodes;
	}
	
	public boolean isDeadEnd() {
	    boolean deadEnd = false;
	    int count = 0;
	    
	    if (this.up == null) count++;
	    if (this.down == null) count++;
	    if (this.left == null) count++;
	    if (this.right == null) count++;
	    if (count == 3) deadEnd = true;
	    
	    return deadEnd;
	}
	
	public boolean isConnected(Node n) {
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
