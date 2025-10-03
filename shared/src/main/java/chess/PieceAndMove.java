package chess;
import java.util.Objects;

public class PieceAndMove {
    private final ChessPiece piece;
    private final ChessMove move;

    public PieceAndMove(ChessPiece piece, ChessMove move) {
        this.piece = piece;
        this.move = move;
    }

    public ChessPiece getPiece() {
        return piece;
    }

    public ChessMove getMove() {
        return move;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PieceAndMove that)) return false;
        return Objects.equals(piece, that.piece) &&
                Objects.equals(move, that.move);
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece, move);
    }

    @Override
    public String toString() {
        return piece.getPieceType() + " " + move.toString();
    }
}
