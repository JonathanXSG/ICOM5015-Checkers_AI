import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class Checkerboard extends JPanel {

	private static final long serialVersionUID = 1L;
	private Color square;
	private static final int SQUARE_SIZE = 50;
	private static final int ROW_COUNT = 8;
	private static final int COLUMN_COUNT = 8;
	private Color[][] colorArray = new Color[ROW_COUNT][COLUMN_COUNT];
	public Checkerboard() {
		// TODO Auto-generated constructor stub
		square = Color.black;
//		JPanel panel = new JPanel();
//		panel.setPreferredSize(new Dimension(50, 50));
		
		for(int i = 0; i < ROW_COUNT; i++){
			for(int j = 0; j < COLUMN_COUNT; j++){
				if((i+j)%2 == 0 ){
					square = Color.red;
				}else{
					square = Color.black;
				}
				colorArray[i][j] = square;
			}
			
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		for (int x = 0; x < ROW_COUNT; x++) {
			for (int y = 0; y < COLUMN_COUNT; y++) {
					Color c = colorArray[x][y];
					g.setColor(c);
					g.fillRect((x+1)*SQUARE_SIZE, (y+1)*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
			}
		}
	}

}
