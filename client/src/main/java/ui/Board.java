package ui;

import static ui.EscapeSequences.*;;

public interface Board {

    default void printPieces(String color, int player) {
        System.out.print(color);
        String[] pieces = {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN,
                BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK};
        for (int i = 0; i < pieces.length; i++) {
            if (player == 1) {
                String bgColor = (i % 2 == 0) ? SET_BG_COLOR_LIGHT_PINK : SET_BG_COLOR_DARK_PINK;
                System.out.print(bgColor);
                System.out.print(pieces[i]);
            } else {
                String bgColor = (i % 2 == 0) ? SET_BG_COLOR_DARK_PINK : SET_BG_COLOR_LIGHT_PINK;
                System.out.print(bgColor);
                System.out.print(pieces[i]);
            }

        }
    }

    default void printPawns(String color, int player) {
        System.out.print(color);
        for (int i = 0; i < 8; i++) {
            if (player == 1) {
                String bgColor = (i % 2 == 0) ? SET_BG_COLOR_DARK_PINK : SET_BG_COLOR_LIGHT_PINK;
                System.out.print(bgColor);
                System.out.print(BLACK_PAWN);
            } else {
                String bgColor = (i % 2 == 0) ? SET_BG_COLOR_LIGHT_PINK : SET_BG_COLOR_DARK_PINK;
                System.out.print(bgColor);
                System.out.print(BLACK_PAWN);
            }
        }
    }

    default void nextline(int i, int direction) {
        int nextLine = i + direction;
        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print(SET_BG_COLOR_LIGHT_GREY + " " + i + " " + RESET_BG_COLOR + "\n" + SET_BG_COLOR_LIGHT_GREY + " " + nextLine + " ");
    }

    default void printBlanks(int direction, int lineNumber) {
        //int lineNumber = 6;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                String bgColor = (j % 2 == 0) ? SET_BG_COLOR_DARK_PINK : SET_BG_COLOR_LIGHT_PINK;
                String textColor = (j % 2 == 0) ? SET_TEXT_COLOR_DARK_PINK : SET_TEXT_COLOR_LIGHT_PINK;
                if (i % 2 == 0) {
                    bgColor = (j % 2 == 0) ? SET_BG_COLOR_LIGHT_PINK : SET_BG_COLOR_DARK_PINK;
                    textColor = (j % 2 == 0) ? SET_TEXT_COLOR_LIGHT_PINK : SET_TEXT_COLOR_DARK_PINK;
                }

                System.out.print(bgColor + textColor);
                System.out.print(BLACK_PAWN);
            }
            nextline(lineNumber, direction);
            lineNumber += direction;
        }
    }
}
