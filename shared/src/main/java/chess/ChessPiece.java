package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> moves  = new HashSet<ChessMove>();
        if (piece.getPieceType() == PieceType.BISHOP) {
            moves = bishopMoves(board, myPosition);
        }
        return moves;
    };

    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> bishopMoves = new HashSet<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        while (row < 8 && col < 8) {
            row++;
            col++;
            //write some kind of if to capture a piece
            bishopMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
        }
        while (row > 0 && col < 8) {
            row--;
            col++;
            //write some kind of if to capture a piece
            bishopMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
        }
        while (row < 8 && col > 0) {
            row++;
            col--;
            //write some kind of if to capture a piece
            bishopMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
        }
        while (row > 0 && col > 0) {
            row--;
            col--;
            //write some kind of if to capture a piece
            bishopMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
        }
        return bishopMoves;
    }
}