package org.example;

import static org.example.Constants.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// todo: rozdzielic na WordleGameTest i WordleSolverTest
public class WordleGameTest {

    // Helper method for getting private methods.
    // Source: https://stackoverflow.com/questions/34571/how-do-i-test-a-class-that-has-private-methods-fields-or-inner-classes
    private Object getPrivateField(Object targetObject, String fieldName) throws Exception {
        java.lang.reflect.Field field = targetObject.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(targetObject);
    }

    // todo: jezeli allWords nie beda juz jednakowej dlugosci to zmienic tu - DEVU: co? PRzeciez dlugosc slowajest constant wiec nie moze byc inna niz 5.
    private static final String TARGET = "słowo";
    private static final String VALID_WORD = "a".repeat(WORD_SIZE);
    private static final String OTHER_VALID_WORD = "b".repeat(WORD_SIZE);

    private WordleGame game;
    private WordleSolver solver;

    @BeforeEach
    void setUp() {
        solver = new WordleSolver();
        game = new WordleGame("Wordle", TARGET, solver);
    }

    @Test
    @DisplayName("Konstruktor powinien zapisać nazwę gry")
    void constructorShouldStoreGameName() throws Exception {
        assertEquals("Wordle", getPrivateField(game, "gameName"));
    }

    @Test
    @DisplayName("Konstruktor powinien zapisać słowo do zgadnięcia")
    void constructorShouldStoreWordToGuess() throws Exception {
        assertEquals(TARGET, getPrivateField(game, "wordToGuess"));
    }

    @Test
    @DisplayName("Nowa gra powinna mieć zero wykonanych prób")
    void newGameShouldHaveZeroGuesses() throws Exception {
        assertEquals(0, getPrivateField(game, "guessNumber"));
    }

