package org.example;

import java.util.ArrayList;

public class WordleSolver {
    // Gemini 3.1 Pro generował 15 losowych słów, (Tablica napisana z palca)
    private String[] allWords = {
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

    private String[] candidates;

    WordleSolver() {
        candidates = allWords.clone();
    }

    public void applyFeedback(String guess, FeedbackValue[] feedback) {
        ArrayList<String> newCandidatesArrayList = new ArrayList<>();
        for (String candidate : candidates) {
            boolean matches = true;
            for (int i = 0; i < feedback.length && matches; i++) {
                if (feedback[i] == FeedbackValue.YELLOW) {
                    if (candidate.indexOf(guess.charAt(i)) == -1
                            || candidate.charAt(i) == guess.charAt(i)) {
                        matches = false;
                    }
                } else if (feedback[i] == FeedbackValue.GREEN) {
                    if (candidate.charAt(i) != guess.charAt(i)) {
                        matches = false;
                    }
                } else if (candidate.indexOf(guess.charAt(i)) != -1) {
                    matches = false;
                }
            }

            if (matches) {
                newCandidatesArrayList.add(candidate);
            }
        }
        candidates = newCandidatesArrayList.toArray(new String[0]);
    }

    public String[] getCandidates() {
        return candidates.clone();
    }

    public FeedbackValue[] giveFeedback(String wordToGuess, String guess) {
        int wordLength = wordToGuess.length();
        FeedbackValue[] feedback = new FeedbackValue[wordLength];

        // liczenie występowania liter dla poprawnego dla logiki wordle obslugiwania zoltych?
        // np. slowo: kotek, guess: kolor, powinno byc: GG---, jest: GG-Y-
        // jeżeli tak, odkomentować test excessRepeatedLetterShouldBeGray()

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
