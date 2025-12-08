package ui;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessGame.TeamColor.*;
import static ui.EscapeSequences.*;

public class BoardPrint {
    String darkPinkSquare = SET_BG_COLOR_DARK_PINK + SET_TEXT_COLOR_DARK_PINK;
    String lightPinkSquare = SET_BG_COLOR_LIGHT_PINK + SET_TEXT_COLOR_LIGHT_PINK;
    String darkPurpleSquare = SET_BG_COLOR_PURPLE3 + SET_TEXT_COLOR_PURPLE3;
    String lightPurpleSquare = SET_BG_COLOR_PURPLE4 + SET_TEXT_COLOR_PURPLE4;


    public void print(ChessGame.TeamColor teamColor, ChessPosition position,
                      ChessBoard chessBoard, ChessGame chessGame) {
        Collection<ChessMove> moves = new ArrayList<>();
        if (position != null) {
            ChessPiece selectedPiece = chessBoard.getPiece(position);
            moves = chessGame.validMoves(position);
            //moves = selectedPiece.pieceMoves(chessBoard, position);

        }

        System.out.print(SET_TEXT_COLOR_WHITE + SET_BG_COLOR_LIGHT_GREY);
        if (teamColor == WHITE) {
            System.out.print("    A   B   C  D   E  F   G   H    " + RESET_BG_COLOR + "\n" + SET_BG_COLOR_LIGHT_GREY + " 8 ");
        } else {
            System.out.print("    H   G   F  E   D  C   B   A    " + RESET_BG_COLOR + "\n" + SET_BG_COLOR_LIGHT_GREY + " 1 ");
        }

        int row = (teamColor == WHITE) ? 8 : 1;
        int column = (teamColor == WHITE) ? 1 : 8;
        while ((teamColor == WHITE && row > 0) || (teamColor == BLACK && row < 9)) {
            while ((teamColor == WHITE && column <= 8) || (teamColor == BLACK && column >= 1)) {
                var currentPosition = new ChessPosition(row, column);
                var piece = chessBoard.getPiece(currentPosition);
                String bgColor;
                boolean darkSquare = ((row + column) % 2 == 0);

                if (hasMove(currentPosition, moves)) {
                    bgColor = darkSquare ? darkPurpleSquare : lightPurpleSquare;
                } else {
                    bgColor = darkSquare ? darkPinkSquare : lightPinkSquare;
                }
                System.out.print(bgColor);
                if (piece != null) {
                    if (currentPosition.equals(position)) {
                        System.out.print(SET_BG_COLOR_BLUE);
                    }
                    ChessGame.TeamColor pieceColor = piece.getTeamColor();
                    printPiece(piece, pieceColor);
                } else {
                    System.out.print(BLACK_PAWN);
                }
                column += (teamColor == WHITE) ? 1 : -1;
            }
            if ((teamColor == WHITE && row != 1) || (teamColor == BLACK && row != 8)) {
                nextline(row, teamColor);
            }
            column = (teamColor == WHITE) ? 1 : 8;
            row += (teamColor == WHITE) ? -1 : 1;

        }

        System.out.print(SET_TEXT_COLOR_WHITE + SET_BG_COLOR_LIGHT_GREY);
        if (teamColor == WHITE) {

            System.out.print(" " + 1 + " " + RESET_BG_COLOR + "\n" + SET_BG_COLOR_LIGHT_GREY);
            System.out.print("    A   B   C  D   E  F   G   H    " + RESET_BG_COLOR);
        } else {
            System.out.print(SET_TEXT_COLOR_WHITE + " " + 8 + " " +
                    RESET_BG_COLOR + "\n" + SET_BG_COLOR_LIGHT_GREY);
            System.out.print("    H   G   F  E   D  C   B   A    " + RESET_BG_COLOR);
        }

    }

    private Boolean hasMove(ChessPosition position, Collection<ChessMove> moves) {
        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }

    private void nextline(int i, ChessGame.TeamColor teamColor) {
        int nextLine = (teamColor == WHITE) ? i - 1 : i + 1;
        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print(SET_BG_COLOR_LIGHT_GREY + " " + i + " " + RESET_BG_COLOR + "\n" + SET_BG_COLOR_LIGHT_GREY + " " + nextLine + " ");
    }

    private void printPiece(ChessPiece piece, ChessGame.TeamColor pieceColor) {
        if (pieceColor == WHITE) {
            System.out.print(SET_TEXT_COLOR_WHITE);
        } else {
            System.out.print(SET_TEXT_COLOR_BLACK);
        }
        switch (piece.getPieceType()) {
            case BISHOP -> System.out.print(BLACK_BISHOP);
            case ROOK -> System.out.print(BLACK_ROOK);
            case QUEEN -> System.out.print(BLACK_QUEEN);
            case KNIGHT -> System.out.print(BLACK_KNIGHT);
            case PAWN -> System.out.print(BLACK_PAWN);
            case KING -> System.out.print(BLACK_KING);
        }
    }
}
