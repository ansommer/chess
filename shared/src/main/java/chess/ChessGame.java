package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessPiece.PieceType.KING;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor team;
    private ChessBoard board; //actually do I need to do something with this? How does it know what the board is?

    //need to make the equals and hashcode

    public ChessGame() {
        team = TeamColor.WHITE;
        board = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
        if (team == TeamColor.WHITE) {
            team = TeamColor.BLACK;
        } else if (team == TeamColor.BLACK) {
            team = TeamColor.WHITE;
        }
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

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //fix it so it will throw the error

        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);

        if (piece == null) {
            throw new InvalidMoveException("No piece at start position");
        }
        if (piece.getTeamColor() != team) {
            throw new InvalidMoveException("Not your turn");
        }

        Collection<ChessMove> valid = piece.pieceMoves(board, start);
        if (!valid.contains(move)) {
            throw new InvalidMoveException("Invalid move for piece");
        }


        board.addPiece(start, null);
        board.addPiece(end, piece);

        setTeamTurn(team);

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);
        TeamColor opposingColor = null;
        if (teamColor == TeamColor.WHITE) {opposingColor = TeamColor.BLACK;}
        else if (teamColor == TeamColor.BLACK) {opposingColor = TeamColor.WHITE;}
        Collection<Collection<ChessMove>> opposingMoves = teamMoves(opposingColor);
        for (Collection<ChessMove> movesSet : opposingMoves) {
            for (ChessMove move : movesSet) {
                if (move.getEndPosition() == kingPosition) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            ChessPosition kingPosition = findKing(teamColor);
            ChessPiece king = board.getPiece(kingPosition);
            Collection<ChessMove> kingMoves = king.pieceMoves(board, kingPosition);
            for  (ChessMove x : kingMoves) {
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        ChessPosition position = new ChessPosition(i, j);
                        ChessPiece piece = board.getPiece(position);
                        if (piece != null && piece.getTeamColor() != teamColor) {
                            Collection<ChessMove> validMoves = validMoves(position);
                            for (ChessMove opponentMoves : validMoves) {
                                ChessPosition maybeKing = opponentMoves.getEndPosition();
                                if (maybeKing == x.getEndPosition()) {
                                    return true;
                                }
                            }
                        }

                    }
                }
            }
        }
            //get the moves the king can do
            //check if each of those would be in check
            //if yes, return true
            //at least that's the theory
        return false;
    }


    public ChessPosition findKing(ChessGame.TeamColor teamColor) {
        ChessPosition kingPosition = null;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getPieceType() == KING && piece.getTeamColor() == teamColor) {
                    kingPosition = position;
                }
            }
        }
        return kingPosition;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(ChessGame.TeamColor teamColor) {
        if (isInCheckmate(teamColor) || isInCheck(teamColor)) {
            return false;
        }
        Collection<Collection<ChessMove>> teamMoves = new ArrayList<>();
        teamMoves = teamMoves(teamColor);
        for (Collection<ChessMove> moves : teamMoves) {
            for (ChessMove x : moves) {
                ChessPiece tempPiece =
                board.addPiece(x.getEndPosition(), );
            }
        }
    }


    public Collection<Collection<ChessMove>> teamMoves(ChessGame.TeamColor teamColor) { //gets all the moves a team can make
        Collection<Collection<ChessMove>> teamMoves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> validMoves = validMoves(position);
                    if (validMoves != null && !validMoves.isEmpty()) {
                        teamMoves.add(validMoves);
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        throw new RuntimeException("Not implemented");
    }
}
