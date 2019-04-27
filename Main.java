//import java.awt.*;
//import java.awt.event.*;
//import java.util.*;
//import java.io.*;
import javax.swing.*;

public class Main {
	public static void main(String[] args) {
		//colaborator test push
//		JFrame myFrame = new JFrame("Checkers");
//		myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		myFrame.setLocation(400, 150);
//		myFrame.setSize(525, 550);
//		Checkerboard board = new Checkerboard();
//		myFrame.add(board);
//		myFrame.setVisible(true);

		Board test = new Board(8);
        test.printBoard();
        // Fails because ther is no Piece here
        System.out.println(test.makeMove(Piece.Black, new Pair<>(0,0), new Pair<>(1,1)));
        // Fails because we can't move another player's Piece
        System.out.println(test.makeMove(Piece.Black, new Pair<>(0,7), new Pair<>(1,1)));
        // Successfully moving own Piece
        System.out.println(test.makeMove(Piece.Red, new Pair<>(0,7), new Pair<>(2,3)));
        // Showing how a King is made
        System.out.println(test.makeMove(Piece.Black, new Pair<>(0,1), new Pair<>(0,7)));
		System.out.println(test.makeMove(Piece.Red, new Pair<>(2,3), new Pair<>(2,3)));

		test.printBoard();
        // Calculation initial piece values
        printArray(test.calcValues(Piece.Black));

		System.out.println(test.evaluationFunction(Piece.Black));

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
