package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static chess.ChessPiece.PieceType.ROOK;

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
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
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
        Collection<ChessMove> moves  = new HashSet<>();

        if (piece.getPieceType() == PieceType.BISHOP) {
            moves = bishopMoves(board, myPosition);
        } else if (piece.getPieceType() == PieceType.KING) {
            moves = kingMoves(board, myPosition);
        } else if (piece.getPieceType() == PieceType.KNIGHT) {
            moves = knightMoves(board, myPosition);
        } else if (piece.getPieceType() == PieceType.PAWN) {
            moves = pawnMoves(board, myPosition);
        }
        else if (piece.getPieceType() == PieceType.QUEEN) {
            moves = bishopMoves(board, myPosition);
            Collection<ChessMove> rookMovesSet = rookMoves(board, myPosition);
            moves.addAll(rookMovesSet);
        }
        else if (piece.getPieceType() == ROOK) {
            moves = rookMoves(board, myPosition);
        }
        return moves;
    }

    public boolean checkPosition(ChessBoard board, int checkRow, int checkCol) {
        if (checkRow > 0 && checkRow <= 8 && checkCol > 0 && checkCol <= 8) {
            ChessPiece newPosition = board.getPiece(new ChessPosition(checkRow, checkCol));
            return newPosition == null || !newPosition.pieceColor.equals(pieceColor);
        }
        return false;
    }

    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> bishopMoves = new HashSet<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

       while (row < 8 && col < 8) {
            row++;
            col++;
            if (checkPosition(board, row, col)) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
           ChessPiece newPosition = board.getPiece(new ChessPosition(row, col));
           if (newPosition != null) {break;}
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while (row > 1 && col < 8) {
            row--;
            col++;
            if (checkPosition(board, row, col)) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null)); //maybe this goes outside? confused
            }
            ChessPiece newPosition = board.getPiece(new ChessPosition(row, col));
            if (newPosition != null) {break;}
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while (row < 8 && col > 1) {
            row++;
            col--;
            if (checkPosition(board, row, col)) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
            ChessPiece newPosition = board.getPiece(new ChessPosition(row, col));
            if (newPosition != null) {break;}
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while (row > 1 && col > 1) {
            row--;
            col--;
            if (checkPosition(board, row, col)) {
                bishopMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
            ChessPiece newPosition = board.getPiece(new ChessPosition(row, col));
            if (newPosition != null) {break;}
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
            if (checkPosition(board, xrow, xcol)) {
                kingMoves.add(new ChessMove(myPosition, new ChessPosition(xrow, xcol), null));
            }

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
            if (checkPosition(board, xrow, xcol)) {
                knightMoves.add(new ChessMove(myPosition, new ChessPosition(xrow, xcol), null));
            }
        }

        return knightMoves;
    }

    public boolean hasPiece(ChessBoard board, ChessPosition position, int row, int col ) {
        if (1<=row && row<=8 && 1<=col && col<=8 ) {
            ChessPiece piece = board.getPiece(position);
            return piece != null;
        }
        return false;
    }

    public HashSet<ChessMove> pawnPromote(ChessPosition myPosition, ChessPosition newPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
        moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
        moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
        moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
        return moves;
    }

    public HashSet<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        //white
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            //check if it can move 1 (if yes check if can move 2)
            if (!hasPiece(board, new ChessPosition(row+1, col), row+1, col)) {
                if (row+1 == 8) {
                    moves.addAll(pawnPromote(myPosition, new ChessPosition(row + 1, col)));
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col), null));
                }
                //check if can move 2
                if (row == 2) {
                    if (!hasPiece(board, new ChessPosition(row+2, col), row+2, col)) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, col), null));
                    }
                }
            }
            //check if can move diagonally
            ChessPosition diagonal = new ChessPosition(row+1, col+1);
            if (hasPiece(board, diagonal, diagonal.getRow(), diagonal.getColumn())) {
                ChessPiece piece = board.getPiece(diagonal);
                if (piece.pieceColor != pieceColor) {
                    if (row + 1 == 8) {
                        moves.addAll(pawnPromote(myPosition, new ChessPosition(row + 1, col + 1)));
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col + 1), null));
                    }
                }
            }
            diagonal = new ChessPosition(row+1, col-1);
            if (hasPiece(board, diagonal, diagonal.getRow(), diagonal.getColumn())) {
                ChessPiece piece = board.getPiece(diagonal);
                if (piece.pieceColor != pieceColor) {
                    if (row + 1 == 8) {
                        moves.addAll(pawnPromote(myPosition,diagonal));
                    } else {
                        moves.add(new ChessMove(myPosition, diagonal, null));
                    }
                }
            }
        }

        //black
        if (pieceColor == ChessGame.TeamColor.BLACK) {
            //check if it can move 1 (if yes check if can move 2)
            if (!hasPiece(board, new ChessPosition(row-1, col), row-1, col)) {
                if (row-1 == 1) {
                    moves.addAll(pawnPromote(myPosition, new ChessPosition(row - 1, col)));
                } else {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col), null));
                }
                //check if can move 2
                if (row == 7) {
                    if (!hasPiece(board, new ChessPosition(row-2, col), row-2, col)) {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row - 2, col), null));
                    }
                }
            }
            //check if can move diagonally
            ChessPosition diagonal = new ChessPosition(row-1, col+1);
            if (hasPiece(board, diagonal, diagonal.getRow(), diagonal.getColumn())) {
                ChessPiece piece = board.getPiece(diagonal);
                if (piece.pieceColor != pieceColor) {
                    if (row -1 == 1) {
                        moves.addAll(pawnPromote(myPosition, new ChessPosition(row - 1, col + 1)));
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col + 1), null));
                    }
                }
            }
            diagonal = new ChessPosition(row-1, col-1);
            if (hasPiece(board, diagonal, diagonal.getRow(), diagonal.getColumn())) {
                ChessPiece piece = board.getPiece(diagonal);
                if (piece.pieceColor != pieceColor) {
                    if (row-1 == 1) {
                        moves.addAll(pawnPromote(myPosition,diagonal));
                    } else {
                        moves.add(new ChessMove(myPosition, diagonal, null));
                    }
                }
            }
        }
        return moves;
    }

    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> rookMoves = new HashSet<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        while (row < 8) {
            row++;
            if (checkPosition(board, row, col)) {
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
            ChessPiece newPosition = board.getPiece(new ChessPosition(row, col));
            if (newPosition != null) {break;}
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while (row > 1) {
            row--;
            if (checkPosition(board, row, col)) {
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
            ChessPiece newPosition = board.getPiece(new ChessPosition(row, col));
            if (newPosition != null) {break;}
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while (col < 8) {
            col++;
            if (checkPosition(board, row, col)) {
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
            ChessPiece newPosition = board.getPiece(new ChessPosition(row, col));
            if (newPosition != null) {break;}
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while (col > 1) {
            col--;
            if (checkPosition(board, row, col)) {
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
            ChessPiece newPosition = board.getPiece(new ChessPosition(row, col));
            if (newPosition != null) {break;}
        }

        return rookMoves;
    }


}