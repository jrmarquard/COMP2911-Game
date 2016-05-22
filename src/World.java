import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

/**
 * World contains:
 * - 1 maze
 * - Unlimited beings
 * - Unlimited items
 * 
 * @author John
 *
 */
public class World {
    
    private String name;
    
    // Maze data
    private ArrayList<ArrayList<Node>> nodes;
    private Node start;
    private Node finish;
    
    private int width;
    private int height;
    
    // Multiple beings in a world
    // Beings are things that can move/make decisions
    private Map<String, Being> beings;
    
    // Multiple items in a world.
    // Items are stationary objects that can be interacted with by 
    private ArrayList<Item> items;
    
    public World (String name, int width, int height) {
        this.setName(name);
        
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
        
        
        this.beings = new HashMap<String, Being>();
        this.items = new ArrayList<Item>();
        
        mazeGenerator();
        
        // Add player
        
        // Generate coins
        generateCoins();
    }
    
    public void addPlayer(String name) {
        Being player = new Being(getStartNode(), name);
        beings.put(name, player);
    }
    
    public void generateCoins() {
        
        float h = (float)getHeight();
        float w = (float)getWidth();
        float r = (float)15;
        int numberOfCoins = (int)(h*w*(r/100));
        
        Random rand = new Random();
        int xC = rand.nextInt(getWidth());
        int yC = rand.nextInt(getHeight());
        int coinValue = 50;
        
        for (int x = 0; x < numberOfCoins; x++) {
            while (!uniqueCoordinates(xC, yC)) {
                xC = rand.nextInt(getWidth());
                yC = rand.nextInt(getHeight());
                coinValue = 5+rand.nextInt(80);
            }
            Coins coins = new Coins(getNode(xC,yC),coinValue);
            items.add(coins);
        }
    }
    
    private boolean uniqueCoordinates(int x, int y) {
        if (isStart(x,y)) return false;
        if (isFinish(x,y)) return false;
        for (Item i : items) {
            if (i.getNode().equals(new Node(x,y))) return false;
        }
        return true;
    }

    public int getPlayerCoins(String id) {
        return beings.get(id).getCoins();
    }
    
    public ArrayList<Node> getEntityNodes() {
        ArrayList<Node> coords = new ArrayList<Node>();
        for (Item i : items) {
            coords.add(i.getNode());
        }
        return coords;
    }
    
    public void moveBeing(String id, String dir) {
        Being b = beings.get(id);
        if (b != null) {
            if (dir == "up" && isUp(b.getNode())) {
                b.setNode(b.getNode().getUp());
            } else if (dir == "down" && isDown(b.getNode())) {
                b.setNode(b.getNode().getDown());
            } else if (dir == "left" && isLeft(b.getNode())) {
                b.setNode(b.getNode().getLeft());
            } else if (dir == "right" && isRight(b.getNode())) {
                b.setNode(b.getNode().getRight());
            }
        }
        update();
    }    

    /**
     * Run this after any changes in the maze. It checks for anything
     * that needs to be updated. This includes:
     * - win conditions
     * - entity collisions
     *     - player picks up coins
     *     - player dies
     *     
     * If something has happened, ask the GUI to redraw the world.
     */
    public void update() {        
        entityCollision();
    }
    
    private void entityCollision () {
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            Entity e = iter.next();
            
            for(Map.Entry<String,Being> entry : beings.entrySet()) {
                Being b = entry.getValue();
                if (b.getNode().equals(e.getNode())) {
                    if (e instanceof Coins) {
                        b.addCoins(((Coins)e).getValue());
                        iter.remove();
                    }
                }
            }
        }
    }
    
    /**
     * Return the coordinates of the player
     * 
     * @return x coordinate of the player
     */
    public Node getPlayerCoordinate () {
        return beings.get("Moneymaker").getNode();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Node getBeingCoordinate(String id) {
        return beings.get(id).getNode();
    }
    
    // From where I imported the maze class, need to fix all this up.
    public Node getStartNode() {
        return this.start;
    }
    
    public Node getFinishNode() {
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
    public boolean isAdjacent(int x1, int y1, int x2, int y2) {
        return getNode(x1,y1).isAdjacent(getNode(x2,y2));
    }
    
    public boolean isDown(Node n) {
        if (n.getDown() == null) {
            return false;
        } else {
            return true;
        }
    }
    
    public boolean isUp(Node n) {
        if (n.getUp() == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isRight(Node n) {
        if (n.getRight() == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isLeft(Node n) {
        if (n.getLeft() == null) {
            return false;
        } else {
            return true;
        }
    }
    
    public boolean isStart(int x, int y) {
        return getStartNode().equals(new Node(x,y));
    }

    public boolean isFinish(int x, int y) {
        return getFinishNode().equals(new Node(x,y));
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
    
    /**
     * Generates a maze
     */
    public void mazeGenerator() {
        Stack<Node> explore = new Stack<Node>();
        ArrayList<Node> visited = new ArrayList<Node>();
        Random rand = new Random();
        
        this.start = this.getNode(rand.nextInt(this.width), rand.nextInt(this.height));
        Node currNode = this.getStartNode();
        explore.add(currNode);
        visited.add(currNode);
        
        while(!explore.isEmpty()) {
            ArrayList<Node> neighbourList = unvisitNeighbour(currNode, visited);
            
            if(!neighbourList.isEmpty()) {
                int randomNum = rand.nextInt(neighbourList.size());
                Node chosen = neighbourList.get(randomNum);
                
                explore.push(currNode);
                connectNodes(currNode, chosen);
                currNode = chosen;
                visited.add(chosen);
            } else {
                currNode = explore.pop();
            }
        }
        this.findAndSetFinish();
    }
    
    private void findAndSetFinish() {
        Queue<Node> newExplore = new LinkedList<Node>();
        LinkedList<Node> newVisited = new LinkedList<Node>();
        
        newExplore.add(getStartNode());
        while (!newExplore.isEmpty()){
            Node n = newExplore.remove();
            newVisited.add(n);
            
            ArrayList<Node> reachable = new ArrayList<Node>();
            if (n.getLeft() != null) reachable.add(n.getLeft());
            if (n.getDown() != null) reachable.add(n.getDown());
            if (n.getRight() != null) reachable.add(n.getRight());
            if (n.getUp() != null) reachable.add(n.getUp());
            
            for(Node neighbour: reachable){
                if (!newVisited.contains(neighbour)){
                    newExplore.add(neighbour);
                }
            }
        }
        
        Node node = newVisited.getLast();
        this.finish = this.getNode(node.getX(), node.getY());
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
        
        // Looks for upper neighbour
        if(y + 1 < this.height) {
            if(!visited.contains(this.getNode(x, y + 1))) {
                unvisit.add(this.getNode(x, y + 1));
            }
        }
        
        // Looks for lower neighbour
        if(y - 1 >= 0) {
            if(!visited.contains(this.getNode(x, y - 1))) {
                unvisit.add(this.getNode(x, y - 1));
            }
        }
        
        // Looks for left neighbour
        if(x - 1 >= 0) {
            if(!visited.contains(this.getNode(x - 1, y))) {
                unvisit.add(this.getNode(x - 1, y));
            }
        }
        
        // Looks for right neighbour
        if(x + 1 < this.width) {
            if(!visited.contains(this.getNode(x + 1, y))) {
                unvisit.add(this.getNode(x + 1, y));
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

    public Node getPlayerNode() {
        return beings.get("Moneymaker").getNode();
    }    
}
