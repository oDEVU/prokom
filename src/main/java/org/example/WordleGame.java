package org.example;

import java.util.ArrayList;
import java.util.Arrays;

public class WordleGame {
    private String gameName;
    private String wordToGuess;
    private int guessNumber = 0;
    private String[] guesses = new String[0];
    WordleSolver solver;

    public WordleGame(String gameName, String wordToGuess, WordleSolver solver) {
        this.gameName = gameName;
        this.wordToGuess = wordToGuess;
        this.solver = solver;
    }

    public FeedbackValue[] guessWord(String guess) {
        if (guess.length() != 5) {
            throw new IllegalArgumentException("Zla dlugosc zgadywanego slowa.");
        }
        if (guessNumber >= 6) {
            throw new IllegalStateException("Przekroczono limit prob.");
        }

        FeedbackValue[] feedback = solver.giveFeedback(wordToGuess, guess);
        solver.applyFeedback(guess, feedback);

        guesses = Arrays.copyOf(guesses, guesses.length + 1);
        guesses[guesses.length - 1] = guess;

        guessNumber++;
        return feedback;
    }
}