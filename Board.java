
public class Board {

    private char[][] boardState;
    private int[][] boardValues;
    private int size;
    private static final int PIECE_VALUE = 2;
    private static final int KING_VALUE = 5;
    private static final int CAPTURE_VALUE = 10;
	
	public Board(int size) {
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

    public Board(char[][] initialState) {
        boardState = initialState;
        boardValues = new int[initialState.length][initialState.length];
        this.size = initialState.length;
        setBoardValues();
    }

	private void setBoardValues(){
		int value = 4,  minCol = 0, maxCol = size-1, minRow = 0, maxRow = size-1;
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
    }

	private void setPieces(){
        for(int y=0; y<size; y++){
            for(int x=0; x<size; x++){
                if(y%2==0 ){
                    if(x%2==1 && y == 0)
                        boardState[x][y] = 'b';
                    else if(x%2==1 && y == size-2)
                        boardState[x][y] = 'r';
                }
                else if(y%2==1){
                    if(x%2==0 && y == size-1)
                        boardState[x][y] = 'r';
                    else if(x%2==0 && y == 1)
                        boardState[x][y] = 'b';
                }
                else
                    boardState[x][y] = ' ';
            }
        }
//		for(int i = 0; i<size; i++){
//			for(int j = 0; j < 3; j++){
//				if((i+j)%2 != 0){
//					boardState[i][j] = 'R';
//					boardState[i][size-1-j] = 'B';
//				}
//			}
//		}
	}

	//TODO: Implement checking if a move is valid in terms of diagonal move or jumping over piece
    public boolean makeMove(Piece player, Pair<Integer,Integer> initialCord, Pair<Integer,Integer> finalCord){
        if(checkInvalidCord(initialCord) || checkInvalidCord(finalCord))
            return false;
	    else if(Character.toUpperCase(board(initialCord)) != (player == Piece.Black? 'B' : 'R'))
	        return false;
        else if(board(finalCord) != ' ')
            return false;
        else{
            if(finalCord.posY == size-1 || finalCord.posY == 0)
                board(finalCord, Character.toUpperCase(board(initialCord)));
            else
                board(finalCord, board(initialCord));
            System.out.println();
            board(initialCord, ' ');
            return true;
        }
    }

    public int[][] calcValues(Piece player){
	    int[][] values = new int[size][size];
	    for(int x = 0; x<size; x++){
			for(int y = 0; y < size; y++){
                if(boardState[x][y] == ' ')
                    values[x][y] = 0;
                else
                    values[x][y] = ((Character.toUpperCase(boardState[x][y]) == (player == Piece.Black? 'B' : 'R')) ? 1:-1)
                            * boardValues[x][y] * (Character.isUpperCase(boardState[x][y])? KING_VALUE: PIECE_VALUE) ;
                }
		}
		return values;
    }

    private boolean checkInvalidCord(Pair<Integer,Integer> cord) {
        return cord.posX >= size || cord.posY >= size;
    }

    private char board(Pair<Integer,Integer> cord){
	    return boardState[cord.posX][cord.posY];
    }

    private void board(Pair<Integer,Integer> cord, char value){
        boardState[cord.posX][cord.posY] = value;
    }

    public char[][] getBoardState() {
        return boardState;
    }

    public int getSize() {
        return size;
    }

    public void printBoard(){
        for(int y=0; y<size; y++){
            for(int x=0; x<size; x++){
                StringBuilder buffer = new StringBuilder();
                if(boardState[x][y] == 'B' || boardState[x][y] == 'b')
                    buffer.append(ConsoleColors.RESET).append(ConsoleColors.PURPLE + " ")
                            .append(boardState[x][y]).append(" ");
                else if(boardState[x][y] == 'R' || boardState[x][y] == 'r')
                    buffer.append(ConsoleColors.RESET).append(ConsoleColors.RED + " ")
                            .append(boardState[x][y]).append(" ");
                else if((x+y)%2 != 0)
                    buffer.append(ConsoleColors.RESET).append(ConsoleColors.WHITE).append("   ");
                else
                    buffer.append(ConsoleColors.RED_BACKGROUND).append("   ");
                buffer.append(ConsoleColors.RESET);
                System.out.print(buffer.toString());
            }
            System.out.println();
        }
    }

}
