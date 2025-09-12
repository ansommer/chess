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
        //List<ChessMove> bishopMoves = new ArrayList<>();
        final int up = 8;
        final int down = 0;
        class Helper {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            void checkPosition(int aDirection, int bDirection) {
                row = myPosition.getRow();
                col = myPosition.getColumn();
                while (row < aDirection && col < bDirection) {
                    row += (aDirection == up ? 1 : -1);
                    col += (bDirection == up ? 1 : -1);
                    ChessPiece newPosition = board.getPiece(new ChessPosition(row, col));
                    if (newPosition == null) {
                        bishopMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    } else if (newPosition.pieceColor != pieceColor) {
                        bishopMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                        break; // Stop the loop if you captured an opposing piece
                    } else {
                        break; // Stop the loop if a same-color piece is blocking the path
                    }
                }
            }
        }
        Helper helper = new Helper();
        helper.checkPosition(up, up);
        helper.checkPosition(down, up);
        helper.checkPosition(up, down);
        helper.checkPosition(down, down);


/*
        while (row < 8 && col < 8) {
            row++;
            col++;
            ChessPiece newPosition = board.getPiece(new ChessPosition(row, col));
            if (newPosition == null) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            } else if (newPosition.pieceColor != pieceColor) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                break; // Stop the loop if you captured an opposing piece
            } else {
                break; // Stop the loop if a same-color piece is blocking the path
            }
            //if (newPosition == null || newPosition.pieceColor !=  pieceColor)
              //  bishopMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while (row > 1 && col < 8) {
            row--;
            col++;
            ChessPiece newPosition = board.getPiece(new ChessPosition(row, col));
            if (newPosition == null) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            } else if (newPosition.pieceColor != pieceColor) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                break; // Stop the loop if you captured an opposing piece
            } else {
                break; // Stop the loop if a same-color piece is blocking the path
            }
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while (row < 8 && col > 1) {
            row++;
            col--;
            ChessPiece newPosition = board.getPiece(new ChessPosition(row, col));
            if (newPosition == null) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            } else if (newPosition.pieceColor != pieceColor) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                break; // Stop the loop if you captured an opposing piece
            } else {
                break; // Stop the loop if a same-color piece is blocking the path
            }
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while (row > 1 && col > 1) {
            row--;
            col--;
            ChessPiece newPosition = board.getPiece(new ChessPosition(row, col));
            if (newPosition == null) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            } else if (newPosition.pieceColor != pieceColor) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                break; // Stop the loop if you captured an opposing piece
            } else {
                break; // Stop the loop if a same-color piece is blocking the path
            }
        }*/

        return bishopMoves;
    }
}