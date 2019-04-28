//import java.awt.*;
//import java.awt.event.*;
//import java.util.*;
//import java.io.*;
import java.util.ArrayList;

public class Main {
    private static Board test = new Board(8);
    private static ArrayList<ArrayList<Pair<Integer,Integer>>> tempMoves = new ArrayList<>();


    public static void main(String[] args) {
		//colaborator test push
//		JFrame myFrame = new JFrame("Checkers");
//		myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		myFrame.setLocation(400, 150);
//		myFrame.setSize(525, 550);
//		Checkerboard board = new Checkerboard();
//		myFrame.add(board);
//		myFrame.setVisible(true);


        test.printBoard();

//        System.out.println(test.makeMove(Piece.Red, new Pair<>(0,5), new Pair<>(2,3)));
//        System.out.println(test.makeMove(Piece.Red, new Pair<>(4,5), new Pair<>(4,3)));
//        System.out.println(test.makeMove(Piece.Red, new Pair<>(3,6), new Pair<>(4,5)));
//        System.out.println(test.makeMove(Piece.Black, new Pair<>(3,2), new Pair<>(1,4)));
//        System.out.println(test.makeMove(Piece.Red, new Pair<>(2,5), new Pair<>(0,3)));
        test.printBoard();
        // Calculation initial piece values
        printArray(test.calcValues(Piece.Black));
        //test.getNormalMoves(3, 2, Piece.Black);
        ArrayList<Pair<Integer,Integer>> sequence = new ArrayList<>();
//        getChainMoves(3, 2, Piece.Black, test.isPieceKing(3,2), sequence,1);
        ArrayList<Node> tree = new ArrayList<Node>();
        ArrayList<Node> children = new ArrayList<Node>();
        Board test1 = new Board(8);
        Node root = new Node(children, null, 0, test);
        createTree1(0, tree, Piece.Black, test1,root);
        System.out.println(tree.size());
//        for(int j = 0; j < tree.size(); j++){
//        	tree.get(j).getState().printBoard();
//        }
        ArrayList<Pair<Integer,Integer>> pieces =  test.getAllPieceLocations(Piece.Black);
        System.out.println("All moves from Black player");
        for(int i = 0; i < pieces.size(); i++){

            test.getValidDiagonals(pieces.get(i).posX, pieces.get(i).posY, Piece.Black).forEach((p) ->
                    tempMoves.add(new ArrayList<Pair<Integer, Integer>>(){{{add(p);}}}));

            getChainMoves(pieces.get(i).posX, pieces.get(i).posY, Piece.Black,
                    test.isPieceKing(pieces.get(i).posX, pieces.get(i).posY), sequence);
        }
        System.out.println(tempMoves.size());

        for(int i = 0; i < tempMoves.size(); i++){
        	System.out.println(tempMoves.get(i).toString());
        }
        //test.getChainMoves(3, 2, Piece.Black, test, test.getValidJumps(3, 2, Piece.Black, true), test.getValidJumps(3, 2, Piece.Black, true).size(), moves, sequence);
    }

