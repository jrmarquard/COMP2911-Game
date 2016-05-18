
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
	
	public Coordinate getCoordinate() {
	    return new Coordinate(this.x, this.y);
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

	/**
	 * Checks if the node n is underneath this node
	 * 
	 * @param n query node
	 * @return returns true if n is beneath node
	 */
	public boolean isDown(Node n) {
        if (this.getDown() == null) return false;
	    return this.getDown().equals(n);
	}
    public boolean isUp(Node n) {
        if (this.getUp() == null) return false;
        return this.getUp().equals(n);
    }
    public boolean isLeft(Node n) {
        if (this.getLeft() == null) return false;
        return this.getLeft().equals(n);
    }
    public boolean isRight(Node n) {
        if (this.getRight() == null) return false;
        return this.getRight().equals(n);
    }
    
    public boolean isDown() {
        if (this.getDown() == null) {
            return false;
        } else {
            return true;
        }
    }
    public boolean isUp() {
        if (this.getUp() == null) {
            return false;
        } else {
            return true;
        }
    }
    public boolean isLeft() {
        if (this.getLeft() == null) {
            return false;
        } else {
            return true;
        }
    }
    public boolean isRight() {
        if (this.getRight() == null) {
            return false;
        } else {
            return true;
        }
    }
	
	public void print() {
		System.out.println(this.x + " " + this.y);
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
