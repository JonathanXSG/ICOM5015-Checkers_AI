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
    
//    public  void getChainMoves(int x, int y, Piece player, Board board, ArrayList<Pair<Integer,Integer>> jumps, int pieces, ArrayList<ArrayList<Pair<Pair<Integer,Integer>, Pair<Integer,Integer>>>> moves, ArrayList<Pair<Pair<Integer,Integer>, Pair<Integer,Integer>>> sequence){
//    	if(pieces == 0){
//    		moves.add(sequence);
//    		//sequence.remove(sequence.size()-1);
//    		return;
//    	}else{
//    		//ArrayList<Pair<Integer,Integer>> jumps = getValidJumps(x, y, player, true);
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
//        		ArrayList<Pair<Integer,Integer>> extraJumps = getValidJumps(jumps.get(pieces-1).posX, jumps.get(pieces-1).posY, player, true);
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

    
    public ArrayList<Node> createTree(){
    	ArrayList<Node> children = new ArrayList<>(48);
        Node parent = new Node(children, null, 0, new Board(getBoardState()));
        ArrayList<Pair<Integer,Integer>> pieces = getAllPieceLocations(Piece.Black);
        Node child;
    	ArrayList<Node> tree = new ArrayList<>();
    	tree.add(parent);
    	
		return tree;
    }
    
    public void getChainMoves(int x, int y, Piece player,boolean king, ArrayList<Pair<Integer,Integer>> sequence, ArrayList<ArrayList<Pair<Integer,Integer>>> tempMoves){
        ArrayList<Pair<Integer,Integer>> extraJumps = getValidJumps(x,y, player, king, true);
        if(extraJumps.size() == 0){
            if(!sequence.isEmpty())
                tempMoves.add(sequence);
            return;
        }
        for(int i = 0; i < extraJumps.size(); i++){
            ArrayList<Pair<Integer,Integer>> sequenceTemp = new ArrayList<>(sequence);
            sequenceTemp.add(new Pair<>(extraJumps.get(i).posX,extraJumps.get(i).posY));
            getChainMoves(extraJumps.get(i).posX, extraJumps.get(i).posY, player, king, sequenceTemp, tempMoves);
        }
        return;
    }
    
    public void createTree1(int depth, ArrayList<Node> tree, Piece player, Board board, Node root){
    	int Min = -1000;
    	int Max = 1000;
    	if(depth == 3){
    		root.setValue(board.evaluationFunction(player));
    		tree.add(root);
    		return;
    	}else{
    		//Node root;
    		Node child = null;
    		//ArrayList<Node> children = new ArrayList<Node>();
    		ArrayList<Pair<Integer,Integer>> pieces = getAllPieceLocations(player);
    		ArrayList<Pair<Integer,Integer>> sequence = new ArrayList<>();
    		ArrayList<ArrayList<Pair<Integer,Integer>>>  tempMoves = new ArrayList<>();
    		Board hypotheticalBoard = new Board(getBoardState());
//    		if(depth == 0){
//    			root =  new Node(children, null, 0, hypotheticalBoard);
//    		}
    		for(int i =0; i < pieces.size(); i++){
    			ArrayList<Pair<Integer,Integer>> moves = getValidDiagonals(pieces.get(i).posX, pieces.get(i).posY, player);
    			ArrayList<Pair<Integer,Integer>> captures = getValidJumps(pieces.get(i).posX, pieces.get(i).posY, player, isPieceKing(pieces.get(i).posX, pieces.get(i).posY),true);
    			if(captures.size() > 0){
    				getChainMoves(pieces.get(i).posX, pieces.get(i).posY,player, isPieceKing(pieces.get(i).posX, pieces.get(i).posY), sequence, tempMoves);
    				for(int j = 0; j < sequence.size(); j++){
    					ArrayList<Node> children1 = new ArrayList<Node>();
    					hypotheticalBoard.makeMove(player, pieces.get(i),  sequence.get(i));
    					if(player == Piece.Black){
    						child = new Node(children1, root, Max,hypotheticalBoard);
    					}else{
    						child = new Node(children1, root, Min,hypotheticalBoard);
    					}
    					
    					root.addChild(child);;
    				}
    			}else{
    				for(int h = 0; h<moves.size(); h++){
    					ArrayList<Node> children1 = new ArrayList<Node>();
    					hypotheticalBoard.makeMove(player, pieces.get(h),  moves.get(h));
    					if(player == Piece.Black){
    						child = new Node(children1, root, Max,hypotheticalBoard);
    					}else{
    						child = new Node(children1, root, Min,hypotheticalBoard);
    					}
    					root.addChild(child);
    				}
    			}
    		}
    		tree.add(root);
			if(player == Piece.Black){
				player = Piece.Red;
			}else{
				player = Piece.Black;
			}
			for(int k = 0; k < root.childrenNum(); k++){
				createTree1(depth+1, tree, player, hypotheticalBoard, root.child(k));
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
    		
		return;
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

}