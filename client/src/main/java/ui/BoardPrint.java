package ui;

import chess.*;

import static chess.ChessGame.TeamColor.*;
import static ui.EscapeSequences.*;

public class BoardPrint {
    private ChessBoard chessBoard = new ChessBoard();
    String darkPinkSquare = SET_BG_COLOR_DARK_PINK + SET_TEXT_COLOR_DARK_PINK;
    String lightPinkSquare = SET_BG_COLOR_LIGHT_PINK + SET_TEXT_COLOR_LIGHT_PINK;

    public void print(ChessGame.TeamColor teamColor) {
        chessBoard.resetBoard();


        int row = (teamColor == WHITE) ? 8 : 1;
        int column = (teamColor == WHITE) ? 8 : 1;
        while ((teamColor == WHITE && row > 0) || (teamColor == BLACK && row < 9)) {
            while ((teamColor == WHITE && column > 0) || (teamColor == BLACK && column < 9)) {
                var piece = chessBoard.getPiece(new ChessPosition(row, column));
                String bgColor;
                if (row % 2 == 0) {
                    bgColor = (column % 2 == 0) ? darkPinkSquare : lightPinkSquare;
                } else {
                    bgColor = (column % 2 == 0) ? lightPinkSquare : darkPinkSquare;
                }
                System.out.print(bgColor);
                if (piece != null) {
                    ChessGame.TeamColor pieceColor = piece.getTeamColor();
                    printPiece(piece, pieceColor);
                } else {
                    System.out.print(BLACK_PAWN);
                }
                column += (teamColor == WHITE) ? -1 : 1;
            }
            if ((teamColor == WHITE && row != 1) || (teamColor == BLACK && row != 8)) {
                nextline(row, teamColor);
            }
            column = (teamColor == WHITE) ? 8 : 1;
            row += (teamColor == WHITE) ? -1 : 1;

        }


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