    @Test
    @DisplayName("Zbyt długie słowo powinno rzucić IllegalArgumentException")
    void tooLongWordShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> game.guessWord("a".repeat(WORD_SIZE + 1)));
    }

    @Test
    @DisplayName("Zbyt krótkie słowo powinno rzucić IllegalArgumentException")
    void tooShortWordShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> game.guessWord("a".repeat(WORD_SIZE - 1)));
    }

    @Test
    @DisplayName("Puste słowo powinno rzucić IllegalArgumentException")
    void emptyWordShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> game.guessWord(""));
    }

    @Test
    @DisplayName("Niepodanie słowa powinno rzucić IllegalArgumentException")
    void nullWorldShouldThrowIllegalArgumentException() {
        assertThrows(NullPointerException.class, () -> game.guessWord(null));
    }


    @Test
    @DisplayName("Próba wykraczająca poza limit powinna rzucić IllegalStateException")
    void guessOverMaxTriesShouldThrowIllegalStateException() {
        for (int i = 0; i < MAX_TRIES; i++) {
            game.guessWord(VALID_WORD);
        }

        assertThrows(IllegalStateException.class, () -> game.guessWord(VALID_WORD));
    }

    @Test
    @DisplayName("Próba równa limitowi nie powinna rzucać wyjątku")
    void maxTriesGuessShouldNotThrow() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < MAX_TRIES; i++) {
                game.guessWord(VALID_WORD);
            }
        });
    }

    // todo: jezeli allWords nie beda juz jednakowej dlugosci to zmienic tu (expected i guessWord())
    @Test
    @DisplayName("Poprawne słowo powinno zwrócić same GREEN")
    void correctWordShouldReturnAllGreen() {
        FeedbackValue[] expected = {
                FeedbackValue.GREEN, FeedbackValue.GREEN, FeedbackValue.GREEN,
                FeedbackValue.GREEN, FeedbackValue.GREEN
        };

        assertArrayEquals(expected, game.guessWord(TARGET));
    }

    @Test
    @DisplayName("Słowo bez wspólnych liter powinno zwrócić same GRAY")
    void wordWithNoCommonLettersShouldReturnAllGray() {
        FeedbackValue[] expected = {
                FeedbackValue.GRAY, FeedbackValue.GRAY, FeedbackValue.GRAY,
                FeedbackValue.GRAY, FeedbackValue.GRAY
        };

        assertArrayEquals(expected, game.guessWord("ekran"));
    }

    @Test
    @DisplayName("Litery obecne w słowie, ale na złych pozycjach, powinny być YELLOW")
    void lettersOnWrongPositionsShouldBeYellow() {
        FeedbackValue[] expected = {
                FeedbackValue.YELLOW, FeedbackValue.YELLOW, FeedbackValue.GRAY,
                FeedbackValue.YELLOW, FeedbackValue.GRAY
        };

        assertArrayEquals(expected, game.guessWord("wolsa"));
    }

    @Test
    @DisplayName("Feedback powinien poprawnie mieszać GREEN, YELLOW i GRAY")
    void feedbackShouldMixGreenYellowAndGray() {
        FeedbackValue[] expected = {
                FeedbackValue.GREEN, FeedbackValue.YELLOW, FeedbackValue.GREEN,
                FeedbackValue.GRAY, FeedbackValue.GRAY
        };

        assertArrayEquals(expected, game.guessWord("swoje"));
    }

    @Test
    @DisplayName("Poprawna próba powinna zwiększyć licznik prób o jeden")
    void validGuessShouldIncrementGuessNumber() throws Exception {
        assertEquals(0, getPrivateField(game, "guessNumber"));
        game.guessWord(VALID_WORD);
        assertEquals(1, getPrivateField(game, "guessNumber"));
    }

    @Test
    @DisplayName("Niepoprawna długość słowa nie powinna zwiększać licznika prób")
    void invalidLengthShouldNotIncrementGuessNumber() throws Exception {
        assertEquals(0, getPrivateField(game, "guessNumber"));
        assertThrows(IllegalArgumentException.class, () -> game.guessWord("a".repeat(WORD_SIZE-1)));
        assertEquals(0, getPrivateField(game, "guessNumber"));
    }

    @Test
    @DisplayName("Poprawna próba powinna zostać zapisana w historii prób")
    void validGuessShouldBeStoredInGuesses() throws Exception {
        game.guessWord(VALID_WORD);
        assertArrayEquals(new String[] {VALID_WORD}, (String[]) getPrivateField(game, "guesses"));
    }

    @Test
    @DisplayName("Historia prób powinna zachować kolejność zgadywanych słów")
    void guessesShouldKeepOrder() throws Exception {
        game.guessWord(VALID_WORD);
        game.guessWord(OTHER_VALID_WORD);
        assertArrayEquals(new String[] {VALID_WORD, OTHER_VALID_WORD}, (String[]) getPrivateField(game, "guesses"));
    }

    // todo: jezeli allWords nie beda juz jednakowej dlugosci to zmienic tu (10 nastepnych testow)
    // todo: 2 nastepne test nadal kozystaja z solvera, nie mam jebanego pojecia co z tym faktem zrobić
    @Test
    @DisplayName("Próba powinna zawęzić listę kandydatów")
    void guessShouldNarrowCandidates() {
        game.guessWord("kotek");

        assertArrayEquals(new String[] {"słowo"}, solver.getCandidates());
    }

    // Czmu to jest zakomentowane Janek?

//    @Test
//    @DisplayName("Powtarzająca się litera powinna dostać kolor tyle razy ile występuje w zgadywanym słowie.")
//    void excessRepeatedLetterShouldBeGray() {
//        FeedbackValue[] expected = {
//                FeedbackValue.GRAY, FeedbackValue.GRAY, FeedbackValue.GREEN,
//                FeedbackValue.GRAY, FeedbackValue.GREEN
//        };
//
//        assertArrayEquals(expected, game.guessWord("ooooo"));
//    }

    @Test
    @DisplayName("Lista kandydatów po próbie powinna nadal zawierać słowo do zgadnięcia")
    void candidatesShouldStillContainTargetAfterGuess() {
        game.guessWord(VALID_WORD);

        assertTrue(Arrays.asList(solver.getCandidates()).contains(TARGET));
    }
}