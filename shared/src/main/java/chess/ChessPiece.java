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

    // Maybe I need to override the equals function. That sounds kind right. So let's check with a TA. Yeah...

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
        Collection<ChessMove> moves  = new HashSet<>();
        if (piece.getPieceType() == PieceType.BISHOP) {
            moves = bishopMoves(board, myPosition);
        }
        return moves;
    }

    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> bishopMoves = new HashSet<>();
        //List<ChessMove> bishopMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        class Helper {
            boolean checkPosition(int checkRow, int checkCol) {
                ChessPiece newPosition = board.getPiece(new ChessPosition(checkRow, checkCol));
                if (newPosition == null) {
                    bishopMoves.add(new ChessMove(myPosition, new ChessPosition(checkRow, checkCol), null));
                } else if (newPosition.pieceColor != pieceColor) {
                    bishopMoves.add(new ChessMove(myPosition, new ChessPosition(checkRow, checkCol), null));
                    return false;
                } else {
                    return false;
                }
                return true;
            }
        }

        Helper helper = new Helper();

        while (row < 8 && col < 8) {
            row++;
            col++;
            ChessPiece newPosition = board.getPiece(new ChessPosition(row, col));
            if (!helper.checkPosition(row, col)) {break;}
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while (row > 1 && col < 8) {
            row--;
            col++;
            ChessPiece newPosition = board.getPiece(new ChessPosition(row, col));
            if (!helper.checkPosition(row, col)) {break;}
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while (row < 8 && col > 1) {
            row++;
            col--;
            ChessPiece newPosition = board.getPiece(new ChessPosition(row, col));
            if (!helper.checkPosition(row, col)) {break;}
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while (row > 1 && col > 1) {
            row--;
            col--;
            ChessPiece newPosition = board.getPiece(new ChessPosition(row, col));
            if (!helper.checkPosition(row, col)) {break;}
        }

        return bishopMoves;
    }
}