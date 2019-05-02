import java.util.ArrayList;

public class AIPlayer {
    private ArrayList<ArrayList<Pair<Integer,Integer>>> tempMoves = new ArrayList<>();
    private Piece playerPiece;
    private Node nextMove;
    private int maxDepth;
    private EvaluationFunction evaluator;

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
        if(depth == 0){
            root.setValue(evaluator.evaluate(root.getState(), player==Piece.Black? Piece.Red: Piece.Black, false));
        }else{
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
                                new Pair[]{piece,tempMove.get(tempMove.size() - 1)},0,0);

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
                                new Pair[]{piece,move},0,0);

                        root.addChild(child);
                    }
                }
            }

            //Once we are done doing all the possible moves for each piece from this player, we change players
            //and do each possible move by the opponent on those moves by the player
            player = (player == Piece.Black)? Piece.Red : Piece.Black;
            for(Node childNode : root.getChildren()){
                createTree(depth-1, player, childNode);
            }
        }
    }

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
        Node bestNode = root;
        if(root.childrenNum()==0){
//            root.setValue(root.getState().evaluationFunction(maxPlayer? Piece.Red : Piece.Black));
            return root;
        }else{
            if(maxPlayer){
                //Node bestNode = root;
                for(int i = root.childrenNum()-1; i >= 0; i--){
                    Node valNode = abPruning(root.child(i), alpha, beta, depth+1, false);
                    int bestValue = Math.max(bestNode.getValue(), valNode.getValue());

                    if(bestValue == valNode.getValue()){
                        bestNode = valNode;
                    }
                    if ((bestValue > alpha) && depth == 0) {
                        //only update best score if alpha was better
                        nextMove = root.child(i);
                    }
                    alpha = Math.max(alpha, bestNode.getValue());
                    root.setBeta(beta);
                    root.setAlpha(alpha);
                    if(beta <= alpha){
                        break;
                    }
                }
                return bestNode;
            }else{
                for(int i = root.childrenNum()-1; i >= 0; i--){
                    Node valNode = abPruning(root.child(i), alpha, beta, depth+1, true);
                    int bestValue = Math.min(bestNode.getValue(), valNode.getValue());
                    if(bestValue == valNode.getValue()){
                        bestNode = valNode;
                    }
                    if ((bestValue < beta) && depth == 0) {
                        //only update best score if alpha was better
                        nextMove = root.child(i);
                    }
                    beta = Math.min(beta, bestNode.getValue());
                    root.setBeta(beta);
                    root.setAlpha(alpha);
                    if(beta <= alpha){
                        break;
                    }
                }
                return bestNode;
            }
        }
    }

    public Node getNextMove() {
        return nextMove;
    }
}
