
public class Board {
	
	char[][] boardState;
    int[][] boardValues;
    int size;
    //int movesCount=0;
    private static final int PIECE_VALUE = 2;
    private static final int CAPTURE_VALUE = 5;
	
	public Board(int size) {
		// TODO Auto-generated constructor stub
		boardState = new char[size][size];
        boardValues = new int[size][size];
        for(int y=0; y<size; y++){
            for(int x=0; x<size; x++){
                boardState[x][y] = ' ';
            }
        }
        this.size = size;
        setBoardValues();
        setPieces();
	}
	
	private void setBoardValues(){
		int value = 4;
		int minCol = 0;
        int maxCol = size-1;
        int minRow = 0;
        int maxRow = size-1; 
		while(value > 0){
			for(int y=minCol; y<=maxCol; y++)
				if((minRow+y)%2 != 0)
					boardValues[minRow][y] = value;
			for(int x=minRow+1; x<=maxRow; x++)
				if((x+maxCol)%2 != 0)
					boardValues[x][maxCol] = value;
			for(int y=maxCol-1; y>=minCol; y--)
				if((maxRow+y)%2 != 0)
					boardValues[maxRow][y] = value;
			for(int x=maxRow-1; x>=minRow; x--)
				if((minCol+x)%2 != 0)
					boardValues[x][minCol] = value;
			minCol++;
            minRow++;
            maxCol--;
            maxRow--;
			value--;
		}
//        for(int y=0; y<size; y++)
//            for(int x=0; x<size; x++)
//            	if((x+y)%2 != 0)
//                boardValues[x][y] = 1;
        
//        for(int i = 0; i < size; i++){
//        	if(i%2 != 0){
//        		boardValues[0][i] = 4;
//            	boardValues[i][0] = 4;
//            	boardValues[size - 1 - i][size-1] = 4;
//        		boardValues[size-1][size - 1 - i] = 4;	
//        	}
//        	
//        }
        
        for(int y=0; y<size; y++){
        	for(int x=0; x<size; x++){
        		System.out.print(boardValues[x][y] + " ");
        	}
        	System.out.println();
            	
        }
        System.out.println();   
        

//        for(int i=0; i<size; i++){
//            boardValues[i][i]++;
//            boardValues[(size-1-i)][i]++;
//        }
//        boardValues[(size/2)][(size/2)]++;

    }
	
	private void setPieces(){
		for(int i = 0; i<size; i++){
			for(int j = 0; j < 3; j++){
				if((i+j)%2 != 0){
					boardState[i][j] = 'r';
					boardState[i][size-1-j] = 'b';
				}
			}
		}
		for(int y=0; y<size; y++){
        	for(int x=0; x<size; x++){
        		System.out.print(boardState[x][y] + " ");
        	}
        	System.out.println();
            	
        }
        System.out.println();   
	}

}
