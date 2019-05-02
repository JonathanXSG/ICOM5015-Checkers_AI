
public class MediumEvaluation implements EvaluationFunction {

    private int calcBoardValue(Board state, Piece player){
        int value = 0;
        char[][] board = state.getBoardState();
        int[][] values = state.getBoardValues();

        for(int x = 0; x<state.getSize(); x++){
            for(int y = 0; y < state.getSize(); y++){
                if(board[x][y] != ' ')
                    value += ((Character.toUpperCase(board[x][y]) == (player == Piece.Black? 'B' : 'R')) ? 1:-1)
                            * (values[x][y] * (state.isPieceKing(x,y)?
                            CheckersValues.KING_VALUE: CheckersValues.PIECE_VALUE)) ;
            }
        }
        return value;
    }

    @Override
    public int evaluate(Board state, Piece player, boolean debug) {
        int values = calcBoardValue(state, player);
        int playerPieces = 0;
        int opponentPieces = 0;
        int offensiveJumps = 0;
        int defensiveJumps = 0;
        for(Pair<Integer,Integer> cord : state.getAllPieceLocations(player)){
            offensiveJumps += state.getValidJumps(cord.posX, cord.posY, player,
                    state.isPieceKing(cord.posX, cord.posY),true).size();
            defensiveJumps += state.getValidJumps(cord.posX, cord.posY, player,
                    state.isPieceKing(cord.posX, cord.posY),false).size();
        }
        offensiveJumps *= CheckersValues.CAPTURE_VALUE;
        defensiveJumps *= -CheckersValues.CAPTURE_VALUE;
        if((defensiveJumps-offensiveJumps)>0)values*=2;
        if(debug){
            System.out.println("playerPieces "+playerPieces);
            System.out.println("opponentPieces "+opponentPieces);
            System.out.println("defensiveJumps "+defensiveJumps);
            System.out.println("offensiveJumps "+offensiveJumps);
            System.out.println("defensiveJumps - offensiveJumps "+(defensiveJumps-offensiveJumps));
            System.out.println("values "+values);
            System.out.println("total "+(values+offensiveJumps+defensiveJumps));
        }

        return values+offensiveJumps+defensiveJumps+opponentPieces-playerPieces;
    }

}
