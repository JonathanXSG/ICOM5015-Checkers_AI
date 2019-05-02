//import java.awt.*;
//import java.awt.event.*;
//import java.util.*;
//import java.io.*;

import javax.swing.JFrame;

public class Main {
    private static Board initialBoard;

    public static void main(String[] args) {
    	
//    	JFrame myFrame = new JFrame("Checkers");
//		myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//myFrame.setSize(525, 550);
//    	Piece initialPiece = Piece.Black;
//    	BoardUI board = new BoardUI(initialBoard, initialPiece);
//    	for(int i = 0; i < initialBoard.getBoardState().length; i++){
//    		for(int j = 0; j < initialBoard.getBoardState().length; j++){
//    			if(initialBoard.getBoardState()[i][j] == 'r'){
//    				board.add(new Checker(Piece.Red), j, i);
//    			}else if(initialBoard.getBoardState()[i][j] == 'b'){
//    				board.add(new Checker(Piece.Black), j, i);
//    			}
//    		}
//    	}
//    	myFrame.setContentPane(board);
//    	myFrame.pack();
//		myFrame.setVisible(true);

        char[][] initial = {
                {' ','b',' ',' ',' ','r',' ','r'},
                {'b',' ','b',' ',' ',' ','r',' '},
                {' ','b',' ',' ',' ','r',' ','r'},
                {'b',' ','b',' ',' ',' ','r',' '},
                {' ','b',' ',' ',' ','r',' ','r'},
                {'b',' ','b',' ',' ',' ','r',' '},
                {' ','b',' ',' ',' ','r',' ','r'},
                {'b',' ','b',' ',' ',' ','r',' '},
        };
//        char[][] initial = {
//                {' ',' ',' ',' ',' ',' ',' ','b'},
//                {' ',' ',' ',' ',' ',' ','b',' '},
//                {' ',' ',' ',' ',' ','b',' ','b'},
//                {' ',' ',' ',' ',' ',' ','b',' '},
//                {' ',' ',' ',' ',' ','b',' ','b'},
//                {' ',' ','b',' ',' ',' ',' ',' '},
//                {' ','b',' ',' ',' ','b',' ','r'},
//                {'b',' ',' ',' ',' ',' ','b',' '},
//        };
        initialBoard = new Board(initial);

        long time1 = System.nanoTime();
        initialBoard.printBoard();

        Node root = new Node(null, -1000, initialBoard, null,0,0);
        EvaluationFunction evaluationFunction = new MediumEvaluation();
        EvaluationFunction evaluationFunction2 = new MediumEvaluation();
        AIPlayer aiPlayer =  new AIPlayer(Piece.Black,4, evaluationFunction);
        AIPlayer aiPlayer2 =  new AIPlayer(Piece.Red,3, evaluationFunction2);
        Node bestMove;
        int round = 0;
        boolean pass=true;

        while (!initialBoard.hasPlayerLost(Piece.Black) && !initialBoard.hasPlayerLost(Piece.Red)){
            // Black turn
            aiPlayer.calculateMove(root);
            bestMove = aiPlayer.getNextMove();
            pass=initialBoard.makeMove(Piece.Black, bestMove.getAction()[0], bestMove.getAction()[1]);
            System.out.println("BLACK "+round);
            initialBoard.printBoard();
            root = bestMove;

            if(initialBoard.hasPlayerLost(Piece.Red))
                break;
            //Red turn
            aiPlayer2.calculateMove(root);

            bestMove = aiPlayer2.getNextMove();
            pass=initialBoard.makeMove(Piece.Red, bestMove.getAction()[0], bestMove.getAction()[1]);
            System.out.println("RED " + round);
            initialBoard.printBoard();
            root = bestMove;
            round++;
        }
        long time2 = System.nanoTime();

        if(initialBoard.hasPlayerLost(Piece.Black))
            System.out.println("RED PLAYER WON");
        if(initialBoard.hasPlayerLost(Piece.Red))
            System.out.println("BLACK PLAYER WON");

//        root.print("",false);
        System.out.println("Time: "+ (time2-time1));
//        System.out.println(bestMove.getAction());

//        System.out.println(bestMove.getValue());
//        printArray(bestMove.getState().calcValues(Piece.Black));
//        evaluationFunction.evaluate(bestMove.getState(), Piece.Black, true);
//        System.out.println(bestMove.getState().evaluationFunction(Piece.Black));

    }

    private static void printArray(int[][] array){
        for(int y = 0; y<array.length; y++){
            for (int[] anArray : array) {
                System.out.printf("%3d ", anArray[y]);
            }
            System.out.println();
        }
    }
    
}
