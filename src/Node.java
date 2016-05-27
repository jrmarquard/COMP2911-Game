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
	
	/**
	 * Constructor which creates a node with input coordinates
	 * 
	 * @param x Horizontal coordinate
	 * @param y Vertical coordinate
	 */
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
		this.up = null;
		this.right = null;
		this.down = null;
		this.left = null;
		this.visibility = 100f;		
	}
	
	/**
	 * Gets this nodes visibility
	 * 
	 * @return Visibility of this node
	 */
	public float getVisibility() {
        return visibility;
    }

	/**
	 * Sets this nodes visibility to input value
	 * 
	 * @param visibility Visibility to be set
	 */
    public void setVisibility(float visibility) {
        if (visibility > 100f) {
            this.visibility = 100f;
        } else {
            this.visibility = visibility;
        }
    }

    /**
     * Returns the horizontal coordinate of this node
     * 
     * @return Horizontal coordinate of this node
     */
    public int getX() {
		return this.x;
	}
	
    /**
     * Returns the vertical coordinate of this node
     * 
     * @return Vertical coordinate of this node
     */
	public int getY() {
		return this.y;
	}
	
	/**
	 * Gets the node above this node
	 * 
	 * @return The node positioned above this one
	 */
	public Node getUp() {
		return this.up;
	}
	
	/**
	 * Gets the node to the right of this node
	 * 
	 * @return The node positioned to the right of this one
	 */
	public Node getRight() {
		return this.right;
	}
	
	/**
	 * Gets the node below this node
	 * 
	 * @return The node positioned below this one
	 */
	public Node getDown() {
		return this.down;
	}
	
	/**
	 * Gets the node to the left of this one
	 * 
	 * @return The node positioned to the left of this one
	 */
	public Node getLeft() {
		return this.left;
	}
	
	/**
	 * Sets the node above to be connected to this one
	 * 
	 * @param up The node to be connected
	 */
	public void setUp(Node up) {
		this.up = up;
	}
	
	/**
	 * Sets the node to the right to be connected to this one
	 * 
	 * @param right The node to be connected
	 */
	public void setRight(Node right) {
		this.right = right;
	}

	/**
	 * Sets the node below to be connected to this one
	 * 
	 * @param down The node to be connected
	 */
	public void setDown(Node down) {
		this.down = down;
	}

	/**
	 * Sets the node to the left to be connected to this one
	 * 
	 * @param left The node to be connected
	 */
	public void setLeft(Node left) {
		this.left = left;
	}
	
	/**
	 * Gets an arrayList of the nodes which are connected to this one
	 * 
	 * @return The nodes connected to this one
	 */
	public ArrayList<Node> getConnectedNodes() {
	    ArrayList<Node> nodes = new ArrayList<Node>();
	    if (this.up != null) nodes.add(this.up);
	    if (this.down != null) nodes.add(this.down);
	    if (this.left != null) nodes.add(this.left);
	    if (this.right != null) nodes.add(this.right);
	    return nodes;
	}
	
	/**
	 * Finds whether this node is a dead end (ie is walled in on 3 sides)
	 * 
	 * @return Whether this node is a dead end
	 */
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
	
	/**
	 * Finds whether this node is connected to the input node
	 * 
	 * @param n The node to be checked for adjacency
	 * @return Whether this node is connected to the input node
	 */
	public boolean isConnected(Node n) {
	    if (this.up == n || this.right == n || this.down == n || this.left == n) {
	        return true;
	    } else {
	        return false;
	    }
	}
	
	/**
	 * Finds whether this node is equal to an input object
	 */
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
