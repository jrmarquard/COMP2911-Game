
public class Player {

	private Node position;
	
	public Player(int x, int y){
		this.position = new Node(x ,y);
	}
	
	public Player(Node node){
		this.position = node;
	}
	
	public Node getPosition(){
		return this.position;
	}
	
	public void setPosition(int x, int y){
		this.position = new Node(x, y);
	}
	
	public void setPosition(Node position){
		this.position = position;
	}
	
}
