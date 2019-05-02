//import java.awt.*;
//import java.awt.event.*;
//import java.util.*;
//import java.io.*;

public class Main {
    private static Board test = new Board(8);

    public static void main(String[] args) {
		//colaborator test push
//		JFrame myFrame = new JFrame("Checkers");
//		myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		myFrame.setLocation(400, 150);
//		myFrame.setSize(525, 550);
//		Checkerboard board = new Checkerboard();
//		myFrame.add(board);
//		myFrame.setVisible(true);

        long time1 = System.nanoTime();
        //test.printBoard();

        Node root = new Node(null, -1000, test, "Root",0,0);
        EvaluationFunction evaluationFunction = new MediumEvaluation();
        AIPlayer aiPlayer =  new AIPlayer(Piece.Black,7, evaluationFunction);
        Node bestMove=aiPlayer.calculateMove(root);

        long time2 = System.nanoTime();
        root.print("",false);
        System.out.println("Time: "+ (time2-time1));
        bestMove.getState().printBoard();
        System.out.println(bestMove.getAction());
        System.out.println(bestMove.getValue());
        printArray(bestMove.getState().calcValues(Piece.Black));
        evaluationFunction.evaluate(bestMove.getState(), Piece.Black, true);
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
