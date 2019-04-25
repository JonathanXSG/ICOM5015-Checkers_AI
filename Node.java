import java.util.Objects;

/**
 * Class for holding information about the steps taken in Solving the puzzle 
 * @author Victor, Jonathan, Eduardo
 *
 */

public class Node {
	
//	private Puzzle state;
	private Board board;
	private int value;
	private Node parentNode;
//	private String actionTaken;
	private Pair<Integer,Integer> coordinatesMove;
	
	/**
	 * Constructor for creating a Node object 
	 * @param currentState State of the puzzle at the current time
	 * @param parent Parent of the current Node (null if it is the first node)
	 * @param action Action taken by a parent node after finding the best rational choice (left, right, up, down)
	 * @param coordinates Final coordinates of the movement done in the puzzle
	 */
	public Node(Node parent,  Pair<Integer,Integer> coordinates, int nodeValue, Board currentBoard) {
		// TODO Auto-generated constructor stub
		//state = currentState;
		board = currentBoard;
		value = nodeValue;
		parentNode = parent;
//		actionTaken = action;
		coordinatesMove = coordinates;
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
	
	/**
	 * Method for setting the parent of the current node
	 * @param parent Node that is going to be the parent of the current node
	 */
	public void setParentNode(Node parent){
		parentNode = parent;
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
	 * @param stateSet the state that is going to be the set to the node
	 */
	public void setState(Board boardSet){
		board = boardSet;
	}
	
	/**
	 * Method for getting the total cost of the path traversed until the current node
	 * @return the sum of the costs incurred in the path to this node
	 */
//	public int getPathCost(){
//		return state.getgCost() + state.gethCost();
//	}

	/**
	 * Method for getting the action done by the parent to get to this node
	 * @return Movement done by the parent node (left, right, up, down) 
	 */
	public int getAction(){
		return value;
	}
	
	/**
	 * Method for setting the action done to particular node
	 * @param action String of the action to be set
	 */
	public void setValue(int newValue){
		value = newValue;
	}
	
	/**
	 * Method for getting the final coordinates of movement done in the parent node's puzzle state
	 * @return pair of end coordinates (x,y) of the movement done 
	 */
	public Pair<Integer, Integer> getCoordinates(){
		return coordinatesMove;
	}
	
	/**
	 * Method for setting final coordinates of movement to the node
	 * @param coordinates pair of coordinates to be set to the node
	 */
	public void setCoordinates(Pair<Integer, Integer> coordinates){
		coordinatesMove = coordinates;
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
}