    static void getChainMoves(int x, int y, Piece player,boolean king, ArrayList<Pair<Integer,Integer>> sequence){
        ArrayList<Pair<Integer,Integer>> extraJumps = test.getValidJumps(x,y, player, king, true);
        if(extraJumps.size() == 0){
            if(!sequence.isEmpty())
                tempMoves.add(sequence);
            return;
        }
        for(int i = 0; i < extraJumps.size(); i++){
            ArrayList<Pair<Integer,Integer>> sequenceTemp = new ArrayList<>(sequence);
            sequenceTemp.add(new Pair<>(extraJumps.get(i).posX,extraJumps.get(i).posY));
            getChainMoves(extraJumps.get(i).posX, extraJumps.get(i).posY, player, king, sequenceTemp);
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
    
    public static void createTree1(int depth, ArrayList<Node> tree, Piece player, Board board, Node root){
    	int Min = -1000;
    	int Max = 1000;
    	if(depth == 3){
    		root.setValue(board.evaluationFunction(player));
    		tree.add(root);
    		return;
    	}else{
    		//Node root;
    		Node child = null;
    		//ArrayList<Node> children = new ArrayList<Node>();
    		Board hypotheticalBoard = new Board(board.getBoardState());
    		ArrayList<Pair<Integer,Integer>> pieces = board.getAllPieceLocations(player);
    		boolean onlyKills = false;
    		ArrayList<Pair<Integer,Integer>> sequence = new ArrayList<>();
    		//ArrayList<ArrayList<Pair<Integer,Integer>>>  tempMoves = new ArrayList<>();
//    		if(depth == 0){
//    			root =  new Node(children, null, 0, hypotheticalBoard);
//    		}
    		
    		for(int l = 0; l < pieces.size(); l++){
    			ArrayList<Pair<Integer,Integer>> verifyCaptures = hypotheticalBoard.getValidJumps(pieces.get(l).posX, pieces.get(l).posY, player, hypotheticalBoard.isPieceKing(pieces.get(l).posX, pieces.get(l).posY),true);
    			if(verifyCaptures.size() > 0){
    				onlyKills = true;
    				break;
    			}	
    		}
    		
    		if(onlyKills){
    			for(int i = 0; i < pieces.size();i++){
    				getChainMoves(pieces.get(i).posX, pieces.get(i).posY,player, hypotheticalBoard.isPieceKing(pieces.get(i).posX, pieces.get(i).posY), sequence);
    				for(int j = 0; j < tempMoves.size(); j++){
    					ArrayList<Node> children1 = new ArrayList<Node>();
						Board hypotheticalBoard1 = new Board(hypotheticalBoard.getBoardState());
    					for(int m = 0; m < tempMoves.get(j).size();m++){
    						hypotheticalBoard1.makeMove(player, pieces.get(i),tempMoves.get(j).get(m));	
    					}
						if(player == Piece.Black){
							child = new Node(children1, root, Max,hypotheticalBoard1);
							//hypotheticalBoard.makeMove(player, sequence.get(j), pieces.get(i));
						}else{
							child = new Node(children1, root, Min,hypotheticalBoard1);
							//hypotheticalBoard.makeMove(player, sequence.get(j), pieces.get(i));
						}
						root.addChild(child);
	    			}
    			}	
    		}else{
    			for(int i = 0; i < pieces.size();i++){
    				ArrayList<Pair<Integer,Integer>> moves = hypotheticalBoard.getValidDiagonals(pieces.get(i).posX, pieces.get(i).posY, player);
    				for(int h = 0; h<moves.size(); h++){
    					ArrayList<Node> children1 = new ArrayList<Node>();
    					Board hypotheticalBoard2 = new Board(hypotheticalBoard.getBoardState());
						hypotheticalBoard2.makeMove(player, pieces.get(i),  moves.get(h));		
						if(player == Piece.Black){
							child = new Node(children1, root, Max,hypotheticalBoard2);
							//hypotheticalBoard.makeMove(player, moves.get(h), pieces.get(i));
						}else{
							child = new Node(children1, root, Min,hypotheticalBoard2);
							//hypotheticalBoard.makeMove(player, moves.get(h), pieces.get(i));
						}
						root.addChild(child);
					}
    			}
    		}
    		
//    		for(int i =0; i < pieces.size(); i++){
//    			ArrayList<Pair<Integer,Integer>> moves = getValidDiagonals(pieces.get(i).posX, pieces.get(i).posY, player);
//    			ArrayList<Pair<Integer,Integer>> captures = getValidJumps(pieces.get(i).posX, pieces.get(i).posY, player, isPieceKing(pieces.get(i).posX, pieces.get(i).posY),true);
//    			if(captures.size() > 0){
//    				getChainMoves(pieces.get(i).posX, pieces.get(i).posY,player, isPieceKing(pieces.get(i).posX, pieces.get(i).posY), sequence, tempMoves);
//    				for(int j = 0; j < sequence.size(); j++){
//    					ArrayList<Node> children1 = new ArrayList<Node>();
//    					hypotheticalBoard.makeMove(player, pieces.get(i),  sequence.get(i));
//    					if(player == Piece.Black){
//    						child = new Node(children1, root, Max,hypotheticalBoard);
//    						hypotheticalBoard.makeMove(player, sequence.get(i), pieces.get(i));
//    					}else{
//    						child = new Node(children1, root, Min,hypotheticalBoard);
//    						hypotheticalBoard.makeMove(player, sequence.get(i), pieces.get(i));
//    					}
//    					
//    					root.addChild(child);
//    				}
//    			}else{
//    				for(int h = 0; h<moves.size(); h++){
//    					ArrayList<Node> children1 = new ArrayList<Node>();
//    					hypotheticalBoard.makeMove(player, pieces.get(h),  moves.get(h));
//    					if(player == Piece.Black){
//    						child = new Node(children1, root, Max,hypotheticalBoard);
//    						hypotheticalBoard.makeMove(player, moves.get(h), pieces.get(h));
//    					}else{
//    						child = new Node(children1, root, Min,hypotheticalBoard);
//    						hypotheticalBoard.makeMove(player, moves.get(h), pieces.get(h));
//    					}
//    					root.addChild(child);
//    				}
//    			}
//    		}
    		tree.add(root);
			if(player == Piece.Black){
				player = Piece.Red;
			}else{
				player = Piece.Black;
			}
			for(int k = 0; k < root.childrenNum(); k++){
				createTree1(depth+1, tree, player, root.child(k).getState(), root.child(k));
			}
//    			ArrayList<Pair<Integer,Integer>> moves = getValidDiagonals(pieces.get(i).posX, pieces.get(i).posY);
//    			ArrayList<Pair<Integer,Integer>> captures = getValidJumps(pieces.get(i).posX, pieces.get(i).posY, player, true);
//    			moves.addAll(captures);
//    			for(int j = 0; j < pieces.size(); j++){
//    				makeMove(player, pieces.get(i), moves.get(j));
//    				child = new Node(children, root, 0, new Board(getBoardState()));
//    				root.addChild(child);
//    			}
    		}
    		
		return;
    }
    
}
