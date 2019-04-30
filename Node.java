import java.util.Objects;
import java.util.ArrayList;

/**
 * Class for holding information about the steps taken in Solving the puzzle 
 * @author Victor, Jonathan, Eduardo
 *
 */

public class Node {
	
//	private Board state;
	private int nodeAlpha;
	private int nodeBeta;
	private Board board;
	private int value;
	private Node parentNode;
	private String actionTaken;
	private ArrayList<Node> childrenNodes;
//	private Pair<Integer,Integer> coordinatesMove;
	
	/**
	 * Constructor for creating a Node object 
	 * @param currentBoard State of the board at the current time
	 * @param parent Parent of the current Node (null if it is the first node)
	 */
	public Node(Node parent, int nodeValue, Board currentBoard, String action, int alpha, int beta) {
		board = currentBoard;
		value = nodeValue;
		parentNode = parent;
		actionTaken = action;
		childrenNodes = new ArrayList<>();
		alpha = nodeAlpha;
		beta = nodeBeta;
//		coordinatesMove = coordinates;
	}

	public Node() {
	}

	/**
	 * Method for finding the node that is the parent of the current node
	 * @return Current Node's Parent Node
	 */
	public Node getParentNode(){
		return parentNode;
	}
	
	public String getAction(){
		return actionTaken;
	}

	
	/**
	 * Method for getting the state of the puzzle at the current node
	 * @return organization of the puzzle at the current node
	 */
	public Board getState(){
		return board;
	}
	
	/**
	 * Method for setting a state to the current node
	 * @param state the state that is going to be the set to the node
	 */
	public void setState(Board state){
		board = state;
	}

	/**
	 * Method for getting the value of the current state
	 * @return Movement done by the parent node (left, right, up, down) 
	 */
	public int getValue(){
		return value;
	}
	
	/**
	 * Method for setting the value of the current state
	 * @param newValue integer value of this state
	 */
	public void setValue(int newValue){
		value = newValue;
	}
	
	public void setAlpha(int newValue){
		nodeAlpha = newValue;
	}
	
	public void setBeta(int newValue){
		nodeBeta = newValue;
	}
	
	public int getAlpha(){
		return nodeAlpha;
	}
	
	public int getBeta(){
		return nodeBeta;
	}
	
	public void addChild(Node child){
		childrenNodes.add(child);
	}
	
	public int childrenNum(){
		return childrenNodes.size();
	}
	
	public Node child(int index){
		return childrenNodes.get(index);
	}

	public ArrayList<Node> getChildren() {
		return childrenNodes;
	}

	//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Node node = (Node) o;
//        return Objects.equals(state, node.state);
//    }
//
//    @Override
//    public int hashCode() {
//
//        return Objects.hash(state);
//    }

	public void print(String prefix, boolean isTail) {
		System.out.println(prefix + (isTail ? "└── " : "├── ") + actionTaken + " V= "+value + " A= "+nodeAlpha + " B= "+nodeBeta);
		for (int i = 0; i < childrenNodes.size() - 1; i++) {
			childrenNodes.get(i).print(prefix + (isTail ? "    " : "│   "), false);
		}
		if (childrenNodes.size() > 0) {
			childrenNodes.get(childrenNodes.size() - 1)
					.print(prefix + (isTail ?"    " : "│   "), true);
		}
	}
}
