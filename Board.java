
public class Board {
	
	char[][] boardState;
    int[][] boardValues;
    int size;
    int movesCount=0;
	
	public Board() {
		// TODO Auto-generated constructor stub
		boardState = new char[size][size];
        boardValues = new int[size][size];
        for(int y=0; y<size; y++){
            for(int x=0; x<size; x++){
                boardState[x][y] = ' ';
            }
        }
        this.size = size;
	}

}
