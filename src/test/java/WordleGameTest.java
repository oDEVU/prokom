import static org.junit.jupiter.api.Assertions.assertThrows;

import org.example.WordleGame;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class WordleGameTest {

    private final WordleGame game = new WordleGame("Wordle", "slowo");

    @Test
    @DisplayName("Words not exactly 5 letters long throw IllegalArgumentException")
    void testWordLengthLimit() {
        assertThrows(IllegalArgumentException.class, () -> game.guessWord("abcdef"));
        assertThrows(IllegalArgumentException.class, () -> game.guessWord("abcd"));
    }

    @Test
    @DisplayName("More than 6 tries throw IllegalStateException")
    void testTriesLimit() {
        final WordleGame tmpGame = new WordleGame("TempWordle", "slowo");

        assertThrows(IllegalStateException.class, () -> {
            for (int i = 0; i < 7; i++) {
                tmpGame.guessWord("abcde");
            }
        });
    }
}