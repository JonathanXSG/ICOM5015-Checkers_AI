import java.util.ArrayList;

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
    	boardState = new char[initialState.length][initialState.length];
    	for(int y=0; y<initialState.length; y++){
            for(int x=0; x<initialState.length; x++){
                boardState[x][y] = initialState[x][y];
            }
        }
        //boardState = initialState;
        boardValues = new int[initialState.length][initialState.length];
        this.size = initialState.length;
        setBoardValues();
    }

    public Board(Board board) {
        this.size = board.boardState.length;
        boardState = new char[size][size];
        boardValues = new int[size][size];
        for(int y=0; y<size; y++){
            for(int x=0; x<size; x++){
                boardState[x][y] = board.boardState[x][y];
                boardValues[x][y] = board.boardValues[x][y];
            }
        }
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
                    if(x%2==1 && (y == 0 || y == 2))
                        boardState[x][y] = 'b';
                    else if(x%2==1 && y == size-2)
                        boardState[x][y] = 'r';
                }
                else if(y%2==1){
                    if(x%2==0 && (y == size-1 || y == size-3))
                        boardState[x][y] = 'r';
                    else if(x%2==0 && y == 1)
                        boardState[x][y] = 'b';
                }
                else
                    boardState[x][y] = ' ';
            }
        }
	}

	//TODO: Implement checking if a move is valid in terms of diagonal move or jumping over piece
    public boolean makeMove(Piece player, Pair<Integer,Integer> initialCord, Pair<Integer,Integer> finalCord){
	    boolean king = isPieceKing(initialCord.posX, initialCord.posY);
        if(isInvalidCord(initialCord) || isInvalidCord(finalCord))
            return false;
	    else if(Character.toUpperCase(board(initialCord)) != (player == Piece.Black? 'B' : 'R'))
	        return false;
        else if(board(finalCord) != ' ')
            return false;
        else if(!getValidDiagonals(initialCord.posX, initialCord.posY, player).contains(finalCord) && !getValidJumps(initialCord.posX, initialCord.posY, player, king,true).contains(finalCord)) {
            return false;
        }
        else if(!king && player == Piece.Black && finalCord.posY < initialCord.posY){
            return false;
        }
        else if(!king && player == Piece.Red && finalCord.posY > initialCord.posY){
            return false;
        }
        else{
            if(getValidJumps(initialCord.posX, initialCord.posY, player, king, true).contains(finalCord)){
                //Jump over opponent
                board(new Pair<>((finalCord.posX+initialCord.posX)/2,(finalCord.posY+initialCord.posY)/2), ' ');
            }
            if(finalCord.posY == size-1 || finalCord.posY == 0)
                board(finalCord, Character.toUpperCase(board(initialCord)));
            else
                board(finalCord, board(initialCord));
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
                            * boardValues[x][y] * (isPieceKing(x,y)? KING_VALUE: PIECE_VALUE) ;
                }
		}
		return values;
    }

    private int calcBoardValue(Piece player){
        int value = 0;
        for(int x = 0; x<size; x++){
            for(int y = 0; y < size; y++){
                if(boardState[x][y] != ' ')
                    value += ((Character.toUpperCase(boardState[x][y]) == (player == Piece.Black? 'B' : 'R')) ? 1:-1)
                            * boardValues[x][y] * (isPieceKing(x,y)? KING_VALUE: PIECE_VALUE) ;
            }
        }
        return value;
    }

    public int evaluationFunction(Piece player){
        int values = calcBoardValue(player);
        int offensiveJumps = 0;
        int defensiveJumps = 0;
        for(Pair<Integer,Integer> cord : getAllPieceLocations(player)){
            offensiveJumps += getValidJumps(cord.posX, cord.posY, player,
                    isPieceKing(cord.posX, cord.posY),true).size();
            defensiveJumps += getValidJumps(cord.posX, cord.posY, player,
                    isPieceKing(cord.posX, cord.posY),false).size();
        }
        offensiveJumps *= CAPTURE_VALUE;
        defensiveJumps *= -CAPTURE_VALUE;

        return values+offensiveJumps+defensiveJumps;
    }

    /**
     *  Method for getting al locations of pieces of the corresponding player.
     * @param player Variable specifying the player to check for.
     * @return An array containing the coordinates of all the pieces
     */
    public ArrayList<Pair<Integer,Integer>> getAllPieceLocations(Piece player){
        ArrayList<Pair<Integer,Integer>> locations = new ArrayList<>(12);

        int count = size*size,  maxCol = size-1,  minCol = 0, maxRow = size-1, minRow = 0;
        while (count > 0) {
            for (int i = minCol+1; i <= maxCol; i+=2)
                if(isPlayerInCord(minRow, i, player))
                    locations.add(new Pair<>(minRow, i));
            for (int i = minRow+2; i <= maxRow; i+=2)
                if(isPlayerInCord(i, maxCol, player))
                    locations.add(new Pair<>(i, maxCol));
            for (int i = maxCol-1; i >= minCol; i-=2)
                if(isPlayerInCord(maxRow, i, player))
                    locations.add(new Pair<>(maxRow, i));
            for (int i = maxRow-2; i >= minRow+1; i-=2)
                if(isPlayerInCord(i, minCol, player))
                    locations.add(new Pair<>(i, minCol));
            minCol++;
            minRow++;
            maxCol--;
            maxRow--;
            count--;
        }
        return locations;
    }

    /**
     * Method finds all valid diagonal positions from the given coordinate
     * @param x X coordinate of the desired position to check
     * @param y Y coordinate of the desired position to check
     * @param player
     * @return array of 1-4 valid diagonal coordinates
     */
    public ArrayList<Pair<Integer,Integer>> getValidDiagonals(int x, int y, Piece player){
    	ArrayList<Pair<Integer,Integer>> diagonals = new ArrayList<>(4);
    	int modifier = (player == Piece.Black? 1:-1);
    	if(!isPieceKing(x,y) && boardState[x][y] != ' ' && (boardState[x][y] == (player == Piece.Black? 'b' : 'r'))){
    		 if(!isInvalidCord(x+modifier, y+modifier) && boardState[x+modifier][y+modifier] == ' ')
    	            diagonals.add(new Pair<>(x+modifier, y+modifier));
    		 if(!isInvalidCord(x-modifier, y+modifier) && boardState[x-modifier][y+modifier] == ' ')
    	            diagonals.add(new Pair<>(x-modifier, y+modifier));
    	}else if(boardState[x][y] != ' ' && (boardState[x][y] == (player == Piece.Black? 'b' : 'r'))){
    		if(!isInvalidCord(x+modifier, y+modifier)  && boardState[x+modifier][y+modifier] == ' ')
                diagonals.add(new Pair<>(x+modifier, y+modifier));
            if(!isInvalidCord(x+modifier, y-modifier)  && boardState[x+modifier][y-modifier] == ' ')
                diagonals.add(new Pair<>(x+modifier, y-modifier));
            if(!isInvalidCord(x-modifier, y+modifier)  && boardState[x-modifier][y+modifier] == ' ')
                diagonals.add(new Pair<>(x-modifier, y+modifier));
            if(!isInvalidCord(x-modifier, y-modifier)  && boardState[x-modifier][y-modifier] == ' ')
                diagonals.add(new Pair<>(x-modifier, y-modifier));
    	}

        return diagonals;
    }
    
    public ArrayList<Pair<Pair<Integer,Integer>, Pair<Integer,Integer>>> getNormalMoves(int x, int y, Piece player){
    	//ArrayList<ArrayList<Pair<Pair<Integer,Integer>, Pair<Integer,Integer>>>> moves = new ArrayList<ArrayList<Pair<Pair<Integer,Integer>, Pair<Integer,Integer>>>>();
    	ArrayList<Pair<Pair<Integer,Integer>, Pair<Integer,Integer>>> moves = new ArrayList<Pair<Pair<Integer,Integer>, Pair<Integer,Integer>>>();
    	Pair<Pair<Integer,Integer>, Pair<Integer,Integer>> move;
    	Pair<Integer, Integer> initCord = new Pair<>(x,y);
//    	Board hypotheticalBoard = board;
//    	ArrayList<Pair<Integer,Integer>> pieces = getAllPieceLocations(player);
//    	for(int i = 0; i < pieces.size();i++){
//    		ArrayList<Pair<Integer,Integer>> diagonals = getValidDiagonals(pieces.get(i).posX, pieces.get(i).posY, player);
//    		ArrayList<Pair<Integer,Integer>> jumps = getValidJumps(pieces.get(i).posX, pieces.get(i).posY,player, true);
//    		if(jumps.size() != 0){
//    			diagonals.addAll(jumps);
//    		}
//    		move = new Pair<>(pieces.get(i), diagonals.get(i));
//    		moves.add(move);
//    	}

    	ArrayList<Pair<Integer,Integer>> diagonals = getValidDiagonals(x, y, player);
    	//ArrayList<Pair<Integer,Integer>> jumps = getValidJumps(x, y, player, true);
    	System.out.println(diagonals.size());
    	//System.out.println(jumps.size());
//    	if(jumps.size() > 0){
//    		for(int j = 0; j < jumps.size(); j++){
//    			move = new Pair<>(initCord, jumps.get(j));
//    			moves.add(move);
//    		}
//    		
//    	}else{
    		for(int i = 0; i < diagonals.size(); i++){
        		move = new Pair<>(initCord, diagonals.get(i));
        		moves.add(move);
        	}
//    	}
    	
    	
    	System.out.println(moves.size());
    	for(int j = 0; j < moves.size(); j++){
    		System.out.println(moves.get(j).toString());
    	}
    	return moves;
    	
    }

    /**
     * Method checks for all valid diagonal jump. It takes into consideration if the Piece is a King or not.
     * It is assumed the the coordinates given contain a piece of the player specified.
     * @param x X coordinate of the desired position to check
     * @param y Y coordinate of the desired position to check
     * @param player Variable specifying the current Player
     * @param offensive Boolean specifying if offensive or defensive jumps are to be considered.
     * @return If looking for offensive jumps, it will return an array of cords after jump.
     *          If looking for defensive humps, it will return an array of cords after opponents jump.
     */
    public ArrayList<Pair<Integer,Integer>> getValidJumps(int x, int y, Piece player, boolean king, boolean offensive){
        ArrayList<Pair<Integer,Integer>> jumps = new ArrayList<>(4);

         /*
            The modifier is used to change de coordinates that will be checked.
            If the player is a Black Piece then the coordinates are increasing.
         */
        int modifier = (player == Piece.Black? 1:-1);
        if(offensive){
            if(!king){
                if(!isInvalidCord(x+1, y+modifier) && !isInvalidCord(x+2, y+(modifier*2))
                        && boardState[x+1][y+modifier] != ' ' && boardState[x+2][y+(modifier*2)] == ' '
                        && iskOpponentInCord(x+1, y+modifier, player))
                jumps.add(new Pair<>(x+2, y+(modifier*2)));
                if(!isInvalidCord(x-1, y+modifier) && !isInvalidCord(x-2, y+(modifier*2))
                        && boardState[x-1][y+modifier] != ' ' && boardState[x-2][y+(modifier*2)] == ' '
                        && iskOpponentInCord(x-1, y+modifier, player))
                    jumps.add(new Pair<>(x-2, y+(modifier*2)));
            }
            else{
            /*
            If the Piece is King then it can move in either direction.
             */
                if(!isInvalidCord(x+1, y+1) && !isInvalidCord(x+2, y+2)
                        && boardState[x+1][y+1] != ' ' && boardState[x+2][y+2] == ' '
                        && iskOpponentInCord(x+1, y+1, player))
                    jumps.add(new Pair<>(x+2, y+2));
                if(!isInvalidCord(x+1, y-1) && !isInvalidCord(x+2, y-2)
                        && boardState[x+1][y-1] != ' ' && boardState[x+2][y-2] == ' '
                        && iskOpponentInCord(x+1, y-1, player))
                    jumps.add(new Pair<>(x+2, y-2));
                if(!isInvalidCord(x-1, y+1) && !isInvalidCord(x-2, y+2)
                        && boardState[x-1][y+1] != ' ' && boardState[x-2][y+2] == ' '
                        && iskOpponentInCord(x-1, y+1, player))
                    jumps.add(new Pair<>(x-2, y+2));
                if(!isInvalidCord(x-1, y-1) && !isInvalidCord(x-2, y-2)
                        && boardState[x-1][y-1] != ' ' && boardState[x-2][y-2] == ' '
                        && iskOpponentInCord(x-1, y-1, player))
                    jumps.add(new Pair<>(x-2, y-2));
            }

        }
        else{
            if(!isInvalidCord(x+1, y+modifier) && !isInvalidCord(x-1, y+(modifier*-1))
                    && boardState[x+1][y+modifier] != ' ' && boardState[x-1][y+(modifier*-1)] == ' '
                    && iskOpponentInCord(x+1, y+modifier, player))
                jumps.add(new Pair<>(x-1, y+(modifier*-1)));
            if(!isInvalidCord(x-1, y+modifier) && !isInvalidCord(x+1, y+(modifier*-1))
                    && boardState[x-1][y+modifier] != ' ' && boardState[x+1][y+(modifier*-1)] == ' '
                    && iskOpponentInCord(x-1, y+modifier, player))
                jumps.add(new Pair<>(x+1, y+(modifier*-1)));
            // These are if the other player's piece is King
            if(!isInvalidCord(x+1, y+(modifier*-1)) && !isInvalidCord(x-1, y+modifier)
                    && isPieceKing(x+1, y+(modifier*-1))
                    && boardState[x+1][y+(modifier*-1)] != ' ' && boardState[x-1][y+modifier] == ' '
                    && iskOpponentInCord(x+1, y+(modifier*-1), player))
                jumps.add(new Pair<>(x-1, y+modifier));
            if(!isInvalidCord(x-1, y+(modifier*-1)) && !isInvalidCord(x+1, y+modifier)
                    && isPieceKing(x-1, y+(modifier*-1))
                    && boardState[x-1][y+(modifier*-1)] != ' ' && boardState[x+1][y+modifier] == ' '
                    && iskOpponentInCord(x-1, y+(modifier*-1), player))
                jumps.add(new Pair<>(x+1, y+modifier));
        }

        return jumps;
    }

    private boolean isInvalidCord(Pair<Integer,Integer> cord) {
        return isInvalidCord(cord.posX, cord.posY);
    }
    private boolean isInvalidCord(int x, int y) {
        return x >= size || y >= size || x < 0 || y < 0;
    }

    private boolean iskOpponentInCord(int x, int y, Piece player) {
        return (Character.toUpperCase(boardState[x][y]) != (player == Piece.Black? 'B' : 'R'));
    }

    private boolean isPlayerInCord(int x, int y, Piece player) {
        return (Character.toUpperCase(boardState[x][y]) == (player == Piece.Black? 'B' : 'R'));
    }

    public boolean isPieceKing(int x, int y){
        return Character.isUpperCase(boardState[x][y]);
    }

    public void makeKing(int x, int y){
        boardState[x][y] = Character.toUpperCase(boardState[x][y]);
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
            System.out.print(ConsoleColors.YELLOW_BOLD + y +" ");
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
            if(y==7) System.out.println(ConsoleColors.YELLOW_BOLD+"   0  1  2  3  4  5  6  7"+ConsoleColors.RESET);
        }
    }

    /**
     * Method to check if a given player has lost the game
     * @param player Player to evaluate if it has any remaining pieces or moves
     * @return True if the player has lost or false if the player has remaining pieces or moves
     */
    public boolean hasPlayerLost(Piece player) {
        ArrayList<Pair<Integer, Integer>> playerPieces = getAllPieceLocations(player);
        if(playerPieces.isEmpty()){
            return true;
        }
        else{
            for (Pair<Integer, Integer> piece: playerPieces){
                if (!getValidDiagonals(piece.posX, piece.posY, player).isEmpty() || !getValidJumps(piece.posX, piece.posY , player, isPieceKing(piece.posX, piece.posY), true).isEmpty()){
                    return false;
                }
            }
            return true;
        }
    }

}