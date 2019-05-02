//import java.awt.*;
//import java.awt.event.*;
//import java.util.*;
//import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static Board test = new Board(8);
    private static ArrayList<ArrayList<Pair<Integer,Integer>>> tempMoves = new ArrayList<>();
    private static ArrayList<Node> leaves = new ArrayList<Node>();


    public static void main(String[] args) {
    	
    	Scanner input = new Scanner(System.in);
    	boolean valid1 = false;
    	boolean valid2 = false;
    	boolean validMove = false;
    	String initialCord = null;
    	String finalCord = null;
    	int x1 = 0;
    	int y1 = 0;
    	int x2 = 0;
    	int y2 = 0;
    	
    	while(!valid1){
    		System.out.print("Select which one of your pieces you want to move (ej.: 1,2): ");
        	initialCord  = input.next();
        	if(Character.isDigit(initialCord.charAt(0)) && Character.isDigit(initialCord.charAt(2))){
        		x1 =  Character.getNumericValue(initialCord.charAt(0));
        		y1 =  Character.getNumericValue(initialCord.charAt(2));
        		valid1 = true;
        	}else{
        		System.out.println("Please enter valid information");
        	}
    	}
    	
    	while(!valid2){
    		System.out.print("Select where do you want to move: (ej.: 1,2): ");
        	finalCord  = input.next();
        	if(Character.isDigit(initialCord.charAt(0)) && Character.isDigit(initialCord.charAt(2))){
        		x2 =  Character.getNumericValue(finalCord.charAt(0));
        		y2 =  Character.getNumericValue(finalCord.charAt(2));
        		valid2 = true;
        	}else{
        		System.out.println("Please enter valid information");
        	}
    	}
    	
    	System.out.println("Move from: " + initialCord + " to " + finalCord);
    	Pair<Integer,Integer> firstCord = new Pair<Integer,Integer>(x1,y1);
    	Pair<Integer,Integer> secondCord = new Pair<Integer,Integer>(x2,y2);
    	input.close();
    	if(validMove = test.makeMove(Piece.Red, firstCord, secondCord)){
    		System.out.println("Succesfully moved the piece");
    	}
        long time1 = System.nanoTime();
        //test.printBoard();

//        System.out.println(test.makeMove(Piece.Red, new Pair<>(0,5), new Pair<>(2,3)));
//        System.out.println(test.makeMove(Piece.Red, new Pair<>(4,5), new Pair<>(4,3)));
//        System.out.println(test.makeMove(Piece.Red, new Pair<>(3,6), new Pair<>(4,5)));
//        System.out.println(test.makeMove(Piece.Black, new Pair<>(3,2), new Pair<>(1,4)));
//        System.out.println(test.makeMove(Piece.Red, new Pair<>(2,5), new Pair<>(0,3)));
//        test.printBoard();

        Node root = new Node(null, -1000, test, "Root",0,0);
        createTree1(0, Piece.Black, root);
        Node bestMove = abPruning(root, -1000, 1000, true);

        System.out.println(leaves.size());
        long time2 = System.nanoTime();
        root.print("",false);
        System.out.println("Time: "+ (time2-time1));
        bestMove.getState().printBoard();
        System.out.println(bestMove.getAction());

//        for(int j = 0; j < tree.size(); j++){
//        	tree.get(j).getState().printBoard();
//        }
//        ArrayList<Pair<Integer,Integer>> pieces =  test.getAllPieceLocations(Piece.Black);
//        System.out.println("All moves from Black player");
//        for(int i = 0; i < pieces.size(); i++){
//
//            test.getValidDiagonals(pieces.get(i).posX, pieces.get(i).posY, Piece.Black).forEach((p) ->
//                    tempMoves.add(new ArrayList<Pair<Integer, Integer>>(){{{add(p);}}}));
//
//            getChainMoves(pieces.get(i).posX, pieces.get(i).posY, Piece.Black,
//                    test.isPieceKing(pieces.get(i).posX, pieces.get(i).posY), sequence);
//        }
//        System.out.println(tempMoves.size());
//
//        for(int i = 0; i < tempMoves.size(); i++){
//        	System.out.println(tempMoves.get(i).toString());
//        }
        //test.getChainMoves(3, 2, Piece.Black, test, test.getValidJumps(3, 2, Piece.Black, true), test.getValidJumps(3, 2, Piece.Black, true).size(), moves, sequence);
    }

    static void getChainMoves(int x, int y, Piece player,boolean king, ArrayList<Pair<Integer,Integer>> sequence, Board currentBoard){
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
        return;
    }

    private static void printArray(int[][] array){
        for(int y = 0; y<array.length; y++){
            for (int[] anArray : array) {
                System.out.printf("%3d ", anArray[y]);
            }
            System.out.println();
        }
    }
    
    public static void createTree1(int depth, Piece player, Node root){
    	int Min = -1000;
    	int Max = 1000;
    	if(depth == 3){
    		root.setValue(root.getState().evaluationFunction(player));
    		leaves.add(root);
    		return;
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
                        child = new Node(root, (player == Piece.Black) ? Max : Min, hypotheticalBoard1, action,0,0);

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
                        String action = piece.toString() + " => " + move.toString();

                        child = new Node(root, (player == Piece.Black) ? Max : Min, hypotheticalBoard2, action,0,0);

                        root.addChild(child);
                    }
                }
    		}

            //Once we are done doing all the possible moves for each piece from this player, we change players
            //and do each possible move by the opponent on those moves by the player
            player = (player == Piece.Black)? Piece.Red : Piece.Black;
    		for(Node childNode : root.getChildren()){
    		    createTree1(depth+1, player, childNode);
    		}
    	}
    }
    
    public static Node abPruning(Node root, int alpha, int beta, boolean maxPlayer){
    	Node bestNode = root;
    	if(leaves.contains(root)){
    		return root;
    	}else{
    		if(maxPlayer){
    			//Node bestNode = root;
    			for(int i = root.childrenNum()-1; i >= 0; i--){
    				Node valNode = abPruning(root.child(i), alpha, beta, false); 
    				int bestValue = Math.max(bestNode.getValue(), valNode.getValue());
    				if(bestValue == valNode.getValue()){
    					bestNode = valNode;
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
    				Node valNode = abPruning(root.child(i), alpha, beta, true); 
    				int bestValue = Math.min(bestNode.getValue(), valNode.getValue());
    				if(bestValue == valNode.getValue()){
    					bestNode = valNode;
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
    
}
