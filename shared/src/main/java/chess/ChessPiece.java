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
        } else if (piece.getPieceType() == PieceType.PAWN) {
            moves = pawnMoves(board, myPosition);
        }
        else if (piece.getPieceType() == PieceType.QUEEN) {
            moves = bishopMoves(board, myPosition);
            //what's the difference btwn a collection and a hashset and why am i using them
            Collection<ChessMove> rookMovesSet = rookMoves(board, myPosition);
            moves.addAll(rookMovesSet);
        }
        else if (piece.getPieceType() == PieceType.ROOK) {
            moves = rookMoves(board, myPosition);
        }
        return moves;
    }

    public boolean checkPosition(ChessBoard board, ChessPosition myPosition, int checkRow, int checkCol) {
        if (checkRow > 0 && checkRow <= 8 && checkCol > 0 && checkCol <= 8) {
            ChessPiece newPosition = board.getPiece(new ChessPosition(checkRow, checkCol));
            return newPosition == null || newPosition.pieceColor != pieceColor;
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
            if (checkPosition(board, myPosition, row, col)) {
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
            if (checkPosition(board, myPosition, row, col)) {
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
            if (checkPosition(board, myPosition, row, col)) {
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
            if (checkPosition(board, myPosition, row, col)) {
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
            if (checkPosition(board, myPosition, xrow, xcol)) {
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
            if (checkPosition(board, myPosition, xrow, xcol)) {
                knightMoves.add(new ChessMove(myPosition, new ChessPosition(xrow, xcol), null));
            }
        }

        return knightMoves;
    }

    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        //ok idk if I have to see if it's their first move and they can move 2x or promote also
        HashSet<ChessMove> pawnMoves = new HashSet<>();
//        HashSet<ChessPosition> possibilities = new HashSet<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece piece = board.getPiece(myPosition);
        //White
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            ChessPiece newPosition = board.getPiece(new ChessPosition(row+1, col));
            if (newPosition == null) {
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row+1, col), null));
            }
            if (col == 1) {
                newPosition = board.getPiece(new ChessPosition(row+2, col));
                if (newPosition == null) {
                    pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row+2, col), null));
                }
            }
            newPosition = board.getPiece(new ChessPosition(row+1, col+1));
            if (newPosition != null) {checkPosition(board, myPosition, row+1, col+1);}
            newPosition = board.getPiece(new ChessPosition(row+1, col-1));
            if (newPosition != null) {checkPosition(board, myPosition, row+1, col-1);}
        } else { //Black
            ChessPiece newPosition = board.getPiece(new ChessPosition(row-1, col));
            if (newPosition == null) {
                pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row-1, col), null));
            }
            if (col == 1) {
                newPosition = board.getPiece(new ChessPosition(row-2, col));
                if (newPosition == null) {
                    pawnMoves.add(new ChessMove(myPosition, new ChessPosition(row-2, col), null));
                }
            }
            newPosition = board.getPiece(new ChessPosition(row-1, col+1));
            if (newPosition != null) {checkPosition(board, myPosition, row-1, col+1);}
            newPosition = board.getPiece(new ChessPosition(row-1, col-1));
            if (newPosition != null) {checkPosition(board, myPosition, row-1, col-1);}
        }

        return pawnMoves;

    }

    /*public Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> queenMoves = new HashSet<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        queenMoves = bishopMoves(board, myPosition);
        return queenMoves;
    }*/

    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> rookMoves = new HashSet<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        while (row < 8) {
            row++;
            if (checkPosition(board, myPosition, row, col)) {
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
            ChessPiece newPosition = board.getPiece(new ChessPosition(row, col));
            if (newPosition != null) {break;}
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while (row > 1) {
            row--;
            if (checkPosition(board, myPosition, row, col)) {
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
            ChessPiece newPosition = board.getPiece(new ChessPosition(row, col));
            if (newPosition != null) {break;}
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while (col < 8) {
            col++;
            if (checkPosition(board, myPosition, row, col)) {
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
            ChessPiece newPosition = board.getPiece(new ChessPosition(row, col));
            if (newPosition != null) {break;}
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        while (col > 1) {
            col--;
            if (checkPosition(board, myPosition, row, col)) {
                rookMoves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            }
            ChessPiece newPosition = board.getPiece(new ChessPosition(row, col));
            if (newPosition != null) {break;}
        }

        return rookMoves;
    }


}