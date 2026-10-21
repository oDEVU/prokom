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

public class WordleSolverTest {

    private static final String TARGET = "słowo";

    private WordleSolver solver;

    @BeforeEach
    void setUp() {
        solver = new WordleSolver();
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

    @Test
    @DisplayName("Nowy obiekt klasy powinien zaczynać z 15 kandydatami")
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
