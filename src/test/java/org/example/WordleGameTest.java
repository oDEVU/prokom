package org.example;

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

public class WordleGameTest {

    // Helper method for getting private methods.
    // Source: https://stackoverflow.com/questions/34571/how-do-i-test-a-class-that-has-private-methods-fields-or-inner-classes
    private Object getPrivateField(Object targetObject, String fieldName) throws Exception {
        java.lang.reflect.Field field = targetObject.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(targetObject);
    }

    private static final String TARGET = "słowo";

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
        assertThrows(IllegalArgumentException.class, () -> game.guessWord("abcdef"));
    }

    @Test
    @DisplayName("Zbyt krótkie słowo powinno rzucić IllegalArgumentException")
    void tooShortWordShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> game.guessWord("abcd"));
    }

    @Test
    @DisplayName("Puste słowo powinno rzucić IllegalArgumentException")
    void emptyWordShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> game.guessWord(""));
    }

    /* todo: test for null
    @Test
    @DisplayName("")
    void () {
        assertThrows(IllegalArgumentException.class, () -> game.guessWord(null));
    }
    */

    @Test
    @DisplayName("Siódma próba powinna rzucić IllegalStateException")
    void seventhGuessShouldThrowIllegalStateException() {
        for (int i = 0; i < 6; i++) {
            game.guessWord("abcde");
        }

        assertThrows(IllegalStateException.class, () -> game.guessWord("abcde"));
    }

    @Test
    @DisplayName("Sześć prób nie powinno rzucać wyjątku")
    void sixGuessesShouldNotThrow() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 6; i++) {
                game.guessWord("abcde");
            }
        });
    }

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
        game.guessWord("abcde");
        assertEquals(1, getPrivateField(game, "guessNumber"));
    }

    @Test
    @DisplayName("Niepoprawna długość słowa nie powinna zwiększać licznika prób")
    void invalidLengthShouldNotIncrementGuessNumber() throws Exception {
        assertEquals(0, getPrivateField(game, "guessNumber"));
        assertThrows(IllegalArgumentException.class, () -> game.guessWord("abc"));
        assertEquals(0, getPrivateField(game, "guessNumber"));
    }

    @Test
    @DisplayName("Poprawna próba powinna zostać zapisana w historii prób")
    void validGuessShouldBeStoredInGuesses() throws Exception {
        game.guessWord("abcde");
        assertArrayEquals(new String[] {"abcde"}, (String[]) getPrivateField(game, "guesses"));
    }

    @Test
    @DisplayName("Historia prób powinna zachować kolejność zgadywanych słów")
    void guessesShouldKeepOrder() throws Exception {
        game.guessWord("abcde");
        game.guessWord("drzwi");
        assertArrayEquals(new String[] {"abcde", "drzwi"}, (String[]) getPrivateField(game, "guesses"));
    }

    @Test
    @DisplayName("Próba powinna zawęzić listę kandydatów")
    void guessShouldNarrowCandidates() {
        game.guessWord("kotek");

        assertArrayEquals(new String[] {"słowo"}, solver.getCandidates());
    }

    @Test
    @DisplayName("GREEN powinien zostawić tylko słowa z tą literą na tej pozycji")
    void greenShouldKeepOnlyWordsWithLetterAtSamePosition() {
        FeedbackValue[] feedback = {
                FeedbackValue.GREEN, FeedbackValue.GRAY, FeedbackValue.GRAY,
                FeedbackValue.GRAY, FeedbackValue.GRAY
        };

        // litery 'x' uzyto jako place holder gdyż zadne slowo w candidates jej nie ma i nie zostanie odzucone.
        // Użycie innych liter skutowalo w przedwczesnym odzuceniem wszystkich kandydatow.
        solver.applyFeedback("kxxxx", feedback);

        assertArrayEquals(new String[] {"kotek", "konto", "kwiat"}, solver.getCandidates());
    }

    @Test
    @DisplayName("YELLOW powinien zostawić słowa z tą literą na innej pozycji")
    void yellowShouldKeepOnlyWordsWithLetterOnDifferentPosition() {
        FeedbackValue[] feedback = {
                FeedbackValue.YELLOW, FeedbackValue.GRAY, FeedbackValue.GRAY,
                FeedbackValue.GRAY, FeedbackValue.GRAY
        };

        // litery 'x' uzyto jako place holder gdyż zadne slowo w candidates jej nie ma i nie zostanie odzucone.
        // Użycie innych liter skutowalo w przedwczesnym odzuceniem wszystkich kandydatow.
        solver.applyFeedback("kxxxx", feedback);

        assertArrayEquals(new String[] {"ekran", "pasek", "mleko", "miska"}, solver.getCandidates());
    }

    @Test
    @DisplayName("Wiele pozycji GREEN powinno zostawić każdego pasującego kandydata tylko raz")
    void multipleGreensShouldKeepEachCandidateOnce() {
        FeedbackValue[] feedback = {
                FeedbackValue.GREEN, FeedbackValue.GREEN, FeedbackValue.GRAY,
                FeedbackValue.GRAY, FeedbackValue.GRAY
        };

        // litery 'x' uzyto jako place holder gdyż zadne slowo w candidates jej nie ma i nie zostanie odzucone.
        // Użycie innych liter skutowalo w przedwczesnym odzuceniem wszystkich kandydatow.
        solver.applyFeedback("koxxx", feedback);

        assertArrayEquals(new String[] {"kotek", "konto"}, solver.getCandidates());
    }

    @Test
    @DisplayName("GRAY powinno usunąć kandydatów zawierających daną literę")
    void grayShouldRemoveCandidatesWithLetter() {
        FeedbackValue[] feedback = {
                FeedbackValue.GRAY, FeedbackValue.GRAY, FeedbackValue.GRAY,
                FeedbackValue.GRAY, FeedbackValue.GRAY
        };

        // litery 'x' uzyto jako place holder gdyż zadne slowo w candidates jej nie ma i nie zostanie odzucone.
        // Użycie innych liter skutowalo w przedwczesnym odzuceniem wszystkich kandydatow.
        solver.applyFeedback("kxxxx", feedback);

        assertArrayEquals(new String[] {"trawa", "słowo", "drzwi", "lampa", "rower", "serce", "chleb", "zegar"}, solver.getCandidates());
    }

    @Test
    @DisplayName("YELLOW powinno usunąć kandydatów nieposiadających danej litery")
    void yellowShouldRemoveCandidatesWithoutLetter() {
        FeedbackValue[] feedback = {
                FeedbackValue.YELLOW, FeedbackValue.GRAY, FeedbackValue.GRAY,
                FeedbackValue.GRAY, FeedbackValue.GRAY
        };

        // litery 'x' uzyto jako place holder gdyż zadne slowo w candidates jej nie ma i nie zostanie odzucone.
        // Użycie innych liter skutowalo w przedwczesnym odzuceniem wszystkich kandydatow.
        solver.applyFeedback("zxxxx", feedback);

        assertArrayEquals(new String[] {"drzwi"}, solver.getCandidates());
    }

    @Test
    @DisplayName("Same GREEN powinny zostawić tylko zgadnięte słowo")
    void allGreenShouldLeaveOnlyGuessedWord() {
        FeedbackValue[] feedback = {
                FeedbackValue.GREEN, FeedbackValue.GREEN, FeedbackValue.GREEN,
                FeedbackValue.GREEN, FeedbackValue.GREEN
        };

        solver.applyFeedback("kotek", feedback);

        assertArrayEquals(new String[] {"kotek"}, solver.getCandidates());
    }

    @Test
    @DisplayName("Feedback niepasujący do żadnego słowa powinien opróżnić listę kandydatów")
    void feedbackMatchingNothingShouldEmptyCandidates() {
        FeedbackValue[] feedback = {
                FeedbackValue.GREEN, FeedbackValue.GREEN, FeedbackValue.GREEN,
                FeedbackValue.GREEN, FeedbackValue.GREEN
        };

        solver.applyFeedback("xxxxx", feedback);

        assertEquals(0, solver.getCandidates().length);
    }

    @Test
    @DisplayName("Feedback zawierający wszystkie możliwe wartości powinien zastosować ich reguły")
    void feedbackWithAllValues() {
        FeedbackValue[] feedback = {
                FeedbackValue.GREEN, FeedbackValue.GRAY, FeedbackValue.GRAY,
                FeedbackValue.GRAY, FeedbackValue.YELLOW
        };

        solver.applyFeedback("kwiat", feedback);

        assertArrayEquals(new String[] {"kotek", "konto"}, solver.getCandidates());
    }

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
        game.guessWord("konto");

        assertTrue(Arrays.asList(solver.getCandidates()).contains(TARGET));
    }

    @Test
    @DisplayName("Nowa gra powinna zaczynać z 15 kandydatami")
    void newGameShouldStartWithFifteenCandidates() {
        assertEquals(15, solver.getCandidates().length);
    }

    @Test
    @DisplayName("getCandidates powinno zwracać kopię tablicy, a nie oryginał")
    void getCandidatesShouldReturnDefensiveCopy() {
        String[] first = solver.getCandidates();
        String[] second = solver.getCandidates();

        assertNotSame(first, second);
    }

    @Test
    @DisplayName("Zmiana zwróconej tablicy nie powinna wpływać na stan gry")
    void modifyingReturnedCandidatesShouldNotAffectGame() {
        String[] candidates = solver.getCandidates();
        String original = candidates[0];
        candidates[0] = "zmiana";

        assertEquals(original, solver.getCandidates()[0]);
    }
}