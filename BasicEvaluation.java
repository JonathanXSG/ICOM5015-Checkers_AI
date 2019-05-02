public class BasicEvaluation implements EvaluationFunction {

    private int calcBoardValue(Board state, Piece player){
        int value = 0;
        char[][] board = state.getBoardState();
        int[][] values = state.getBoardValues();

        for(int x = 0; x<state.getSize(); x++){
            for(int y = 0; y < state.getSize(); y++){
                if(board[x][y] != ' ')
                    value += ((Character.toUpperCase(board[x][y]) == (player == Piece.Black? 'B' : 'R')) ? 1:-1)
                            * (values[x][y] + (state.isPieceKing(x,y)?
                            CheckersValues.KING_VALUE: CheckersValues.PIECE_VALUE)) ;
            }
        }
        return value;
    }

    @Override
    public int evaluate(Board state, Piece player, boolean debug) {
        int values = calcBoardValue(state, player);

        if(debug){
            System.out.println("values "+values);
            System.out.println("total "+(values));
        }

        return values;
    }
}
