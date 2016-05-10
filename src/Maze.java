import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Maze {
	private ArrayList<ArrayList<Node>> nodes;
	
	private Node start;
	private Node finish;
	
	private int width;
	private int height;
	
	public Maze(int width, int height) {
		this.nodes = new ArrayList<ArrayList<Node>>();
		
		int counterW = 0;
		int counterH = 0;
		
		while (counterW < width) {
			this.nodes.add(new ArrayList<Node>());
			while (counterH < height) {
				this.nodes.get(counterW).add(new Node(counterW, counterH));
				counterH++;
			}
			counterH = 0;
			counterW++;
		}
		
		this.start = null;
		this.finish = null;
		
		this.width = width;
		this.height = height;
	}
	
	public Node getStart() {
		return this.start;
	}
	
	public Node getFinish() {
		return this.finish;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public Node getNode(int x, int y) {
		return this.nodes.get(x).get(y);
	}
	
	public void makePath(int xA, int yA, int xB, int yB) {
		if (xA == xB || yA == yB) {
			if ((xA + 1) == xB) {
				this.getNode(xA, yA).setRight(this.getNode(xB, yB));
				this.getNode(xB, yB).setLeft(this.getNode(xA, yA));
			} else if ((xB + 1) == xA) {
				this.getNode(xA, yA).setLeft(this.getNode(xB, yB));
				this.getNode(xB, yB).setRight(this.getNode(xA, yA));
			} else if ((yA + 1) == yB) {
				this.getNode(xA, yA).setDown(this.getNode(xB, yB));
				this.getNode(xB, yB).setUp(this.getNode(xA, yA));
			} else if ((yB + 1) == yA) {
				this.getNode(xA, yA).setUp(this.getNode(xB, yB));
				this.getNode(xB, yB).setDown(this.getNode(xA, yA));
			} else {
				//Illegal
			}
		} else {
			//Illegal
		}
	}
	
	public void setStart(int x, int y) {
		this.start = new Node(x, y);
		if (x == -1) {
			this.start.setRight(this.getNode(0, y));
			this.getNode(0, y).setLeft(this.start);
		} else if (y == -1) {
			this.start.setDown(this.getNode(x, 0));
			this.getNode(x, 0).setUp(this.start);
		} else if (x == this.width) {
			this.start.setLeft(this.getNode(x - 1, y));
			this.getNode(x - 1, y).setRight(this.start);
		} else if (y == this.height) {
			this.start.setUp(this.getNode(x, y - 1));
			this.getNode(x, y - 1).setDown(this.start);
		}
	}
	
	public void setFinish(int x, int y) {
		this.finish = new Node(x, y);
		if (x == -1) {
			this.finish.setRight(this.getNode(0, y));
			this.getNode(0, y).setLeft(this.finish);
		} else if (y == -1) {
			this.finish.setDown(this.getNode(x, 0));
			this.getNode(x, 0).setUp(this.finish);
		} else if (x == this.width) {
			this.finish.setLeft(this.getNode(x - 1, y));
			this.getNode(x - 1, y).setRight(this.finish);
		} else if (y == this.height) {
			this.finish.setUp(this.getNode(x, y - 1));
			this.getNode(x, y - 1).setDown(this.finish);
		}
	}
	
	public void printMaze(Player player) {
	   int width = 0;
	   int height = 0;
	   System.out.print("  ");
	   while (width < this.width) {
		   if (this.getNode(width, height).getUp() == null) {
			   System.out.print("___ ");
		   } else if (this.getNode(width, height).getUp() == this.getFinish()){
			   System.out.print(" F  ");
		   } else if (this.getNode(width, height).getUp() == this.getStart()){
			   System.out.print(" S  ");
		   }
		   width++;
	   }
	   
	   System.out.print("\n");
	   
	   while (height < this.height) {
		   width = 0;
		   if (this.getNode(width, height).getLeft() == null) {
			   System.out.print(" |");
		   } else if (this.getNode(width, height).getLeft() == this.getFinish()){
			   System.out.print("F ");
		   } else if (this.getNode(width, height).getLeft() == this.getStart()){
			   System.out.print("S ");
		   }
		   while (width < this.width) {
			   if (player.getPosition().equals(this.getNode(width, height))) {
				   System.out.print(" @ ");
			   } else {
				   System.out.print("   ");
			   }
			   if (this.getNode(width, height).getRight() == null) {
				   System.out.print("|");
			   } else {
				   System.out.print(" ");
			   }
			   width++;
	   	   }
		   width--;
		   if (this.getNode(width, height).getRight() == this.getFinish()){
			   System.out.print("F ");
		   } else if (this.getNode(width, height).getRight() == this.getStart()){
			   System.out.print("S ");
		   }
		   System.out.print("\n");
		   width = 0;
		   if (this.getNode(width, height).getLeft() == null) {
			   System.out.print(" |");
		   } else {
			   System.out.print("  ");
		   }
		   
		   while (width < this.width) {
			   if (this.getNode(width, height).getDown() == null) {
				   System.out.print("___");
			   } else {
				   System.out.print("   ");
			   }
			   if (this.getNode(width, height).getRight() == null) {
				   System.out.print("|");
			   } else {
				   System.out.print(" ");
			   }
			   
			   width++;
		   }
		   
		   System.out.print("\n");
		   height++;
	   }
	   height--;
	   width = 0;
	   System.out.print("   ");
	   while (width < this.width) {
		   if (this.getNode(width, height).getDown() == this.getFinish()) {
			   System.out.print("F   ");
		   } else if (this.getNode(width, height).getDown() == this.getStart()) {
			   System.out.print("S   ");
		   } else {
			   System.out.print("    ");
		   }
		   width++;
	   }
	   System.out.print("\n");
	}
	
	/**
	 * Generates a maze
	 */
	public void mazeGenerator() {
		Stack<Node> explore = new Stack<Node>();
		ArrayList<Node> visited = new ArrayList<Node>();
		Random rand = new Random();
		
		int chosenEdge = rand.nextInt(4);
		int x = 0;
		int y = 0;
		
		// Left edge
		if(chosenEdge == 0) {
			x = 0;
			y = rand.nextInt(this.height);
			this.setStart(-1, y);
			this.setFinish(this.width, this.height - 1);
		} 
		
		// Upper edge
		else if(chosenEdge == 1) {
			x = rand.nextInt(this.width);
			y = 0;
			this.setStart(x, -1);
			this.setFinish(this.width, this.height - 1);
		} 
		
		// Right edge
		else if(chosenEdge == 2) {
			x = this.width - 1;
			y = rand.nextInt(this.height);
			this.setStart(width, y);
			this.setFinish(this.width, this.height - 1);
		} 
		
		// Lower edge
		else if(chosenEdge == 3) {
			x = rand.nextInt(this.width);
			y = this.height - 1;
			this.setStart(x, height);
			this.setFinish(this.width, this.height - 1);
		}
		
		Node currNode = getNode(x, y);
		explore.add(currNode);
		visited.add(currNode);
		
		while(!explore.isEmpty()) {
			ArrayList<Node> neighbourList = unvisitNeighbour(currNode, visited);
			
			if(!neighbourList.isEmpty()) {
				int	randomNum = rand.nextInt(neighbourList.size());
				Node chosen = neighbourList.get(randomNum);
				
				explore.push(currNode);
				connectNodes(currNode, chosen);
				currNode = chosen;
				visited.add(chosen);
			} else {
				currNode = explore.pop();
			}
		}
	}
	
	/**
	 * Returns a list of unvisit neighbors from the given node, 
	 * if there is no unvisit neighbors, an empty list will be returned
	 * @param node the node for visiting its neighbors
	 * @param visited a list that contains the nodes that have been visited
	 * @return a list of unvisit neighbors from the given node
	 */
	private ArrayList<Node> unvisitNeighbour(Node node, ArrayList<Node> visited) {
		ArrayList<Node> unvisit = new ArrayList<Node>();
		int x = node.getX();
		int y = node.getY();
		
		// Now looks for neighbors of the given node depending on its position
		// Nodes that are not at the boarder
		if(x > 0 && x < this.width - 1 && y > 0 && y < this.height - 1) {
			if(!visited.contains(this.getNode(x, y + 1))) {
				unvisit.add(this.getNode(x, y + 1));
			}
			
			if(!visited.contains(this.getNode(x, y - 1))) {
				unvisit.add(this.getNode(x, y - 1));
			}
			
			if(!visited.contains(this.getNode(x - 1, y))) {
				unvisit.add(this.getNode(x - 1, y));
			}
			
			if(!visited.contains(this.getNode(x + 1, y))) {
				unvisit.add(this.getNode(x + 1, y));
			}
		} else {
			// Top left node
			if(x == 0 && y == 0) {
				if(!visited.contains(this.getNode(x + 1, y))) {
					unvisit.add(this.getNode(x + 1, y));
				}
				
				if(!visited.contains(this.getNode(x, y + 1))) {
					unvisit.add(this.getNode(x, y + 1));
				}
			} 
			
			// Top right node
			else if(x == this.width - 1 && y == 0) {
				if(!visited.contains(this.getNode(x - 1, y))) {
					unvisit.add(this.getNode(x - 1, y));
				}
				
				if(!visited.contains(this.getNode(x, y + 1))) {
					unvisit.add(this.getNode(x, y + 1));
				}
			} 
			
			// Bottom left node
			else if(x == 0 && y == height - 1){
				if(!visited.contains(this.getNode(x, y - 1))) {
					unvisit.add(this.getNode(x, y - 1));
				}
				
				if(!visited.contains(this.getNode(x + 1, y))) {
					unvisit.add(this.getNode(x + 1, y));
				}
			} 
			
			// Bottom right node
			else if(x == width - 1 && y == height - 1) {
				if(!visited.contains(this.getNode(x, y - 1))) {
					unvisit.add(this.getNode(x, y - 1));
				}
				
				if(!visited.contains(this.getNode(x - 1, y))) {
					unvisit.add(this.getNode(x - 1, y));
				}
			} else {
				// Nodes at the left boarder excluding the corner ones
				if(x == 0){
					if(!visited.contains(this.getNode(x, y - 1))) {
						unvisit.add(this.getNode(x, y - 1));
					}
					
					if(!visited.contains(this.getNode(x + 1, y))) {
						unvisit.add(this.getNode(x + 1, y));
					}
					
					if(!visited.contains(this.getNode(x, y + 1))) {
						unvisit.add(this.getNode(x, y + 1));
					}
				} 
				
				// Nodes at the right boarder excluding the corner ones
				else if(x == width - 1) {
					if(!visited.contains(this.getNode(x, y - 1))) {
						unvisit.add(this.getNode(x, y - 1));
					}
					
					if(!visited.contains(this.getNode(x - 1, y))) {
						unvisit.add(this.getNode(x - 1, y));
					}
					
					if(!visited.contains(this.getNode(x, y + 1))) {
						unvisit.add(this.getNode(x, y + 1));
					}
				} 
				
				// Nodes at the top boarder excluding the corner ones
				else if(y == 0) {
					if(!visited.contains(this.getNode(x - 1, y))) {
						unvisit.add(this.getNode(x - 1, y));
					}
					
					if(!visited.contains(this.getNode(x, y + 1))) {
						unvisit.add(this.getNode(x, y + 1));
					}
					
					if(!visited.contains(this.getNode(x + 1, y))) {
						unvisit.add(this.getNode(x + 1, y));
					}
				} 
				
				// Nodes at the bottom boarder excluding the corner ones
				else if(y == height - 1) {
					if(!visited.contains(this.getNode(x - 1, y))) {
						unvisit.add(this.getNode(x - 1, y));
					}
					
					if(!visited.contains(this.getNode(x, y - 1))) {
						unvisit.add(this.getNode(x, y - 1));
					}
					
					if(!visited.contains(this.getNode(x + 1, y))) {
						unvisit.add(this.getNode(x + 1, y));
					}
				}
			}
		}
		
		return unvisit;
	}
	
	/**
	 * Connects the two given nodes by comparing their coordinates
	 * @param nodeA the first node to be connected
	 * @param nodeB the second node to be connected
	 */
	private void connectNodes(Node nodeA, Node nodeB) {
		int xA = nodeA.getX();
		int yA = nodeA.getY();
		int xB = nodeB.getX();
		int yB = nodeB.getY();
		
		// If nodeA is above nodeB
		if(xA == xB && yA == yB - 1) {
			nodeA.setDown(nodeB);
			nodeB.setUp(nodeA);
		} 
		
		// If nodeA is below nodeB
		else if(xA == xB && yA == yB + 1) {
			nodeA.setUp(nodeB);
			nodeB.setDown(nodeA);
		} 
		
		// If nodeA is left to nodeB
		else if(xA == xB - 1 && yA == yB) {
			nodeA.setRight(nodeB);
			nodeB.setLeft(nodeA);
		} 
		
		// If nodeA is right to nodeB
		else if(xA == xB + 1 && yA == yB) {
			nodeA.setLeft(nodeB);
			nodeB.setRight(nodeA);
		}
	}
}
