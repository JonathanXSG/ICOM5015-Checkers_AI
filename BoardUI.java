import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

public class BoardUI extends JComponent
{
   /**
	 * 
	 */
	private static final long serialVersionUID = -977823110304009296L;

// dimension of checkerboard square (25% bigger than checker)

   
private final static int SQUAREDIM = (int) (Checker.getDimension() * 1.25);

   // dimension of checkerboard (width of 8 squares)

   private final int BOARDDIM = 8 * SQUAREDIM;

   // preferred size of Board component

   private Dimension dimPrefSize;

   // dragging flag -- set to true when user presses mouse button over checker
   // and cleared to false when user releases mouse button

   private boolean inDrag = false;

   // displacement between drag start coordinates and checker center coordinates

   private int deltax, deltay;

   // reference to positioned checker at start of drag

   private PosCheck posCheck;

   // center location of checker at start of drag

   private int oldcx, oldcy;

   // list of Checker objects and their initial positions

   private List<PosCheck> posChecks;
   
   private Board gameBoard;
   
   private Piece player;

   public BoardUI(Board initialBoard, Piece initialPlayer)
   {
      posChecks = new ArrayList<>();
      dimPrefSize = new Dimension(BOARDDIM, BOARDDIM);
      gameBoard = initialBoard;
      player = initialPlayer;

      addMouseListener(new MouseAdapter()
                       {
                          @Override
                          public void mousePressed(MouseEvent me)
                          {
                             // Obtain mouse coordinates at time of press.

                             int x = me.getX();
                             int y = me.getY();

                             // Locate positioned checker under mouse press.

                             for (PosCheck posCheck: posChecks)
                                if (Checker.contains(x, y, posCheck.cx, 
                                                     posCheck.cy))
                                {
                                   BoardUI.this.posCheck = posCheck;
                                   oldcx = posCheck.cx;
                                   oldcy = posCheck.cy;
                                   deltax = x - posCheck.cx;
                                   deltay = y - posCheck.cy;
                                   inDrag = true;
                                   return;
                                }
                          }

                          @Override
                          public void mouseReleased(MouseEvent me)
                          {
                             // When mouse released, clear inDrag (to
                             // indicate no drag in progress) if inDrag is
                             // already set.

                             if (inDrag)
                                inDrag = false;
                             else
                                return;

                             // Snap checker to center of square.

                             int x = me.getX();
                             int y = me.getY();
                             posCheck.cx = (x - deltax) / SQUAREDIM * SQUAREDIM + 
                                           SQUAREDIM / 2;
                             posCheck.cy = (y - deltay) / SQUAREDIM * SQUAREDIM + 
                                           SQUAREDIM / 2;

                             // Do not move checker onto an occupied square.

                             for (PosCheck posCheck: posChecks)
                                if (posCheck != BoardUI.this.posCheck && 
                                    posCheck.cx == BoardUI.this.posCheck.cx &&
                                    posCheck.cy == BoardUI.this.posCheck.cy)
                                {
                                   BoardUI.this.posCheck.cx = oldcx;
                                   BoardUI.this.posCheck.cy = oldcy;
                                }
                                	
                             boolean valid = gameBoard.makeMove(player, new Pair<Integer, Integer>((oldcx)/SQUAREDIM, oldcy/SQUAREDIM), new Pair<Integer, Integer>((x)/SQUAREDIM, (y)/SQUAREDIM));
                             if(!valid){
                            	posCheck.cx = oldcx;
                            	posCheck.cy = oldcy;
                             }else{
                            	 changePlayer();
                            	 update();
//                            	 posCheck.cx = posCheck.cx;
//                            	 posCheck.cy = posCheck.cy;
                             }
//                             update();

                             posCheck = null;
                             repaint();
                          }
                       });

      // Attach a mouse motion listener to the applet. That listener listens
      // for mouse drag events.

      addMouseMotionListener(new MouseMotionAdapter()
                             {
                                @Override
                                public void mouseDragged(MouseEvent me)
                                {
                                   if (inDrag)
                                   {
                                      // Update location of checker center.

                                      posCheck.cx = me.getX() - deltax;
                                      posCheck.cy = me.getY() - deltay;
                                      repaint();
                                   }
                                }
                             });

   }

   public void add(Checker checker, int row, int col)
   {
      if (row < 0 || row > 7)
         throw new IllegalArgumentException("row out of range: " + row);
      if (col < 0 || col > 7)
         throw new IllegalArgumentException("col out of range: " + col);
      PosCheck posCheck = new PosCheck();
      posCheck.checker = checker;
      posCheck.cx = (col) * SQUAREDIM + SQUAREDIM / 2;
      posCheck.cy = (row) * SQUAREDIM + SQUAREDIM / 2;
//      for (PosCheck _posCheck: posChecks)
//         if (posCheck.cx == _posCheck.cx && posCheck.cy == _posCheck.cy)
//            throw new AlreadyOccupiedException("square at (" + row + "," +
//                                               col + ") is occupied");
      posChecks.add(posCheck);
   }

   @Override
   public Dimension getPreferredSize()
   {
      return dimPrefSize;
   }

   @Override
   protected void paintComponent(Graphics g)
   {
      paintCheckerBoard(g);
      
      for (PosCheck posCheck: posChecks){
//    	  if(gameBoard.getBoardState()[posCheck.cx/SQUAREDIM][posCheck.cy/SQUAREDIM] == ' '){
//    		  posChecks.remove(posCheck);
//    	  }
    	  if (posCheck != BoardUI.this.posCheck){
    		  posCheck.checker.draw(g, posCheck.cx, posCheck.cy);  
    	  }
      }
    	 

      // Draw dragged checker last so that it appears over any underlying 
      // checker.

      if (posCheck != null)
         posCheck.checker.draw(g, posCheck.cx, posCheck.cy);
      
  
   }

   private void paintCheckerBoard(Graphics g)
   {
      ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);

      // Paint checkerboard.

      for (int row = 0; row < 8; row++)
      {
         g.setColor(((row & 1) != 0) ? Color.BLACK : Color.WHITE);
         for (int col = 0; col < 8; col++)
         {
            g.fillRect(col * SQUAREDIM, row * SQUAREDIM, SQUAREDIM, SQUAREDIM);
            g.setColor((g.getColor() == Color.BLACK) ? Color.WHITE : Color.BLACK);
         }
      }
      
   }

   // positioned checker helper class

   private class PosCheck
   {
      public Checker checker;
      public int cx;
      public int cy;
   }
   
	private void changePlayer(){
		if(player == Piece.Black){
			player = Piece.Red;
		}else if(player == Piece.Red){
			player = Piece.Black;
		}
		   
	}
	
	private void update(){
		for(int i = 0; i < posChecks.size(); i++){
			if(gameBoard.getBoardState()[posChecks.get(i).cx/SQUAREDIM][posChecks.get(i).cy/SQUAREDIM] == ' '){
				posChecks.remove(i);
			}
		}
//		for (PosCheck posCheck: posChecks){
//			if(gameBoard.getBoardState()[posCheck.cx/SQUAREDIM][posCheck.cy/SQUAREDIM] == ' '){
//   			 posChecks.remove(posCheck);
//			}
//   	 	}
	}
	
	
   
   
}