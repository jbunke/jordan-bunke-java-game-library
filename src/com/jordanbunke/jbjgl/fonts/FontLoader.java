package com.jordanbunke.jbjgl.fonts;

import com.jordanbunke.jbjgl.image.ImageProcessing;
import com.jordanbunke.jbjgl.image.JBJGLImage;
import com.jordanbunke.jbjgl.io.JBJGLImageIO;

import java.awt.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static com.jordanbunke.jbjgl.fonts.FontConstants.LINE_HEIGHT;
import static com.jordanbunke.jbjgl.fonts.FontConstants.MATCH_COLOR;

public class FontLoader {
    private static final int X_INCREMENT = 20, Y_INCREMENT = 38;
    private static final int X_INDEX = 0, Y_INDEX = 1;

    private static final char STARTING_ASCII = 33, FINAL_ASCII = 126;
    private static final int NUM_LATIN_EXTENDED_CHARS = 59;
    private static final int CHARS_ON_ROW = 16;

    public static Map<Character, Grapheme> loadASCIIFromSource(final Path source) {
        JBJGLImage image = JBJGLImageIO.readImage(source);
        Map<Character, Grapheme> map = new HashMap<>();

        map.put(' ', whitespace());

        for (char c = STARTING_ASCII; c <= FINAL_ASCII; c++) {
            int[] coordinates = asciiToCoordinates(c);

            if (coordinates.length != 2) continue;

            Grapheme grapheme = graphemeFromCoordinates(image, c, coordinates);
            map.put(c, grapheme);
        }

        return map;
    }

    public static Map<Character, Grapheme> loadLatinExtendedFromSource(final Path source) {
        JBJGLImage image = JBJGLImageIO.readImage(source);
        Map<Character, Grapheme> map = new HashMap<>();

        for (int i = 0; i < NUM_LATIN_EXTENDED_CHARS; i++) {
            int[] coordinates = asciiToCoordinates((char) i);

            if (coordinates.length != 2) continue;

            char c = charFromIndexLatinExtended(i);

            Grapheme grapheme = graphemeFromCoordinates(image, c, coordinates);
            map.put(c, grapheme);
        }

        return map;
    }

    // HELPER FUNCTIONS:
    private static Grapheme graphemeFromCoordinates(final JBJGLImage image,
                                                    final char c, final int[] coordinates) {
        final int[] start = new int[]
                { coordinates[X_INDEX] * X_INCREMENT, coordinates[Y_INDEX] * Y_INCREMENT };
        final int[] size = new int[] { X_INCREMENT - 1, Y_INCREMENT - 1 };

        // BOUNDS DETERMINATION
        int firstXWith = firstXWith(image, start, size);
        int firstXWithout = firstXWithout(image, start, size);

        // COPY
        JBJGLImage grapheme = getGrapheme(image, start, size, firstXWith, firstXWithout);

        return Grapheme.create(grapheme, c, firstXWithout - firstXWith, size[Y_INDEX]);
    }

    private static JBJGLImage getGrapheme(final JBJGLImage image, final int[] start, final int[] size,
                                             final int firstXWith, final int firstXWithout) {
        JBJGLImage grapheme = JBJGLImage.create(firstXWithout - firstXWith, size[Y_INDEX]);
        Graphics g = grapheme.getGraphics();
        g.setColor(MATCH_COLOR);

        for (int x = firstXWith; x < firstXWithout; x++) {
            for (int y = start[Y_INDEX]; y < start[Y_INDEX] + size[Y_INDEX]; y++) {
                if (ImageProcessing.colorAtPixel(image, x, y).equals(MATCH_COLOR)) {
                    g.fillRect(x - firstXWith, y - start[Y_INDEX], 1, 1);
                }
            }
        }

        g.dispose();

        return grapheme;
    }

    private static Grapheme whitespace() {
        final int width = 8;
        return Grapheme.create(JBJGLImage.create(width, LINE_HEIGHT), ' ', width, LINE_HEIGHT);
    }

    private static int firstXWithout(final JBJGLImage image, final int[] start, final int[] size) {
        for (int x = (start[X_INDEX] + size[X_INDEX]) - 1; x >= start[X_INDEX]; x--) {
            for (int y = start[Y_INDEX]; y < start[Y_INDEX] + size[Y_INDEX]; y++) {
                if (ImageProcessing.colorAtPixel(image, x, y).equals(MATCH_COLOR)) {
                    return x + 1;
                }
            }
        }

        return start[X_INDEX] + size[X_INDEX];
    }

    private static int firstXWith(final JBJGLImage image, final int[] start, final int[] size) {
        for (int x = start[X_INDEX]; x < start[X_INDEX] + size[X_INDEX]; x++) {
            for (int y = start[Y_INDEX]; y < start[Y_INDEX] + size[Y_INDEX]; y++) {
                if (ImageProcessing.colorAtPixel(image, x, y).equals(MATCH_COLOR)) {
                    return x;
                }
            }
        }

        return start[X_INDEX];
    }

    private static char charFromIndexLatinExtended(int i) {
        return switch (i) {
            case 0 -> '??';
            case 1 -> '??';
            case 2 -> '??';
            case 3 -> '??';
            case 4 -> '??';
            case 5 -> '??';
            case 6 -> '??';
            case 7 -> '??';
            case 8 -> '??';
            case 9 -> '??';
            case 10 -> '??';
            case 11 -> '??';
            case 12 -> '??';
            case 13 -> '??';
            case 14 -> '??';
            case 15 -> '??';
            case 16 -> '??';
            case 17 -> '??';
            case 18 -> '??';
            case 19 -> '??';
            case 20 -> '??';
            case 21 -> '??';
            case 22 -> '??';
            case 23 -> '??';
            case 24 -> '??';
            case 25 -> '??';
            case 26 -> '??';
            // GAP
            case 32 -> '??';
            case 33 -> '??';
            case 34 -> '??';
            case 35 -> '??';
            case 36 -> '??';
            case 37 -> '??';
            case 38 -> '??';
            case 39 -> '??';
            case 40 -> '??';
            case 41 -> '??';
            case 42 -> '??';
            case 43 -> '??';
            case 44 -> '??';
            case 45 -> '??';
            case 46 -> '??';
            case 47 -> '??';
            case 48 -> '??';
            case 49 -> '??';
            case 50 -> '??';
            case 51 -> '??';
            case 52 -> '??';
            case 53 -> '??';
            case 54 -> '??';
            case 55 -> '??';
            case 56 -> '??';
            case 57 -> '??';
            case 58 -> '??';
            default -> '???';
        };
    }

    private static int[] asciiToCoordinates(final char c) {
        final int x = c % CHARS_ON_ROW, y = c / CHARS_ON_ROW;

        return new int[] { x, y };
    }
}
