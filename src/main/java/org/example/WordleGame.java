package org.example;

import java.util.ArrayList;
import java.util.Arrays;

public class WordleGame {
    String gameName;
    String wordToGuess;
    int guessNumber = 0;
    String[] guesses;

    // Gemini 3.1 Pro generował 15 losowych słów, (Tablica napisana z palca)
    String[] allWords = {
            "kotek",
            "trawa",
            "konto",
            "słowo",
            "drzwi",
            "ekran",
            "pasek",
            "kwiat",
            "lampa",
            "rower",
            "serce",
            "mleko",
            "chleb",
            "miska",
            "zegar"
    };

    String[] candidates;

    WordleGame(String gameName, String wordToGuess) {
        this.gameName = gameName;
        this.wordToGuess = wordToGuess;

        candidates = allWords.clone();
    }

    FeedbackValue[] guessWord(String guess) {
        if (guess.length() != 5) {
            throw new IllegalStateException("Zla dlugosc zgadywanego slowa.");
        }
        if (guessNumber >= 6) {
            throw new IllegalStateException("Przekroczono limit prob.");
        }

        FeedbackValue[] feedback = giveFeedback(wordToGuess, guess);
        applyFeedback(guess, feedback);

        // XD?
        guesses = Arrays.copyOf(guesses, guesses.length + 1);
        guesses[guesses.length - 1] = guess;

        guessNumber++;
        return feedback;
    }

    void applyFeedback(String guess, FeedbackValue[] feedback) {
        ArrayList<String> newCandidatesArrayList = new ArrayList<>();
        for (String candidate : candidates) {
            for  (int i = 0; i < feedback.length; i++) {
                if (feedback[i] == FeedbackValue.GRAY) {
                    continue;
                }
                if (feedback[i] == FeedbackValue.YELLOW) {
                    if (candidate.indexOf(guess.charAt(i)) == -1) {
                        continue;
                    }
                }
                if (feedback[i] == FeedbackValue.GREEN) {
                    if (candidate.charAt(i) != guess.charAt(i)) {
                        continue;
                    }
                }
                newCandidatesArrayList.add(candidate);
            }
        }
        candidates = newCandidatesArrayList.toArray(new String[0]);
    }

    String[] getCandidates() {
        return candidates.clone();
    }

    private FeedbackValue[] giveFeedback(String wordToGuess, String guess) {
        int wordLength = wordToGuess.length();
        FeedbackValue[] feedback = new FeedbackValue[wordLength];

        for (int i = 0; i < wordLength; i++) {
            if (wordToGuess.charAt(i) == guess.charAt(i)) {
                feedback[i] = FeedbackValue.GREEN;
            } else if (wordToGuess.indexOf(guess.charAt(i)) != -1) {
                feedback[i] = FeedbackValue.YELLOW;
            } else {
                feedback[i] = FeedbackValue.GRAY;
            }
        }

        return feedback;
    }
}
