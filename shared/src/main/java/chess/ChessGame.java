package chess;

import java.util.*;

import static chess.ChessPiece.PieceType.KING;
import static chess.ChessPiece.PieceType.ROOK;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board; //actually do I need to do something with this? How does it know what the board is?
    private ChessBoard testBoard;
    private boolean blackKingOrRook1Moved;
    private boolean blackKingOrRook2Moved;
    private boolean whiteKingOrRook1Moved;
    private boolean whiteKingOrRook2Moved;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return blackKingOrRook1Moved == chessGame.blackKingOrRook1Moved && blackKingOrRook2Moved == chessGame.blackKingOrRook2Moved && whiteKingOrRook1Moved == chessGame.whiteKingOrRook1Moved && whiteKingOrRook2Moved == chessGame.whiteKingOrRook2Moved && teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board) && Objects.equals(testBoard, chessGame.testBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board, testBoard, blackKingOrRook1Moved, blackKingOrRook2Moved, whiteKingOrRook1Moved, whiteKingOrRook2Moved);
    }

/*@Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board) && Objects.equals(testBoard, chessGame.testBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board, testBoard);
    }*/



    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
        blackKingOrRook1Moved = false;
        whiteKingOrRook1Moved = false;
        blackKingOrRook2Moved = false;
        whiteKingOrRook2Moved = false;
    }

    /**
     * @return Which teamTurn's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the teamTurn whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        Iterator<ChessMove> it = moves.iterator();
        while (it.hasNext()) {
            ChessMove move = it.next();
            ChessBoard testBoard = board.copy();
            testBoard.addPiece(move.getStartPosition(), null);
            testBoard.addPiece(move.getEndPosition(), piece);
            if (isInCheck(piece.getTeamColor(), testBoard)) {
                it.remove();
            }
        }

        if (piece.getPieceType().equals(KING)) {
            if (piece.getTeamColor().equals(TeamColor.WHITE)) {
                if (canCastle(piece.getTeamColor(), startPosition, whiteKingOrRook1Moved, 1)) {
                    moves.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow(), 3), null));
                }
                if (canCastle(piece.getTeamColor(), startPosition, whiteKingOrRook2Moved, 2)) {
                    moves.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow(), 7), null));
                }
            } else if (piece.getTeamColor().equals(TeamColor.BLACK)) {
                if (canCastle(piece.getTeamColor(), startPosition, blackKingOrRook1Moved, 1)) {
                    moves.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow(), 3), null));
                }
                if (canCastle(piece.getTeamColor(), startPosition, blackKingOrRook2Moved, 2)) {
                    moves.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow(), 7), null));
                }
            }
        }


        return moves;
    }

    
    //I think I can come back to this and simplify by saying !whiteKingOrRook1Moved or !blackKingOrRook1Moved bc they use the same logic
    //but first see if it works this way
    public boolean canCastle(TeamColor teamColor, ChessPosition start, Boolean castleMove, int side) {
        if (!castleMove && side == 1) {
            for (int col = 2; col <= 5; col++) {
                testBoard = board.copy();
                if (col != 5 && testBoard.getPiece(new ChessPosition(start.getRow(), col)) != null) {
                    return false;
                }
                testBoard.addPiece(start, null);
                testBoard.addPiece(new ChessPosition(start.getRow(), col), new ChessPiece(teamColor, KING));
                if (isInCheck(teamTurn, testBoard)) {
                    return false;
                }
            }
            return true;
        }

        if (!castleMove && side == 2) {
            for (int col = 5; col <= 7; col++) {
                testBoard = board.copy();
                if (col != 5 && testBoard.getPiece(new ChessPosition(start.getRow(), col)) != null) {
                    return false;
                }
                testBoard.addPiece(start, null);
                testBoard.addPiece(new ChessPosition(start.getRow(), col), new ChessPiece(teamColor, KING));
                if(isInCheck(teamTurn, testBoard)) {
                    return false;
                }
            }
            return true;
            }
        return false;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //maybe take a look at the errors again

        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);
        ChessPiece.PieceType promo = move.getPromotionPiece();


        if (piece == null) {
            throw new InvalidMoveException("No piece at start position");
        }

        if (isInCheck(teamTurn)) {
            throw new InvalidMoveException("That will put you in check!");
        }

        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Not your turn");
        }


        Collection<ChessMove> valid = validMoves(start);

        if (!valid.contains(move)) {
            throw new InvalidMoveException("Invalid move for piece");
        }

        if(promo != null){
            piece = new ChessPiece(teamTurn, promo);
        }


        if(piece.getPieceType() == KING) {
            if (end.getColumn() == 3 && piece.getTeamColor().equals(TeamColor.BLACK) && canCastle(piece.getTeamColor(), start, blackKingOrRook1Moved, 1)) {
                castle(start, end, piece, piece.getPieceType());
            } else if (end.getColumn() == 3 && piece.getTeamColor().equals(TeamColor.WHITE) && canCastle(piece.getTeamColor(), start, whiteKingOrRook1Moved, 1)) {
                castle(start, end, piece, piece.getPieceType());
            } else if (end.getColumn() == 7 && piece.getTeamColor().equals(TeamColor.BLACK) && canCastle(piece.getTeamColor(), start, blackKingOrRook1Moved, 1)) {
                castle(start, end, piece, piece.getPieceType());
            } else if (end.getColumn() == 7 && piece.getTeamColor().equals(TeamColor.WHITE) && canCastle(piece.getTeamColor(), start, whiteKingOrRook1Moved, 1)) {
                castle(start, end, piece, piece.getPieceType());
            } else {
                board.addPiece(start, null);
                board.addPiece(end, piece);
            }
        } else {
            board.addPiece(start, null);
            board.addPiece(end, piece);
        }

        checkIfMoved(piece, start);
        if (teamTurn.equals(TeamColor.WHITE)) {teamTurn = TeamColor.BLACK;}
        else if (teamTurn.equals(TeamColor.BLACK)) {teamTurn = TeamColor.WHITE;}
        setTeamTurn(teamTurn);

    }


    public void castle(ChessPosition start, ChessPosition end, ChessPiece piece, ChessPiece.PieceType type) {
        ChessPosition castleMove1 = null;
        ChessPosition castleMove2 = null;
        int row;
        boolean canCastle1;
        boolean canCastle2;
        if(teamTurn == TeamColor.WHITE) {
            row = 1;
            canCastle1 = whiteKingOrRook1Moved;
            canCastle2 = whiteKingOrRook2Moved;
            castleMove1 = new ChessPosition(1, 3);
            castleMove2 = new ChessPosition(1, 7);
        } else {
            row = 8;
            canCastle1 = blackKingOrRook1Moved;
            canCastle2 = blackKingOrRook2Moved;
            castleMove1 = new ChessPosition(8, 3);
            castleMove2 = new ChessPosition(8, 7);
            }
        if(!canCastle1 && end.equals(castleMove1)) {
            board.addPiece(new ChessPosition(row, 5), null);
            board.addPiece(new ChessPosition(row, 3), new ChessPiece(teamTurn, KING));
            board.addPiece(new ChessPosition(row, 1), null);
            board.addPiece(new ChessPosition(row, 4), new ChessPiece(teamTurn, ROOK));
        }

        if(!canCastle2 && end.equals(castleMove2)) {
            board.addPiece(new ChessPosition(row, 5), null);
            board.addPiece(new ChessPosition(row, 7), new ChessPiece(teamTurn, KING));
            board.addPiece(new ChessPosition(row, 8), null);
            board.addPiece(new ChessPosition(row, 6), new ChessPiece(teamTurn, ROOK));
        }
    }

    public void checkIfMoved(ChessPiece piece, ChessPosition start) {
        if(piece.getPieceType() == KING && teamTurn == TeamColor.WHITE) {
            whiteKingOrRook1Moved = true;
            whiteKingOrRook2Moved = true;
        }
        if(piece.getPieceType() == KING && teamTurn == TeamColor.BLACK) {
            blackKingOrRook1Moved = true;
            blackKingOrRook2Moved = true;
        }

        if(piece.getPieceType() == ROOK && start.equals(new ChessPosition(1, 1))){
            whiteKingOrRook1Moved = true;
        } else if (start.equals(new ChessPosition(1, 8))) {
            whiteKingOrRook2Moved = true;
        } else if (start.equals(new ChessPosition(8, 1))) {
            blackKingOrRook1Moved = true;
        } else if (start.equals(new ChessPosition(8, 8))) {
            blackKingOrRook2Moved = true;
        }
    }
    
    /**
     * @param teamColor which teamTurn to check for check
     * @return True if the specified teamTurn is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(teamColor, this.board);
    }

    public boolean isInCheck(TeamColor teamColor, ChessBoard boardToCheck) {
        ChessPosition kingPosition = findKing(teamColor, boardToCheck);
        TeamColor opposingColor = null;
        if (teamColor == TeamColor.WHITE) {opposingColor = TeamColor.BLACK;}
        else if (teamColor == TeamColor.BLACK) {opposingColor = TeamColor.WHITE;}
        Collection<PieceAndMove> opposingMoves = teamMoves(opposingColor, boardToCheck);
        for (PieceAndMove pam : opposingMoves) {
            ChessMove move = pam.getMove();
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
        return false;
    }


    /**
     * @param teamColor which teamTurn to check for checkmate
     * @return True if the specified teamTurn is in checkmate
     */

    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) { return false; }
        ChessPosition kingPosition = findKing(teamColor, board);
        ChessPiece king = board.getPiece(kingPosition);
        Collection<ChessMove> kingMoves = king.kingMoves(board, kingPosition);
        Collection<ChessMove> validKingMoves = king.kingMoves(board, kingPosition);

        TeamColor opposingColor = null;
        if (teamColor.equals(TeamColor.WHITE)) {opposingColor = TeamColor.BLACK;}
        else if (teamColor.equals(TeamColor.BLACK)) {opposingColor = TeamColor.WHITE;}

        for (ChessMove move : kingMoves) {
            testBoard = board.copy();
            testBoard.addPiece(kingPosition, null);
            testBoard.addPiece(move.getEndPosition(), new ChessPiece(teamColor, KING));
            if (isInCheck(teamColor, testBoard)) {
                validKingMoves.remove(move);
            }

        }
        if (validKingMoves.isEmpty()) {
            Collection<PieceAndMove> myMoves  = teamMoves(teamColor, board);
            for (PieceAndMove pam : myMoves) {
                testBoard = board.copy();
                ChessMove move = pam.getMove();
                ChessPiece piece = pam.getPiece();
                if (piece.getPieceType().equals(KING)) {
                    continue;
                }
                testBoard.addPiece(move.getStartPosition(), null);
                testBoard.addPiece(move.getEndPosition(), piece);
                if (!isInCheck(teamColor, testBoard)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    /**
     * Determines if the given teamTurn is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which teamTurn to check for stalemate
     * @return True if the specified teamTurn is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) return false;
        Collection<PieceAndMove> myMoves  = teamMoves(teamColor, board);
        for (PieceAndMove pam : myMoves) {
            testBoard = board.copy();
            ChessMove move = pam.getMove();
            ChessPiece piece = pam.getPiece();
            testBoard.addPiece(move.getStartPosition(), null);
            testBoard.addPiece(move.getEndPosition(), piece);
            if (!isInCheck(teamColor, testBoard)) {
                return false;
            }
        }
        return true;
    }

    public ChessPosition findKing(TeamColor teamColor, ChessBoard boardToCheck) {
        ChessPosition kingPosition = null;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = boardToCheck.getPiece(position);
                if (piece != null && piece.getPieceType() == KING && piece.getTeamColor() == teamColor) {
                    kingPosition = position;
                }
            }
        }
        return kingPosition;
    }

    public Collection<PieceAndMove> teamMoves(TeamColor teamColor, ChessBoard board) { //gets all the moves a teamTurn can make

        Collection<PieceAndMove> teamMoves = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    if (moves != null && !moves.isEmpty()) {
                        for (ChessMove move : moves) {
                            teamMoves.add(new PieceAndMove(piece, move));
                        }
                    }
                }
            }
        }
        return teamMoves;
    }
    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
