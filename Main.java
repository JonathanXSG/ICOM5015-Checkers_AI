//import java.awt.*;
//import java.awt.event.*;
//import java.util.*;
//import java.io.*;

public class Main {
    private static Board initialBoard;

    public static void main(String[] args) {
		//colaborator test push
//		JFrame myFrame = new JFrame("Checkers");
//		myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		myFrame.setLocation(400, 150);
//		myFrame.setSize(525, 550);
//		Checkerboard board = new Checkerboard();
//		myFrame.add(board);
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
//                {' ',' ',' ',' ',' ',' ',' ','r'},
//                {' ',' ',' ',' ',' ',' ','r',' '},
//                {' ',' ',' ',' ',' ','r',' ','r'},
//                {' ',' ',' ',' ',' ',' ','r',' '},
//                {' ',' ',' ',' ',' ','r',' ','r'},
//                {' ',' ','r',' ',' ',' ','r',' '},
//                {' ','r',' ',' ',' ','r',' ','r'},
//                {'b',' ',' ',' ',' ',' ','r',' '},
//        };
        initialBoard = new Board(initial);

//        long time1 = System.nanoTime();
        initialBoard.printBoard();

        Node root = new Node(null, -1000, initialBoard, null,0,0);
        EvaluationFunction evaluationFunction = new MediumEvaluation();
        AIPlayer aiPlayer =  new AIPlayer(Piece.Black,3, evaluationFunction);
        AIPlayer aiPlayer2 =  new AIPlayer(Piece.Red,3, evaluationFunction);

        aiPlayer.calculateMove(root);
        root.print("", false);
        Node bestMove = aiPlayer.getNextMove();
//        long time2 = System.nanoTime();


        initialBoard.makeMove(Piece.Black, bestMove.getAction()[0], bestMove.getAction()[1]);
        initialBoard.printBoard();

        aiPlayer2.calculateMove(root);
        bestMove = aiPlayer2.getNextMove();

        initialBoard.makeMove(Piece.Red, bestMove.getAction()[0], bestMove.getAction()[1]);
        initialBoard.printBoard();

        aiPlayer.calculateMove(root);
        bestMove = aiPlayer.getNextMove();

        initialBoard.makeMove(Piece.Black, bestMove.getAction()[0], bestMove.getAction()[1]);
        initialBoard.printBoard();

        aiPlayer2.calculateMove(root);
        bestMove = aiPlayer2.getNextMove();

        initialBoard.makeMove(Piece.Red, bestMove.getAction()[0], bestMove.getAction()[1]);
        initialBoard.printBoard();

//        root.print("",false);
//        System.out.println("Time: "+ (time2-time1));
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
