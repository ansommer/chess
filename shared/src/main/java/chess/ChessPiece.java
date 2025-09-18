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
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece piece = (ChessPiece) o;
        return (pieceColor == piece.pieceColor && type == piece.type);
    }

    //alright I think it's correct but I wanna understand it more
    @Override
    public int hashCode() {
        return (71 * pieceColor.hashCode()) + type.hashCode();
    }

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    //Need to ask about the hashCode

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
        //Collection<ChessMove> moves  = new HashSet<>();
        Collection<ChessMove> moves = new ArrayList<>();
        if (piece.getPieceType() == PieceType.BISHOP) {
            moves = bishopMoves(board, myPosition);
        } else if (piece.getPieceType() == PieceType.KING) {
            moves = kingMoves(board, myPosition);
        } else if (piece.getPieceType() == PieceType.KNIGHT) {
            moves = knightMoves(board, myPosition);
        }
        return moves;
    }

    public boolean checkPosition(ChessBoard board, ChessPosition myPosition, int checkRow, int checkCol, HashSet<ChessMove> moves) {

        if (checkRow > 0 && checkRow <= 8 && checkCol > 0 && checkCol <= 8) {
            ChessPiece newPosition = board.getPiece(new ChessPosition(checkRow, checkCol));
            if (newPosition == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(checkRow, checkCol), null));
            } else if (newPosition.pieceColor != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(checkRow, checkCol), null));
                return true;
            } else {
                return true;
            }
        }
        return false;
    }

    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> bishopMoves = new HashSet<>();
        //List<ChessMove> bishopMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        while (row < 8 && col < 8) {
            row++;
            col++;
            if (checkPosition(board, myPosition, row, col, bishopMoves)) {
                break;
            }
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while (row > 1 && col < 8) {
            row--;
            col++;
            if (checkPosition(board, myPosition, row, col, bishopMoves)) {
                break;
            }
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while (row < 8 && col > 1) {
            row++;
            col--;
            if (checkPosition(board, myPosition, row, col, bishopMoves)) {
                break;
            }
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while (row > 1 && col > 1) {
            row--;
            col--;
            if (checkPosition(board, myPosition, row, col, bishopMoves)) {
                break;
            }
        }

        return bishopMoves;
    }

    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> kingMoves = new HashSet<>();
        HashSet<ChessPosition> possibilities = new HashSet<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        possibilities.add(new ChessPosition(row +1, col -1));
        possibilities.add(new ChessPosition(row +1, col));
        possibilities.add(new ChessPosition(row +1, col +1));
        possibilities.add(new ChessPosition(row, col -1));
        possibilities.add(new ChessPosition(row, col +1));
        possibilities.add(new ChessPosition(row -1, col -1));
        possibilities.add(new ChessPosition(row -1, col));
        possibilities.add(new ChessPosition(row -1, col +1));

        for (ChessPosition x : possibilities) {
            int xrow = x.getRow();
            int xcol = x.getColumn();
            checkPosition(board, myPosition, xrow, xcol, kingMoves);
        }

        return kingMoves;
    }

    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> knightMoves = new HashSet<>();
        HashSet<ChessPosition> possibilities = new HashSet<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        possibilities.add(new ChessPosition(row + 2, col - 1));
        possibilities.add(new ChessPosition(row + 2, col + 1));
        possibilities.add(new ChessPosition(row - 2, col - 1));
        possibilities.add(new ChessPosition(row - 2, col + 1));
        possibilities.add(new ChessPosition(row + 1, col - 2));
        possibilities.add(new ChessPosition(row + 1, col + 2));
        possibilities.add(new ChessPosition(row - 1, col - 2));
        possibilities.add(new ChessPosition(row - 1, col + 2));

        for (ChessPosition x : possibilities) {
            int xrow = x.getRow();
            int xcol = x.getColumn();
            checkPosition(board, myPosition, xrow, xcol, knightMoves);
        }

        return knightMoves;
    }

}