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

        System.out.println(test.makeMove(Piece.Red, new Pair<>(0,5), new Pair<>(2,3)));
        System.out.println(test.makeMove(Piece.Red, new Pair<>(4,5), new Pair<>(4,3)));
        System.out.println(test.makeMove(Piece.Red, new Pair<>(3,6), new Pair<>(4,5)));
//        System.out.println(test.makeMove(Piece.Black, new Pair<>(3,2), new Pair<>(1,4)));
//        System.out.println(test.makeMove(Piece.Red, new Pair<>(2,5), new Pair<>(0,3)));
        test.printBoard();
        // Calculation initial piece values
        printArray(test.calcValues(Piece.Black));
        //test.getNormalMoves(3, 2, Piece.Black);
        ArrayList<Pair<Integer,Integer>> sequence = new ArrayList<>();
        getChainMoves(3, 2, Piece.Black, test.isPieceKing(3,2), sequence,1);
        for(int i = 0; i < tempMoves.size(); i++){
        	System.out.println(tempMoves.get(i).toString());
        }
        //test.getChainMoves(3, 2, Piece.Black, test, test.getValidJumps(3, 2, Piece.Black, true), test.getValidJumps(3, 2, Piece.Black, true).size(), moves, sequence);
    }

    static void getChainMoves(int x, int y, Piece player,boolean king, ArrayList<Pair<Integer,Integer>> sequence, int j){
        ArrayList<Pair<Integer,Integer>> extraJumps = test.getValidJumps(x,y, player, king, true);
        if(extraJumps.size() == 0){
            tempMoves.add(sequence);
            return;
        }
        for(int i = 0; i < extraJumps.size(); i++){
            ArrayList<Pair<Integer,Integer>> sequenceTemp = new ArrayList<>(sequence);
            sequenceTemp.add(new Pair<>(extraJumps.get(i).posX,extraJumps.get(i).posY));
            getChainMoves(extraJumps.get(i).posX, extraJumps.get(i).posY, player, king, sequenceTemp, j);
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
}
