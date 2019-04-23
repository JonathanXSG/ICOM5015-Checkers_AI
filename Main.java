//import java.awt.*;
//import java.awt.event.*;
//import java.util.*;
//import java.io.*;
import javax.swing.*;

public class Main {
	public static void main(String[] args) {
		//colaborator test push
		JFrame myFrame = new JFrame("Checkers");
		myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		myFrame.setLocation(400, 150);
		myFrame.setSize(525, 550);
		Checkerboard board = new Checkerboard();
		myFrame.add(board);
		myFrame.setVisible(true);
	}
}
