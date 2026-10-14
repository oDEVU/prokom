import static org.junit.jupiter.api.Assertions.assertEquals;

import org.example.WordleGame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WordleGameTest {

    private final WordleGame game = new WordleGame("Wordle", "slowo");

    @Test
    @DisplayName("Do \'>5\' letter words throw IllegalArgumentException")
    void testWordLenghtLimit() {
        assertThrows(IllegalArgumentException.class, () -> {
            game.guessWord("abcdef");
        });
    }
}