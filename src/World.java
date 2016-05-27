import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Semaphore;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

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
    // World properies
    private String name;
    private App app;
    private boolean updateFlag;
    private boolean worldChangeFlag;
    private boolean enemiesEnabled;
    
    // Maze data
    private ArrayList<ArrayList<Node>> nodes;
    private Node start;
    private Node finish;
    private int width;
    private int height;
    
    // Copying in from merge
    private Node doorStart;
    private Node doorFinish;
    private Node key;
    
    // Lighting
    private ArrayList<Node> visibleNodes;
    private int maxVisDistance;
    
    // Multiple beings in a world
    // Beings are things that can move/make decisions
    private Map<String, Entity> entities;
    
    // Multiple items in a world.
    // Items are stationary objects that can be interacted with by 
    private ArrayList<Item> items;

    //Synchronization
    private Semaphore visibilitySemaphore;
    private Semaphore itemSemaphore;
    private Semaphore entitySemaphore;
    
    // Schedule for the tick service
    private ScheduledExecutorService worldTickTock;
    private static final TimeUnit SCHEDULE_TIME_UNIT = TimeUnit.MILLISECONDS;
    private static final int WORLD_TICK_DELAY = 200; 
    private static final int WORLD_TICK_RATE = 50; 
    private int worldTickCount;

    /**
     * Executor to run the AIs in
     */
    private ScheduledExecutorService aiPool;
    private static final int AI_POOL_DELAY = 600; 
    private static final int AI_POOL_RATE = 150; 
    
    public World (App app, String name, int height, int width, Node start, Node finish, Node doorStart, Node doorFinish, Node key, ArrayList<ArrayList<Node>> nodes, ArrayList<Item> items) {
        // Global settings
        this.app = app;
        this.name = name;
        this.width = width;
        this.height = height;
        this.updateFlag = false;
        this.worldChangeFlag = false;
        this.enemiesEnabled = App.pref.getBool("enemy");
        this.worldTickCount = 0;
        this.maxVisDistance = App.pref.getValue("visibleRange");
        this.visibleNodes = new ArrayList<Node>();
        
        // Semaphores
        this.visibilitySemaphore = new Semaphore(1, true);
        this.itemSemaphore = new Semaphore(1, true);
        this.entitySemaphore = new Semaphore(1, true);
        
        // Schedulers
        this.worldTickTock = Executors.newSingleThreadScheduledExecutor();
        this.aiPool = Executors.newScheduledThreadPool(4);
        
        worldTickTock.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                tickUpdate();
            }
        }, WORLD_TICK_DELAY, WORLD_TICK_RATE, SCHEDULE_TIME_UNIT);
        
        // Copy maze and item collections
        this.nodes = new ArrayList<ArrayList<Node>>(nodes);
        this.items = new ArrayList<Item>(items);
        
        // Create entity collection
        this.entities = new ConcurrentHashMap<String, Entity>();
        
        // Copy special nodes in the maze
        this.start = start;
        this.finish = finish;
        this.doorStart = doorStart;
        this.doorFinish = doorFinish;
        this.key = key;
                
        // If visibility is turned off make all the tiles bright.
        if (maxVisDistance == -1) {
            for (ArrayList<Node> an : nodes) {
                for (Node n : an) {
                    n.setVisibility(0f);
                }
            }
        } else {
            calculateVisibility(start);            
        }
        
        // If enemies are enabled, create 1
        if (enemiesEnabled) {
            this.addEnemy("Enemy");
            aiRunnable AIRunEnemy = new aiRunnable(new AIEnemy(this, "Enemy"));
            aiPool.scheduleAtFixedRate(AIRunEnemy, AI_POOL_DELAY, AI_POOL_RATE, SCHEDULE_TIME_UNIT);
        }        
    }
    
    public World (App app, String name, int width, int height, boolean doorAndKey, 
    		boolean coins) {        
        // Global settings
        this.app = app;
        this.name = name;
        this.width = width;
        this.height = height;
        this.updateFlag = false;
        this.worldChangeFlag = false;
        this.enemiesEnabled = App.pref.getBool("enemy");
        this.worldTickCount = 0;
        this.maxVisDistance = App.pref.getValue("visibleRange");
        this.visibleNodes = new ArrayList<Node>();
        
        // Semaphore initialisation
        this.visibilitySemaphore = new Semaphore(1, true);
        this.itemSemaphore = new Semaphore(1, true);
        this.entitySemaphore = new Semaphore(1, true);
        
        // Schedulers
        this.worldTickTock = Executors.newSingleThreadScheduledExecutor();
        this.aiPool = Executors.newScheduledThreadPool(4);
        
        this.worldTickTock.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                tickUpdate();
            }
        }, WORLD_TICK_DELAY, WORLD_TICK_RATE, SCHEDULE_TIME_UNIT);
        
        // Create the maze nodes
        this.nodes = new ArrayList<ArrayList<Node>>();
        
        for (int w = 0; w < width; w++) {
            this.nodes.add(new ArrayList<Node>());
            for (int h = 0; h < height; h++) {
                this.nodes.get(w).add(new Node(w, h));
            }
        }
        
        // Create entity and item collections
        this.entities = new ConcurrentHashMap<String, Entity>();
        this.items = new ArrayList<Item>();
        
        // Special nodes within the maze
        this.start = null;
        this.finish = null;
        this.doorStart = null;
        this.doorFinish = null;
        this.key = null;
        
        // Generate maze items
        mazeGenerator();
        if (doorAndKey) {
            doorAndKeyGenerator();
        }
        if (coins) {
        	generateCoins();
        }
        
        // If visibility is turned off make all the tiles bright.
        if (maxVisDistance == -1) {
            for (ArrayList<Node> an : nodes) {
                for (Node n : an) {
                    n.setVisibility(0f);
                }
            }
        } else {
            calculateVisibility(start);            
        }
        
        // If enemies are enabled, create 1
        if (enemiesEnabled) {
            this.addEnemy("Enemy");
        }  
    }
    
    /**
     * A wrapper to send a message to the App
     * @param c
     */
    private void sendMessageToApp(Message m) {
        app.sendMessage(m);
    }

    
    /**
     * This updates parts of the world that need to change every
     * WORLD_TICK_RATE number of milliseconds.
     */
    private void tickUpdate() {
        worldTickCount++;
        
        // Go through items and decay energy
        Iterator<Item> itemItr = items.iterator();
        while (itemItr.hasNext()) {
            Item i = itemItr.next();
            if (i.getType() == Item.ENERGY) {
                i.decay();
                if (i.getDecay() == 0) {
                    itemItr.remove();
                    worldChangeFlag = true;
                }
            }
        }
    }

    /**
     * Run this after any changes in the maze. It checks for anything
     * that needs to be updated. This includes:
     * - win conditions
     * - entity collisions
     *     - player picks up coins
     *     - player dies
     */
    private void update() {
        // runs the collision checks for the world
        collision();
        
        // Recalculates the lighting
        calculateVisibility(entities.get("Moneymaker").getNode());
    }

    /**
     * collision runs checks for all the entities and items in the game
     */
    private void collision() {
        try {
            this.itemSemaphore.acquire();
            this.entitySemaphore.acquire();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        // Entity collision
        Iterator<String> iterEntity = entities.keySet().iterator();
        while (iterEntity.hasNext()) {
            Entity e = entities.get(iterEntity.next());
            
            // If the player is dead they can't do anything
            if (e.isDead()) continue;
            
            // Finish check
            if (e.getName().equals("Moneymaker") && e.getNode().equals(finish)) {
                // winner winner chicken dinner
                sendMessageToApp(new Message(Message.GAME_MSG, new String[]{"pause"}));
                sendMessageToApp(new Message(Message.SOUND_MSG, new String[]{"play", "finish"}));
                worldChangeFlag = true;
            }
            
            // Key check
            if (e.getNode().equals(key)) {
                e.setKey(true);
                sendMessageToApp(new Message(Message.SOUND_MSG, new String[]{"play", "key"}));
                key = null;
                worldChangeFlag = true;
            }
            
            // Opening door check
            if (e.getNode().equals(doorStart)) {
                if (e.getKey()) {
                    connectNodes(doorStart, doorFinish);
                    sendMessageToApp(new Message(Message.SOUND_MSG, new String[]{"play", "door"}));
                    doorStart = null;
                    doorFinish = null;
                    worldChangeFlag = true;
                }
            }
            
            // Being attack check
            if (e.getName().equals("Moneymaker")) {
                Entity enemyBeing = entities.get("Enemy");
                // if there are no enemies
                if (enemyBeing == null) {
                    continue;
                }
                Node enemyNode = enemyBeing.getNode();
                if (enemyBeing.isDead()) continue;
                if (e.getNode().equals(enemyNode)) {
                    e.setDead(true);
                    sendMessageToApp(new Message(Message.SOUND_MSG, new String[]{"play", "death"}));
                    worldChangeFlag = true;
                }
            }
        }
        
        // Item collision
        Iterator<Item> itemItr = items.iterator();
        while (itemItr.hasNext()) {
            Item i = itemItr.next();
            
            for(Map.Entry<String, Entity> entry : entities.entrySet()) {
                Entity e = entry.getValue();
                
                if(!e.getName().equals("Enemy")) {
                	if (e.getNode().equals(i.getNode())) {
                        if (i.getType() == Item.COIN) {
                            e.addCoins((i).getValue());
                            itemItr.remove();
                            sendMessageToApp(new Message(Message.SOUND_MSG, new String[]{"play", "coin"}));
                            worldChangeFlag = true;
                        }
                    }
                }
            }
        }
        
        this.entitySemaphore.release();
        this.itemSemaphore.release();
    }

    /**
     * Calculate the visibility of each node from a given node.
     * 
     * @param node The node to start from.
     */
    private void calculateVisibility(Node startNode) {
        
        // if visibility is turned off don't calculate
        if (maxVisDistance == -1) return;
        
        try {
			this.visibilitySemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        // Resets the nodes that were visible to be dark
        for (Node n : visibleNodes) {
            n.setVisibility(0);
        }
        visibleNodes.clear();
        
        // Calculate the visibility 'resolution'
        float visRes = 100.0f/(float)maxVisDistance;
        
        // Queue of nodes to check
        Queue<Node> nodesToCheck = new LinkedList<Node>();
        nodesToCheck.add(startNode);
        
        // Set the starting node to be completely lit
        startNode.setVisibility(0.0f);
        while (!nodesToCheck.isEmpty()) {
            Node n = nodesToCheck.remove();

            // If the node has visibility lower than the darkest possible visibiliity
            if (n.getVisibility() <= 100-visRes) {
                // Add it to the list of all visibile nodes
                visibleNodes.add(n);
                
                // Add all it's children to the list of nodes to check if they haven't been checked already
                // and then set their visibility based on the current node.
                for (Node m : n.getConnectedNodes()) {
                    if (!nodesToCheck.contains(m) && !visibleNodes.contains(m)) {
                        nodesToCheck.add(m);
                        m.setVisibility(n.getVisibility()+visRes);
                    }
                }
            }
            
        }
        this.visibilitySemaphore.release();
    }

    /**
     * Method allowing external objects to update the game state.
     * @param string The message send to the world.
     */
    public void sendMessage(String[] message) {
        switch (message[1]) {
            case "move": entityMove(message[2], message[3]); break;
            case "melee": entityMeleeAttack(message[2]); break;
            case "range": entityRangeAttack(message[2]); break;
        
        }
    }
    
    private void entityRangeAttack(String string) {
        // TODO Auto-generated method stub
        
    }

    private void entityMeleeAttack(String entityName) {
        try {
            this.entitySemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Entity entityAttacking = entities.get(entityName);
        
        if (entityAttacking.isDead()) {
        	this.entitySemaphore.release();
            return;
        } else {
            sendMessageToApp(new Message(Message.SOUND_MSG, new String[]{"play", "sword_swing"}));Node enemyAttackingNode = entityAttacking.getNode();        
            
            Iterator<String> iterEntity = entities.keySet().iterator();
            while (iterEntity.hasNext()) {
                Entity entity = entities.get(iterEntity.next());
                Node entityNode = entity.getNode();
                
                // If the being is dead can't kill it twice
                if (entity.isDead()) continue;
                
                // Swing in every direction
                for (Node m : enemyAttackingNode.getConnectedNodes()) {
                    Item energy = new Item(m, Item.ENERGY, 3);
                    items.add(energy);
                    if (entityNode.equals(m)) {
                        sendMessageToApp(new Message(Message.SOUND_MSG, new String[]{"play", "death"}));
                        entity.setDead(true);
                    }
                }
            }
        worldChangeFlag = true;
        entitySemaphore.release();
        }
    }
    
    private void entityMove(String id, String dir) {
        Entity b = entities.get(id);
        if (b.isDead()) return;
        
        Node n = b.getNode();
        if (b != null) {
            if (dir == "up" && n.getUp() != null) {
                b.setNode(n.getUp()); 
                b.setDirection("up");
                updateFlag = true;
            } else if (dir == "down" && n.getDown() != null) {
                b.setNode(n.getDown()); 
                b.setDirection("down");
                updateFlag = true;
            } else if (dir == "left" && n.getLeft() != null) {
                b.setNode(n.getLeft());
                b.setDirection("left");
                updateFlag = true;
            } else if (dir == "right" && n.getRight() != null) {
                b.setNode(n.getRight());
                b.setDirection("right");
                updateFlag = true;
            }
            if (updateFlag) {
                sendMessageToApp(new Message(Message.SOUND_MSG, new String[]{"play", "step"}));
                updateFlag = false;
                worldChangeFlag = true;
            }
        }
        update();
    }

    public int getPlayerCoins(String id) {
        return entities.get(id).getCoins();
    }
    
    public ArrayList<Node> getEntityNodes() {
        ArrayList<Node> coords = new ArrayList<Node>();
        for (Item i : items) {
            coords.add(i.getNode());
        }
        return coords;
    }

    public boolean isBeingDead(String string) {
        return entities.get(string).isDead();
    }

    public ArrayList<Item> getItems() {
        return items;
    }
    
    public boolean isWorldChangeFlag() {
        return worldChangeFlag;
    }

    public void setWorldChangeFlag(boolean worldChangeFlag) {
        this.worldChangeFlag = worldChangeFlag;
    }    

    /**
     * Add enemy into the world.
     * @param name Name of the player.
     */
    public void addEnemy(String name) {
        Entity enemy = new Entity(this.finish, name);
        entities.put(name, enemy);
        aiRunnable AIRunEnemy = new aiRunnable(new AIEnemy(this, "Enemy"));
        aiPool.scheduleAtFixedRate(AIRunEnemy, AI_POOL_DELAY, AI_POOL_RATE, SCHEDULE_TIME_UNIT);
    }
    
    public boolean isEnemyInMaze() {
    	return this.entities.containsKey("Enemy");
    }

    /**
     * Add being into the world.
     * @param name Name of the enemy
     */
    public void addPlayer(String name, String opt) {
        Node n = null;
        if (name.equals("Moneymaker")) {
            n = this.start;
        } else if (name.equals("Teadrinker")) {
            n = this.finish;
        } 
        Entity player = new Entity(n, name);
        entities.put(name, player);
        if (opt.equals("Easy AI")) {
            aiRunnable AIRun = new aiRunnable(new AIPlayer(this, name, "easy"));
            aiPool.scheduleAtFixedRate(AIRun, AI_POOL_DELAY, AI_POOL_RATE, SCHEDULE_TIME_UNIT);
        } else if (opt.equals("Med AI")) {
            aiRunnable air = new aiRunnable(new AIPlayer(this, name, "med"));
            aiPool.scheduleAtFixedRate(air, AI_POOL_DELAY, AI_POOL_RATE, SCHEDULE_TIME_UNIT);
        } else if (opt.equals("Hard AI")) {
            aiRunnable air = new aiRunnable(new AIPlayer(this, name, "hard"));
            aiPool.scheduleAtFixedRate(air, AI_POOL_DELAY, AI_POOL_RATE, SCHEDULE_TIME_UNIT);
        } else if (opt.equals("Battle AI")) {
        	aiRunnable air = new aiRunnable(new AIFighter(this, name));
            aiPool.scheduleAtFixedRate(air, AI_POOL_DELAY, AI_POOL_RATE, SCHEDULE_TIME_UNIT);
        }
    }
    /**
     * Get's the name of the world
     * @return world name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Gets the start node.
     * @return start node
     */
    public Node getStartNode() {
        return this.start;
    }
    
    /**
     * Gets the finish node
     * @return finish node
     */
    public Node getFinishNode() {
        return this.finish;
    }
    
    /**
     * Gets the width of the maze
     * @return maze width
     */
    public int getWidth() {
        return this.width;
    }
    
    /**
     * Gets the height of the maze
     * @return maze height
     */
    public int getHeight() {
        return this.height;
    }
    
    /**
     * gets the node the key is at
     * @return the node of the key
     */
    public Node getKeyNode() {
        return key;
    }

    /**
     * Gets the node an entity is at
     * @param name the entity name
     * @return the node of the entity
     */
    public Node getEntityNode(String name) {
        return entities.get(name).getNode();
    }

    /**
     * Gets the direction of an entity
     * @param name the entity name
     * @return the direction of the entity
     */
	public String getEntityDirection(String name) {
		return entities.get(name).getDirection();
	}
    
    /**
     * Get the node at the x and y coordinates given.
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return Node at x and y.
     */
    public Node getNode(int x, int y) {
        try {
            return this.nodes.get(x).get(y);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Checks if the x and y coordinates given are corrected to each other.
     * @param x1 x coordinate of node A
     * @param y1 y coordinate of node A
     * @param x2 x coordinate of node B
     * @param y2 y coordinate of node B
     * @return the visibility of the space
     */
    public boolean isConnected(int x1, int y1, int x2, int y2) {
        return getNode(x1,y1).isConnected(getNode(x2,y2));
    }

    /**
     * Gets the visibility of the node at x and y.
     * @param x the x coordinate of the node.
     * @param y the y coordinate of the node.
     * @return the visibility of the node.
     */
    public float getNodeVisibility(int x, int y) {
        return getNode(x, y).getVisibility();
    }
    
    public boolean isDoor(int xA, int yA, int xB, int yB) {
        if((this.getNode(xA, yA).equals(this.doorStart) &&
                this.getNode(xB, yB).equals(this.doorFinish)) ||
                (this.getNode(xA, yA).equals(this.doorFinish) &&
                this.getNode(xB, yB).equals(this.doorStart))) {
            return true;
        } else {
            return false;
        }
    }
    
    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ *
     *                                                                        *
     *   All of the functions below are used in generating the initial maze   *
     *                                                                        *  
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
    
    /**
     * Generates a maze
     */
    private void mazeGenerator() {
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
    
    /**
     * Generates the coins for the maze
     */
    private void generateCoins() {
        float h = (float)getHeight();
        float w = (float)getWidth();
        float r = (float)15;
        int numberOfCoins = (int)(h*w*(r/100));
        
        Random rand = new Random();
        int coinXCoord = rand.nextInt(getWidth());
        int coinYCoord = rand.nextInt(getHeight());
        int coinValue = 50;
        
        for (int x = 0; x < numberOfCoins; x++) {
            coinXCoord = rand.nextInt(getWidth());
            coinYCoord = rand.nextInt(getHeight());
            Node n = getNode(coinXCoord, coinYCoord);
            if (start.equals(n)) continue;
            if (finish.equals(n)) continue;
            for (Item i : items) {
                if (i.getNode().equals(n)) continue;
            }
            Item coin = new Item(n, Item.COIN, coinValue);
            items.add(coin);
        }
    }
    
    /**
     * Sets the finish for the maze
     */
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

    
    /**
     * Fills in the shortest path with the given start and destination
     * @param shortestPath the List that will be storing the shortest path
     * @param path the path created by bfs
     * @param start the starting point
     * @param dest the destination
     */
    private void processPath(ArrayList<Node> shortestPath, ArrayList<Node> path, 
            Node start, Node dest){
        int i = path.indexOf(dest);
        Node source = path.get(i + 1);
        
        shortestPath.add(0, dest);
        if (source.equals(start)) {
            shortestPath.add(0, start);
            return;
        } else {
            processPath(shortestPath, path, start, source);
        }
    }

    /**
     * Generates a door and a key in the maze
     */
    private void doorAndKeyGenerator() {
        ArrayList<Node> path = new ArrayList<Node>();
        ArrayList<Node> shortestPath = new ArrayList<Node>();
        boolean pathFound = false;
        
        Queue<Node> explore = new LinkedList<Node>();
        LinkedList<Node> visited = new LinkedList<Node>();
        
        // finds the shortest path from start to finish
        explore.add(start);
        while (!explore.isEmpty()){
            Node n = explore.remove();
            visited.add(n);
            
            ArrayList<Node> reachable = new ArrayList<Node>();
            if (n.getLeft() != null) reachable.add(n.getLeft());
            if (n.getDown() != null) reachable.add(n.getDown());
            if (n.getRight() != null) reachable.add(n.getRight());
            if (n.getUp() != null) reachable.add(n.getUp());
            
            int i = 0;
            while(i != reachable.size()){
                Node neighbour = reachable.get(i);
                path.add(neighbour);
                path.add(n);
                
                if (neighbour.equals(this.finish)){
                    processPath(shortestPath, path, start, this.finish);
                    pathFound = true;
                    break;
                } else if (!visited.contains(neighbour)){
                    explore.add(neighbour);
                }
                i++;
            }
            
            if(pathFound) {
                break;
            }
        }
        
        // Sets the middle point of the shortest path as a door
        // to block the path
        int halfPoint = shortestPath.size() / 2;
        this.doorStart = shortestPath.get(halfPoint);
        this.doorFinish = shortestPath.get(halfPoint + 1);
        this.disconnectNodes(this.doorStart, this.doorFinish);
        
        // Sets the cost of each of the nodes of the starting side
        HashMap<Node, Integer> nodeCostMap = new HashMap<Node, Integer>();
        this.setNodeCost(this.start, nodeCostMap);
        this.setNodeCost(this.doorStart, nodeCostMap);
        
        // Finds the node with the greatest cost from start
        // and from the door and set this as the location of the key
        for(ArrayList<Node> aList: this.nodes) {
            for(Node node: aList) {
                if(this.key == null) {
                    this.key = node;
                } else {
                	int nodeCost, keyCost;
        			
        			if(nodeCostMap.containsKey(node)) {
        				nodeCost = nodeCostMap.get(node);
        			} else {
        				nodeCost = 0;
        			}
        			
        			if(nodeCostMap.containsKey(key)) {
        				keyCost = nodeCostMap.get(key);
        			} else {
        				keyCost = 0;
        			}
        			
        			if(nodeCost > keyCost) {
        				this.key = node;
        			}
                }
            }
        }
    }
    
    /**
     * Disconnects the given two nodes, ie make a wall between them
     * @param nodeA the first node to be disconnected
     * @param nodeB the second node to be disconnected
     */
    private void disconnectNodes(Node nodeA, Node nodeB) {
        int xA = nodeA.getX();
        int yA = nodeA.getY();
        int xB = nodeB.getX();
        int yB = nodeB.getY();
        
        // If nodeA is above nodeB
        if(xA == xB && yA == yB - 1) {
            nodeA.setDown(null);
            nodeB.setUp(null);
        } 
        
        // If nodeA is below nodeB
        else if(xA == xB && yA == yB + 1) {
            nodeA.setUp(null);
            nodeB.setDown(null);
        } 
        
        // If nodeA is left to nodeB
        else if(xA == xB - 1 && yA == yB) {
            nodeA.setRight(null);
            nodeB.setLeft(null);
        } 
        
        // If nodeA is right to nodeB
        else if(xA == xB + 1 && yA == yB) {
            nodeA.setLeft(null);
            nodeB.setRight(null);
        }
    }
    

    
    /**
     * Sets the cost of the nodes from the given node by doing a bfs
     * @param node the starting node
     */
    private void setNodeCost(Node node, HashMap<Node, Integer> nodeCostMap) {
        Queue<Node> explore = new LinkedList<Node>();
        LinkedList<Node> visited = new LinkedList<Node>();
        int i = 1;
        
        explore.add(node);
        while (!explore.isEmpty()){
            Node n = explore.remove();
            visited.add(n);
            
            ArrayList<Node> reachable = new ArrayList<Node>();
            if (n.getLeft() != null) reachable.add(n.getLeft());
            if (n.getDown() != null) reachable.add(n.getDown());
            if (n.getRight() != null) reachable.add(n.getRight());
            if (n.getUp() != null) reachable.add(n.getUp());
            
            for(Node neighbour: reachable){
                if (!visited.contains(neighbour)){
                	if(nodeCostMap.containsKey(neighbour)) {
            			int neighbourCost = nodeCostMap.get(neighbour);
            			nodeCostMap.put(neighbour, neighbourCost += i);
            		} else {
            			nodeCostMap.put(neighbour, i);
            		}
                    explore.add(neighbour);
                }
            }
            
            i++;
        }
    }

    /**
     * Gets what is between two nodes at the given coordinates
     * @param x1 x coordinate of node A
     * @param y1 y coordinate of node A
     * @param x2 x coordinate of node B
     * @param y2 y coordinate of node B
     * @return string equal to either wall, door, or space
     */
    public String getWallType(int x1, int y1, int x2, int y2) {
        Node nodeA = getNode(x1, y1);
        Node nodeB = getNode(x2, y2);
        
        // If one of the nodes is not a real node.
        if (nodeA == null || nodeB == null) return "wall";
        
        // Check for door.
        if (nodeA.equals(doorStart) && nodeB.equals(doorFinish)) return "door";
        if (nodeA.equals(doorFinish) && nodeB.equals(doorStart)) return "door";
        
        // Check if they are connected.
        if (nodeA.getConnectedNodes().contains(nodeB)) return "space";
        
        // Default to wall.
        return "wall";
    }
    
    /** 
     * Gets the visibility of the space between two nodes
     * @param x1 x coordinate of node A
     * @param y1 y coordinate of node A
     * @param x2 x coordinate of node B
     * @param y2 y coordinate of node B
     * @return the visibility of the space
     */
    public float getWallVisibility(int x1, int y1, int x2, int y2) {
        try {
            this.visibilitySemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Node nodeA = getNode(x1, y1);
        Node nodeB = getNode(x2, y2);
        
        // If one of the nodes is not a real node.
        if (nodeA == null && nodeB == null) {
            visibilitySemaphore.release();
            return 100f;
        } else if (nodeA == null) {
            visibilitySemaphore.release();
            return nodeB.getVisibility();
        } else if (nodeB == null) {
            visibilitySemaphore.release();
            return nodeA.getVisibility();
        }
        
        float visA = nodeA.getVisibility();
        float visB = nodeB.getVisibility();
        visibilitySemaphore.release();
        
        String wallType = getWallType(x1, y1, x2, y2);
        if (wallType.equals("space")) {
            return (visA+visB)/2f;    
        } else {
            return visA > visB ? visB : visA;
        }   
    }
    
    public float getCornerVisibility (int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        ArrayList<Float> wallVis = new ArrayList<Float>();
        
        wallVis.add(getWallVisibility(x1,y1,x2,y2));
        wallVis.add(getWallVisibility(x2,y2,x3,y3));
        wallVis.add(getWallVisibility(x3,y3,x4,y4));
        wallVis.add(getWallVisibility(x4,y4,x1,y1));
       
        float vis = wallVis.remove(0);
        while(!wallVis.isEmpty()) {
            float temp = wallVis.remove(0);
            vis = temp > vis ? vis : temp;
        }
        return vis;
        
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Makes a deep copy of the world except for parts of the world that
     * need to remain independent from other worlds (e.g. ai pool).
     * @return copy of world
     */
    public World copy() {
        //
        return new World(
                this.app,
                this.name,
                this.height,
                this.width,
                this.start,
                this.finish,
                this.doorStart,
                this.doorFinish,
                this.key,
                this.nodes,
                this.items
                );
    }

    /**
     * Returns an array list of the entities
     * @return array list of the entities
     */
    public ArrayList<Entity> getEntities() {
        return new ArrayList<Entity>(entities.values());
    }
    


    /**
     * aiRunnable executes an ai's makeMove method and sends it to the app.
     */
    private class aiRunnable implements Runnable {
        AI ai;
        
        public aiRunnable(AI ai) {
            this.ai = ai;
        }
        
        public void run() {
            try {
                sendMessageToApp(ai.makeMove());
            } catch (Exception e){
                System.out.println("AI runnable error.");
                e.printStackTrace();
            }
        }
    };
    
    public void endWorld () {
        aiPool.shutdownNow();
        worldTickTock.shutdownNow();
    }
}













