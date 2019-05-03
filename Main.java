//import java.awt.*;
//import java.awt.event.*;
//import java.util.*;
//import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
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

        AIvsAI(initial);
//        AIvsPlayer();

    }

    private static void printArray(int[][] array){
        for(int y = 0; y<array.length; y++){
            for (int[] anArray : array) {
                System.out.printf("%3d ", anArray[y]);
            }
            System.out.println();
        }
    }

    private static void AIvsAI(char[][] initial){
        initialBoard = new Board(initial);
        long time1 = System.nanoTime();
        initialBoard.printBoard();

        Node root = new Node(null, -1000, initialBoard, null,0,0);
        EvaluationFunction evaluationFunction = new MediumEvaluation();
        EvaluationFunction evaluationFunction2 = new MediumEvaluation();
        AIPlayer aiPlayer =  new AIPlayer(Piece.Black,7, evaluationFunction);
        AIPlayer aiPlayer2 =  new AIPlayer(Piece.Red,7, evaluationFunction2);
        Node bestMove;
        int round = 0;
        boolean pass=true;

        while (!initialBoard.hasPlayerLost(Piece.Black) && !initialBoard.hasPlayerLost(Piece.Red)){
            // Black turn
            aiPlayer.calculateMove(root);
            bestMove = aiPlayer.getNextMove();
            System.out.println("BLACK "+round);
            for(int index=0; index < bestMove.getAction().size()-1; index++){
                pass=initialBoard.makeMove(Piece.Black, bestMove.getAction().get(index), bestMove.getAction().get(index+1));
            }
            if (!pass){
                initialBoard.printBoard();
                for(int index=0; index < bestMove.getAction().size()-1; index++){
                    System.out.println(bestMove.getAction().get(index) + " => " + bestMove.getAction().get(index+1));
                }
                System.out.println(ConsoleColors.RED_BOLD+"Oh no Invalid move by Black on round "+round+ConsoleColors.RESET);
            }
            root = new Node(null, -1000, initialBoard, null,0,0);

            if(initialBoard.hasPlayerLost(Piece.Red))
                break;

            //Red turn
            aiPlayer2.calculateMove(root);
            bestMove = aiPlayer2.getNextMove();
            System.out.println("RED " + round);
            for(int index=0; index < bestMove.getAction().size()-1; index++){
                pass=initialBoard.makeMove(Piece.Red, bestMove.getAction().get(index), bestMove.getAction().get(index+1));
            }
            if (!pass){
                initialBoard.printBoard();
                for(int index=0; index < bestMove.getAction().size()-1; index++){
                    System.out.println(bestMove.getAction().get(index) + " => " + bestMove.getAction().get(index+1));
                }
                System.out.println(ConsoleColors.RED_BOLD+"Oh no Invalid move by RED on round "+round+ConsoleColors.RESET);
            }
            root = new Node(null, -1000, initialBoard, null,0,0);

            round++;
        }
        long time2 = System.nanoTime();

        if(initialBoard.hasPlayerLost(Piece.Black))
            System.out.println("RED PLAYER WON");
        if(initialBoard.hasPlayerLost(Piece.Red))
            System.out.println("BLACK PLAYER WON");
        initialBoard.printBoard();
        System.out.println("Time: "+ (time2-time1));
    }
    
    private static void AIvsPlayer(){
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
        	if(Character.isDigit(initialCord.charAt(0)) && Character.isDigit(initialCord.charAt(2)) &&
                    initialCord.length() == 3 && initialCord.charAt(1) == ',' ){
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
        	if(Character.isDigit(initialCord.charAt(0)) && Character.isDigit(initialCord.charAt(2)) &&
                    finalCord.length() == 3 && finalCord.charAt(1) == ','){
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
    	if(initialBoard.makeMove(Piece.Red, firstCord, secondCord)){
    		System.out.println("Succesfully moved the piece");
    	}else{
    		System.out.println("Error moving the piece");
    	}
    }
}
