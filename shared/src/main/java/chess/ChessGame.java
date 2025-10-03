package chess;

import java.util.*;

import static chess.ChessPiece.PieceType.KING;
import static chess.ChessPiece.PieceType.PAWN;

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

    @Override
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
    }


    //need to make the equals and hashcode

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
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
        return piece.pieceMoves(board, startPosition);
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition, ChessBoard board) {
        ChessPiece piece = board.getPiece(startPosition);
        return piece.pieceMoves(board, startPosition);
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


        if (piece == null) {
            throw new InvalidMoveException("No piece at start position");
        }

        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Not your turn");
        }

        Collection<ChessMove> valid = piece.pieceMoves(board, start);
        if (!valid.contains(move)) {
            throw new InvalidMoveException("Invalid move for piece");
        }


        board.addPiece(start, null);
        board.addPiece(end, piece);

        setTeamTurn(teamTurn);

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
        Map<ChessPiece, Collection<ChessMove>> opposingMoves  = teamMoves(opposingColor, boardToCheck);
        List<ChessMove> allMoves = new ArrayList<>();
        for (Collection<ChessMove> moves : opposingMoves.values()) {
            allMoves.addAll(moves);
        }
        for (ChessMove move : allMoves) {
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
        return false;


        /*ChessPosition kingPos = findKing(teamColor, boardToCheck);
        Collection<ChessPosition> opponentMoves = teamMoves(getOppositeColor(teamColor), boardToCheck);

        // if any opponent move hits the king’s square, you’re in check
        return opponentMoves.contains(kingPos);*/
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
        testBoard = board.copy();
        for (ChessMove move : kingMoves) {
            testBoard.addPiece(kingPosition, null);
            testBoard.addPiece(move.getEndPosition(), new ChessPiece(teamColor, KING));
            if (isInCheck(teamColor, testBoard)) {
                validKingMoves.remove(move);
            }
            testBoard = board.copy();
        }
        if (validKingMoves.isEmpty()) {
            Map<ChessPiece, Collection<ChessMove>> myMoves  = teamMoves(teamColor, board);
            for (Map.Entry<ChessPiece, Collection<ChessMove>> entry : myMoves.entrySet()) {
                ChessPiece piece = entry.getKey();
                Collection<ChessMove> moves = entry.getValue();
                if (piece.getPieceType().equals(ChessPiece.PieceType.KING)) {
                    continue;
                }
                for (ChessMove move : moves) {
                    testBoard.addPiece(move.getStartPosition(), null);
                    testBoard.addPiece(move.getEndPosition(), piece);
                    if (isInCheck(teamColor, testBoard)) {
                        testBoard = board.copy();
                    } else {
                        testBoard = board.copy();
                        return false;
                    }
                }
            }
        }
        return true;
    }





    /**
     * Determines if the given teamTurn is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which teamTurn to check for stalemate
     * @return True if the specified teamTurn is in stalemate, otherwise false
     */
    public boolean isInStalemate(ChessGame.TeamColor teamColor) {
        if (!isInCheckmate(teamColor)) {
            Map<ChessPiece, Collection<ChessMove>> myMoves  = teamMoves(teamColor, board);

            testBoard = board.copy();
            for (Map.Entry<ChessPiece, Collection<ChessMove>> entry : myMoves.entrySet()) {
                ChessPiece piece = entry.getKey();
                Collection<ChessMove> moves = entry.getValue();

                for (ChessMove move : moves) {
                    testBoard.addPiece(move.getStartPosition(), null);
                    testBoard.addPiece(move.getEndPosition(), piece);
                    if (isInCheck(teamColor, testBoard)) {
                        testBoard = board.copy();
                        return true;
                    } else {
                        testBoard = board.copy();
                    }
                }
            }
        }
        return false;
    }

    public ChessPosition findKing(ChessGame.TeamColor teamColor, ChessBoard boardToCheck) {
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

    public Map<ChessPiece, Collection<ChessMove>> teamMoves(ChessGame.TeamColor teamColor, ChessBoard board) { //gets all the moves a teamTurn can make
        //Collection<ChessPosition> teamMoves = new ArrayList<>();
        Map<ChessPiece, Collection<ChessMove>> teamMoves = new HashMap<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> validMoves = validMoves(position, board);
                    if (validMoves != null && !validMoves.isEmpty()) {
                        teamMoves.put(piece, validMoves);
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
