import java.util.ArrayList;
import java.util.Random;

public class AIPlayer {
    private ArrayList<ArrayList<Pair<Integer,Integer>>> tempMoves = new ArrayList<>();
    private Piece playerPiece;
    private Node nextMove;
    private int maxDepth;
    private EvaluationFunction evaluator;
    private Random generator = new Random();
    static int MAX = 1000; 
    static int MIN = -1000; 

    public AIPlayer(Piece player, int depth, EvaluationFunction evaluatorFunc) {
        playerPiece = player;
        maxDepth = depth;
        evaluator = evaluatorFunc;
    }

    public Node calculateMove(Node currentBoard){
        createTree(maxDepth, playerPiece, currentBoard);
        Node bestNode = abPruning(currentBoard,-1000, 1000,0, playerPiece==Piece.Black);
        return bestNode;
    }

    void createTree(int depth, Piece player, Node root){
        int Min = -1000;
        int Max = 1000;
        Node child;
        //Getting all pieces a player controls
        ArrayList<Pair<Integer,Integer>> pieces = root.getState().getAllPieceLocations(player);
        boolean onlyKills = false;

        //Check if the player has a move that can jump, if so these are the only moves allowed
        for (Pair<Integer, Integer> piece : pieces) {
            ArrayList<Pair<Integer, Integer>> verifyCaptures = root.getState()
                    .getValidJumps(piece.posX, piece.posY, player,
                            root.getState().isPieceKing(piece.posX, piece.posY), true);
            if (verifyCaptures.size() > 0) {
                onlyKills = true;
                break;
            }
        }

        //Only allowed jumps
        if(onlyKills){
            for (Pair<Integer, Integer> piece : pieces) {
                //Get all the possible jump sequences that it the piece can do
                getChainMoves(piece.posX, piece.posY, player,
                        root.getState().isPieceKing(piece.posX, piece.posY), new ArrayList<>(), root.getState());

                //making all the combination of jumps and saving them
                for (ArrayList<Pair<Integer, Integer>> tempMove : tempMoves) {
                    Board hypotheticalBoard1 = new Board(root.getState());
                    for (Pair<Integer, Integer> aTempMove : tempMove) {
                        hypotheticalBoard1.makeMove(player, piece, aTempMove);
                    }
                    String action = piece.toString() + " => " + tempMove.get(tempMove.size() - 1).toString();
                    child = new Node(root, (player == Piece.Black) ? Max : Min, hypotheticalBoard1,
                            new ArrayList<>(tempMove),0,0);
                    child.getAction().add(0, piece);
                    root.addChild(child);
                }
                tempMoves = new ArrayList<>();
            }
            //No jumps available so normal moves
        }else{
            for (Pair<Integer, Integer> piece : pieces) {
                //Get all possible normal moves not being blocked by another piece
                ArrayList<Pair<Integer, Integer>> moves = root.getState()
                        .getValidDiagonals(piece.posX, piece.posY, player);

                //Do all of those moves and save them to the tree
                for (Pair<Integer, Integer> move : moves) {
                    Board hypotheticalBoard2 = new Board(root.getState());
                    hypotheticalBoard2.makeMove(player, piece, move);
//                        String action = piece.toString() + " => " + move.toString();

                    child = new Node(root, (player == Piece.Black) ? Max : Min, hypotheticalBoard2,
                            new ArrayList<>(),0,0);
                    child.getAction().add(piece);
                    child.getAction().add(move);

                    root.addChild(child);
                }
            }
        }

        //Once we are done doing all the possible moves for each piece from this player, we change players
        //and do each possible move by the opponent on those moves by the player
        player = (player == Piece.Black)? Piece.Red : Piece.Black;
        if(depth > 1)
            for(Node childNode : root.getChildren()){
                createTree(depth-1, player, childNode);
            }
    }

    // Recursively find all the chain moves that a player can make
    //Eachof these chain moves wil be an array of coordinates for the jumps.
    void getChainMoves(int x, int y, Piece player,boolean king, ArrayList<Pair<Integer,Integer>> sequence, Board currentBoard){
        ArrayList<Pair<Integer,Integer>> extraJumps = currentBoard.getValidJumps(x,y, player, king, true);
        if(extraJumps.size() == 0){
            if(!sequence.isEmpty())
                tempMoves.add(sequence);
            return;
        }
        for(int i = 0; i < extraJumps.size(); i++){
            ArrayList<Pair<Integer,Integer>> sequenceTemp = new ArrayList<>(sequence);
            sequenceTemp.add(new Pair<>(extraJumps.get(i).posX,extraJumps.get(i).posY));
            Board hypotheticalBoard = new Board(currentBoard);
            hypotheticalBoard.makeMove(player,new Pair<>(x,y), new Pair<>(extraJumps.get(i).posX, extraJumps.get(i).posY));
            getChainMoves(extraJumps.get(i).posX, extraJumps.get(i).posY, player, king, sequenceTemp, hypotheticalBoard);
        }
    }

    Node abPruning(Node root, int alpha, int beta, int depth, boolean maxPlayer){
        // If we are at a leaf, then do the evaluation function to give a value tot ge board state
        if(root.childrenNum()==0){
            root.setValue(evaluator.evaluate(root.getState(), maxPlayer? Piece.Red : Piece.Black, false));
            return root;
        }else{
        	ArrayList<Integer> indexes = new ArrayList<>();
            if(maxPlayer){
            	int bestValue = MIN;
//            	Node bestNode = root;
            	Node node = root;
                //Node bestNode = root;
                for(int i = root.childrenNum()-1; i >= 0; i--){
                	
                	int randomIndex = generator.nextInt(root.childrenNum());
                	while(indexes.contains(randomIndex)){
                		randomIndex = generator.nextInt(root.childrenNum());
                	}
                    Node valNode = abPruning(root.child(randomIndex), alpha, beta, depth+1, false);
                    indexes.add(randomIndex);
                    bestValue = Math.max(bestValue, valNode.getValue());

                    if(bestValue == valNode.getValue() && node.getValue() != valNode.getValue()){
                        node.setValue(bestValue);
                    }
                    if ((bestValue > alpha) && depth == 0) {
                        nextMove = root.child(randomIndex);
                        node = root.child(randomIndex);
                    }
                    alpha = Math.max(alpha, bestValue);
                    root.setBeta(beta);
                    root.setAlpha(alpha);
                    if(beta <= alpha){
                        break;
                    }
                }
                return node;
            }else{
            	int bestValue = MAX;
            	Node node = root;
                for(int i = root.childrenNum()-1; i >= 0; i--){
                	int randomIndex = generator.nextInt(root.childrenNum());
                	while(indexes.contains(randomIndex)){
                		randomIndex = generator.nextInt(root.childrenNum());
                	}
                    Node valNode = abPruning(root.child(randomIndex), alpha, beta, depth+1, true);
                    indexes.add(randomIndex);
                    bestValue = Math.min(bestValue, valNode.getValue());
                    if(bestValue == valNode.getValue() && node.getValue() != valNode.getValue()){
                        node.setValue(bestValue);
                    }
                    if ((bestValue < beta) && depth == 0) {
                        nextMove = root.child(randomIndex);
                        node = root.child(randomIndex);
                    }
                    beta = Math.min(beta, bestValue);
                    root.setBeta(beta);
                    root.setAlpha(alpha);
                    if(beta <= alpha){
                        break;
                    }
                }
                return node;
            }
        }
    }

    public Node getNextMove() {
        return nextMove;
    }
}
