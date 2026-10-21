package org.example;

import java.util.ArrayList;
import java.util.Arrays;

public class WordleGame {
    private String gameName;
    private String wordToGuess;
    private int guessNumber = 0;
    private String[] guesses = new String[0];
    private WordleSolver solver;

    public WordleGame(String gameName, String wordToGuess, WordleSolver solver) {
        this.gameName = gameName;
        this.wordToGuess = wordToGuess;
        this.solver = solver;
    }

    public FeedbackValue[] guessWord(String guess) {
        if (guess == null) {
            throw new NullPointerException("Slowo nie moze byc nullem");
        }
        if (guess.length() != Constants.WORD_SIZE) {
            throw new IllegalArgumentException("Zla dlugosc zgadywanego slowa.");
        }
        if (guessNumber >= Constants.MAX_TRIES) {
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