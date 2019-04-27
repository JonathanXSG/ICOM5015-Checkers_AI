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
        if(isInvalidCord(initialCord) || isInvalidCord(finalCord))
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

    private int calcBoardValue(Piece player){
        int value = 0;
        for(int x = 0; x<size; x++){
            for(int y = 0; y < size; y++){
                if(boardState[x][y] != ' ')
                    value += ((Character.toUpperCase(boardState[x][y]) == (player == Piece.Black? 'B' : 'R')) ? 1:-1)
                            * boardValues[x][y] * (Character.isUpperCase(boardState[x][y])? KING_VALUE: PIECE_VALUE) ;
            }
        }
        return value;
    }

    public int evaluationFunction(Piece player){
        int values = calcBoardValue(player);
        int offensiveJumps = 0;
        int defensiveJumps = 0;
        for(Pair<Integer,Integer> cord : getAllPieceLocations(player)){
            offensiveJumps += getValidJumps(cord.posX, cord.posY, player, true).size();
            defensiveJumps += getValidJumps(cord.posX, cord.posY, player, false).size();
        }
        offensiveJumps *= CAPTURE_VALUE;
        defensiveJumps *= -CAPTURE_VALUE;

        System.out.println("Points from Pieces values= "+values);
        System.out.println("Points from offensive Jumps= "+offensiveJumps);
        System.out.println("Points from defensive Jumps= "+defensiveJumps);

        return values+offensiveJumps+defensiveJumps;
    }

    /**
     *  Method for getting al locations of pieces of the corresponding player.
     * @param player Variable specifying the player to check for.
     * @return An array containing the coordinates of all the pieces
     */
    public ArrayList<Pair<Integer,Integer>> getAllPieceLocations(Piece player){
        ArrayList<Pair<Integer,Integer>> loactions = new ArrayList<>(12);

        int count = size*size,  maxCol = size-1,  minCol = 0, maxRow = size-1, minRow = 0;
        while (count > 0) {
            for (int i = minCol+1; i <= maxCol; i+=2)
                if(isPlayerInCord(minRow, i, player))
                    loactions.add(new Pair<>(minRow, i));
            for (int i = minRow+2; i <= maxRow; i+=2)
                if(isPlayerInCord(i, maxCol, player))
                    loactions.add(new Pair<>(i, maxCol));
            for (int i = maxCol-1; i >= minCol; i-=2)
                if(isPlayerInCord(maxRow, i, player))
                    loactions.add(new Pair<>(maxRow, i));
            for (int i = maxRow-2; i >= minRow+1; i-=2)
                if(isPlayerInCord(i, minCol, player))
                    loactions.add(new Pair<>(i, minCol));
            minCol++;
            minRow++;
            maxCol--;
            maxRow--;
            count--;
        }
        return loactions;
    }

    /**
     * Method finds all valid diagonal positions from the given coordinate
     * @param x X coordinate of the desired position to check
     * @param y Y coordinate of the desired position to check
     * @return array of 1-4 valid diagonal coordinates
     */
    public ArrayList<Pair<Integer,Integer>> getValidDiagonals(int x, int y){
        ArrayList<Pair<Integer,Integer>> diagonals = new ArrayList<>(4);
        if(!isInvalidCord(x+1, y+1))
            diagonals.add(new Pair<>(x+1, y+1));
        if(!isInvalidCord(x+1, y-1))
            diagonals.add(new Pair<>(x+1, y-1));
        if(!isInvalidCord(x-1, y+1))
            diagonals.add(new Pair<>(x-1, y+1));
        if(!isInvalidCord(x-1, y-1))
            diagonals.add(new Pair<>(x-1, y-1));

        return diagonals;
    }
    
    public ArrayList<Pair<Integer,Integer>> getValidDiagonals1(int x, int y, Piece player){
    	//Character.toUpperCase(board(initialCord)) != (player == Piece.Black? 'B' : 'R')
    	ArrayList<Pair<Integer,Integer>> diagonals = new ArrayList<>(4);
    	int modifier = (player == Piece.Black? 1:-1);
    	if(Character.isLowerCase(boardState[x][y]) && boardState[x][y] != ' ' && Character.toUpperCase(boardState[x][y]) == (player == Piece.Black? 'B' : 'R')){
    		 if(!isInvalidCord(x+modifier, y+modifier) && boardState[x+modifier][y+modifier] == ' ')
    	            diagonals.add(new Pair<>(x+modifier, y+modifier));
    		 if(!isInvalidCord(x-modifier, y+modifier) && boardState[x-modifier][y+modifier] == ' ')
    	            diagonals.add(new Pair<>(x-modifier, y+modifier));
    	}else if(boardState[x][y] != ' ' && Character.toLowerCase(boardState[x][y]) == (player == Piece.Black? 'b' : 'r')  ){
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
//    		ArrayList<Pair<Integer,Integer>> diagonals = getValidDiagonals1(pieces.get(i).posX, pieces.get(i).posY, player);
//    		ArrayList<Pair<Integer,Integer>> jumps = getValidJumps(pieces.get(i).posX, pieces.get(i).posY,player, true);
//    		if(jumps.size() != 0){
//    			diagonals.addAll(jumps);
//    		}
//    		move = new Pair<>(pieces.get(i), diagonals.get(i));
//    		moves.add(move);
//    	}

    	ArrayList<Pair<Integer,Integer>> diagonals = getValidDiagonals1(x, y, player);
    	//ArrayList<Pair<Integer,Integer>> jumps = getValidJumps1(x, y, player, true);
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
    
//    public  void getChainMoves(int x, int y, Piece player, Board board, ArrayList<Pair<Integer,Integer>> jumps, int pieces, ArrayList<ArrayList<Pair<Pair<Integer,Integer>, Pair<Integer,Integer>>>> moves, ArrayList<Pair<Pair<Integer,Integer>, Pair<Integer,Integer>>> sequence){
//    	if(pieces == 0){
//    		moves.add(sequence);
//    		//sequence.remove(sequence.size()-1);
//    		return;
//    	}else{
//    		//ArrayList<Pair<Integer,Integer>> jumps = getValidJumps1(x, y, player, true);
//    		//ArrayList<Pair<Pair<Integer,Integer>, Pair<Integer,Integer>>> sequence = new ArrayList<Pair<Pair<Integer,Integer>, Pair<Integer,Integer>>>();
//    		
//    		if(sequence.size() > 1){
//    			sequence.remove(sequence.size()-1);
//    		}
//
//    			Pair<Pair<Integer,Integer>, Pair<Integer,Integer>> move;
//        		Board hypotheticalBoard = board;
//        		Pair<Integer, Integer> initCord = new Pair<>(x,y);
//        		hypotheticalBoard.makeMove(player, initCord, jumps.get(pieces-1));
//        		ArrayList<Pair<Integer,Integer>> extraJumps = getValidJumps1(jumps.get(pieces-1).posX, jumps.get(pieces-1).posY, player, true);
//        		if(extraJumps.size() > 0){
//        			move = new Pair<Pair<Integer,Integer>, Pair<Integer,Integer>>(initCord,jumps.get(pieces-1));
//        			sequence.add(move);
//        			//surroudingPieces = extraJumps;
//        			getChainMoves(jumps.get(pieces-1).posX, jumps.get(pieces-1).posY, player, hypotheticalBoard, extraJumps, extraJumps.size(), moves, sequence);
//        		}else{
//        			move = new Pair<Pair<Integer,Integer>, Pair<Integer,Integer>>(initCord,jumps.get(pieces-1));
//        			sequence.add(move);
//        			getChainMoves(x, y, player, hypotheticalBoard, jumps, pieces-1, moves, sequence);
//        		}	
//    		
//    	}	
//    	System.out.println(moves.size());
//    	for(int j = 0; j < moves.size(); j++){
//    		System.out.println(moves.get(j).toString());
//    	}
//		return;
//    }
    
    public  void getChainMoves(int x, int y, Piece player, ArrayList<Pair<Pair<Integer,Integer>, Pair<Integer,Integer>>> sequence, ArrayList<ArrayList<Pair<Pair<Integer,Integer>, Pair<Integer,Integer>>>> moves, int j){
    	if(j == 0){
    		moves.add(sequence);
    		return;
    	}else{
    		Pair<Pair<Integer,Integer>, Pair<Integer,Integer>> move;
    		Pair<Integer, Integer> initCord = new Pair<>(x,y);
    		ArrayList<Pair<Integer,Integer>> extraJumps = getValidJumps1(x,y, player, true);
    		if(extraJumps.size() == 0){
    			getChainMoves(x, y,player,sequence,moves,0);
    		}
    		for(int i = 0; i < extraJumps.size(); i++){
    			move = new Pair<Pair<Integer,Integer>, Pair<Integer,Integer>>(initCord,extraJumps.get(i));
    			sequence.add(move);
    			getChainMoves(extraJumps.get(i).posX, extraJumps.get(i).posY,player,sequence,moves,j);
    		}
    	}
//    	System.out.println(moves.size());
//    	for(int k = 0; k < moves.size(); k++){
//    		System.out.println(moves.get(k).toString());
//    	}
		return;
    }
    
    
    public ArrayList<Node> createTree(){
    	ArrayList<Node> children = new ArrayList<>(48);
        Node parent = new Node(children, null, 0, new Board(getBoardState()));
        ArrayList<Pair<Integer,Integer>> pieces = getAllPieceLocations(Piece.Black);
        Node child;
    	ArrayList<Node> tree = new ArrayList<>();
    	tree.add(parent);
    	
		return tree;
    }
    
    public void createTree1(int depth, Node root, ArrayList<Node> tree, Piece player){
    	if(depth == 4){
    		return;
    	}else{
    		Node child;
    		ArrayList<Node> children = new ArrayList<Node>();
    		ArrayList<Pair<Integer,Integer>> pieces = getAllPieceLocations(player);
    		ArrayList<Pair<Pair<Integer,Integer>, Pair<Integer,Integer>>> moves;
    		for(int i = 0; i < pieces.size(); i++){
    			moves =  getNormalMoves(pieces.get(i).posX, pieces.get(i).posY, player);
    			for(int j = 0; j < moves.size(); j++){
    				makeMove(player, moves.get(j).posX, moves.get(j).posY);
    			}
//    			ArrayList<Pair<Integer,Integer>> moves = getValidDiagonals(pieces.get(i).posX, pieces.get(i).posY);
//    			ArrayList<Pair<Integer,Integer>> captures = getValidJumps(pieces.get(i).posX, pieces.get(i).posY, player, true);
//    			moves.addAll(captures);
//    			for(int j = 0; j < pieces.size(); j++){
//    				makeMove(player, pieces.get(i), moves.get(j));
//    				child = new Node(children, root, 0, new Board(getBoardState()));
//    				root.addChild(child);
//    			}
    		}
    		createTree1(depth+1, root, tree, player);
    	}
    	
		return;
    }
    
    

    /**
     * Method checks for all valid diagonal jump. It takes into consideration if the Piece is a King or not.
     * It is assumed the the coordinates given contain a piece of the player specified.
     * @param x X coordinate of the desired position to check
     * @param y Y coordinate of the desired position to check
     * @param player Variable specifying the current Player
     * @param offensive Boolean specifying if offensive or defensive jumps are to be considered.
     * @return If looking for offensive jumps, it will return an array of opponents that can be jumped.
     *          If looking for defensive humps, it will return an array of opponents that can jump over it.
     */
    public ArrayList<Pair<Integer,Integer>> getValidJumps(int x, int y, Piece player, boolean offensive){
        ArrayList<Pair<Integer,Integer>> jumps = new ArrayList<>(4);

        if(Character.isLowerCase(boardState[x][y])){
         /*
            The modifier is used to change de coordinates that will be checked.
            If the player is a Black Piece then the coordinates are increasing.
         */
            int modifier = (player == Piece.Black? 1:-1);
            if(offensive){
                if(!isInvalidCord(x+1, y+modifier) && !isInvalidCord(x+2, y+(modifier*2))
                        && boardState[x+1][y+modifier] != ' ' && boardState[x+2][y+(modifier*2)] == ' '
                        && iskOpponentInCord(x+1, y+modifier, player))
                    jumps.add(new Pair<>(x+1, y+modifier));
                if(!isInvalidCord(x-1, y+modifier) && !isInvalidCord(x-2, y+(modifier*2))
                        && boardState[x-1][y+modifier] != ' ' && boardState[x-2][y+(modifier*2)] == ' '
                        && iskOpponentInCord(x-1, y+modifier, player))
                    jumps.add(new Pair<>(x-1, y+modifier));
            }
            else{
                if(!isInvalidCord(x+1, y+modifier) && !isInvalidCord(x-1, y+(modifier*-1))
                        && boardState[x+1][y+modifier] != ' ' && boardState[x-1][y+(modifier*-1)] == ' '
                        && iskOpponentInCord(x+1, y+modifier, player))
                    jumps.add(new Pair<>(x+1, y+modifier));
                if(!isInvalidCord(x-1, y+modifier) && !isInvalidCord(x+1, y+(modifier*-1))
                        && boardState[x-1][y+modifier] != ' ' && boardState[x+1][y+(modifier*-1)] == ' '
                        && iskOpponentInCord(x-1, y+modifier, player))
                    jumps.add(new Pair<>(x-1, y+modifier));
                // These are if the other player's piece is King
                if(!isInvalidCord(x+1, y+(modifier*-1)) && isPieceKing(x+1, y+(modifier*-1))
                        && !isInvalidCord(x-1, y+modifier)
                        && boardState[x+1][y+(modifier*-1)] != ' ' && boardState[x-1][y+modifier] == ' '
                        && iskOpponentInCord(x+1, y+(modifier*-1), player))
                    jumps.add(new Pair<>(x+1, y+(modifier*-1)));
                if(!isInvalidCord(x-1, y+(modifier*-1)) && isPieceKing(x-1, y+(modifier*-1))
                        && !isInvalidCord(x+1, y+(modifier*-1))
                        && boardState[x-1][y+(modifier*-1)] != ' ' && boardState[x+1][y+modifier] == ' '
                        && iskOpponentInCord(x-1, y+(modifier*-1), player))
                    jumps.add(new Pair<>(x-1, y+(modifier*-1)));
            }
        }
        else{
            /*
            If the Piece is King then it can move in either direction.
             */
            if(offensive){
                if(!isInvalidCord(x+1, y+1) && !isInvalidCord(x+2, y+2)
                        && boardState[x+1][y+1] != ' ' && boardState[x+2][y+2] == ' '
                        && iskOpponentInCord(x+1, y+1, player))
                    jumps.add(new Pair<>(x+1, y+1));
                if(!isInvalidCord(x+1, y-1) && !isInvalidCord(x+2, y-2)
                        && boardState[x+1][y-1] != ' ' && boardState[x+2][y-2] == ' '
                        && iskOpponentInCord(x+1, y-1, player))
                    jumps.add(new Pair<>(x+1, y-1));
                if(!isInvalidCord(x-1, y+1) && !isInvalidCord(x-2, y+2)
                        && boardState[x-1][y+1] != ' ' && boardState[x-2][y+2] == ' '
                        && iskOpponentInCord(x-1, y+1, player))
                    jumps.add(new Pair<>(x-1, y+1));
                if(!isInvalidCord(x-1, y-1) && !isInvalidCord(x-2, y-2)
                        && boardState[x-1][y-1] != ' ' && boardState[x-2][y-2] == ' '
                        && iskOpponentInCord(x-1, y-1, player))
                    jumps.add(new Pair<>(x-1, y-1));
            }
            else{
                if(!isInvalidCord(x+1, y+1) && !isInvalidCord(x-1, y-1)
                        && boardState[x+1][y+1] != ' ' && boardState[x-1][y-1] == ' '
                        && iskOpponentInCord(x+1, y+1, player))
                    jumps.add(new Pair<>(x+1, y+1));
                if(!isInvalidCord(x-1, y-1) && !isInvalidCord(x+1, y+1)
                        && boardState[x-1][y-1] != ' ' && boardState[x+1][y+1] == ' '
                        && iskOpponentInCord(x-1, y-1, player))
                    jumps.add(new Pair<>(x-1, y-1));
                // These are if the other player's piece is King
                if(!isInvalidCord(x-1, y+1) && isPieceKing(x-1, y+1) && !isInvalidCord(x+1, y-1)
                        && boardState[x-1][y+1] != ' ' && boardState[x+1][y-1] == ' '
                        && iskOpponentInCord(x-1, y+1, player))
                    jumps.add(new Pair<>(x-1, y+1));
                if(!isInvalidCord(x+1, y-1) && isPieceKing(x+1, y-1) && !isInvalidCord(x-1, y+1)
                        && boardState[x+1][y-1] != ' ' && boardState[x-1][y+1] == ' '
                        && iskOpponentInCord(x+1, y-1, player))
                    jumps.add(new Pair<>(x+1, y-1));
            }
        }

        return jumps;
    }
    
    public ArrayList<Pair<Integer,Integer>> getValidJumps1(int x, int y, Piece player, boolean offensive){
        ArrayList<Pair<Integer,Integer>> jumps = new ArrayList<>(4);

        if(Character.isLowerCase(boardState[x][y])){
         /*
            The modifier is used to change de coordinates that will be checked.
            If the player is a Black Piece then the coordinates are increasing.
         */
            int modifier = (player == Piece.Black? 1:-1);
            if(offensive){
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
                if(!isInvalidCord(x+1, y+modifier) && !isInvalidCord(x-1, y+(modifier*-1))
                        && boardState[x+1][y+modifier] != ' ' && boardState[x-1][y+(modifier*-1)] == ' '
                        && iskOpponentInCord(x+1, y+modifier, player))
                    jumps.add(new Pair<>(x+1, y+modifier));
                if(!isInvalidCord(x-1, y+modifier) && !isInvalidCord(x+1, y+(modifier*-1))
                        && boardState[x-1][y+modifier] != ' ' && boardState[x+1][y+(modifier*-1)] == ' '
                        && iskOpponentInCord(x-1, y+modifier, player))
                    jumps.add(new Pair<>(x-1, y+modifier));
                // These are if the other player's piece is King
                if(!isInvalidCord(x+1, y+(modifier*-1)) && isPieceKing(x+1, y+(modifier*-1))
                        && !isInvalidCord(x-1, y+modifier)
                        && boardState[x+1][y+(modifier*-1)] != ' ' && boardState[x-1][y+modifier] == ' '
                        && iskOpponentInCord(x+1, y+(modifier*-1), player))
                    jumps.add(new Pair<>(x+1, y+(modifier*-1)));
                if(!isInvalidCord(x-1, y+(modifier*-1)) && isPieceKing(x-1, y+(modifier*-1))
                        && !isInvalidCord(x+1, y+(modifier*-1))
                        && boardState[x-1][y+(modifier*-1)] != ' ' && boardState[x+1][y+modifier] == ' '
                        && iskOpponentInCord(x-1, y+(modifier*-1), player))
                    jumps.add(new Pair<>(x-1, y+(modifier*-1)));
            }
        }
        else{
            /*
            If the Piece is King then it can move in either direction.
             */
            if(offensive){
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
            else{
                if(!isInvalidCord(x+1, y+1) && !isInvalidCord(x-1, y-1)
                        && boardState[x+1][y+1] != ' ' && boardState[x-1][y-1] == ' '
                        && iskOpponentInCord(x+1, y+1, player))
                    jumps.add(new Pair<>(x+1, y+1));
                if(!isInvalidCord(x-1, y-1) && !isInvalidCord(x+1, y+1)
                        && boardState[x-1][y-1] != ' ' && boardState[x+1][y+1] == ' '
                        && iskOpponentInCord(x-1, y-1, player))
                    jumps.add(new Pair<>(x-1, y-1));
                // These are if the other player's piece is King
                if(!isInvalidCord(x-1, y+1) && isPieceKing(x-1, y+1) && !isInvalidCord(x+1, y-1)
                        && boardState[x-1][y+1] != ' ' && boardState[x+1][y-1] == ' '
                        && iskOpponentInCord(x-1, y+1, player))
                    jumps.add(new Pair<>(x-1, y+1));
                if(!isInvalidCord(x+1, y-1) && isPieceKing(x+1, y-1) && !isInvalidCord(x-1, y+1)
                        && boardState[x+1][y-1] != ' ' && boardState[x-1][y+1] == ' '
                        && iskOpponentInCord(x+1, y-1, player))
                    jumps.add(new Pair<>(x+1, y-1));
            }
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

    private boolean isPieceKing(int x, int y){
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