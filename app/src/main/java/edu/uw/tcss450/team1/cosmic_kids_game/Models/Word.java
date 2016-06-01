/**
 * @Class Word
 * @Version 1.0.0
 * @Author Justin Burch
 * @Author Brandon Chambers
 *
 * This class provides a model for our Word objects. The model hosts the string literal as well
 * as the grade the string pertains to.
 */

package edu.uw.tcss450.team1.cosmic_kids_game.Models;

public class Word {
    private int grade;
    private String word;

    public enum Difficulty {
        Easy, Normal, Advanced
    }

    public Word(String word, int grade) {
        if (word == null) {
            throw new NullPointerException("Word must not be null");
        } else if (word.length() < 3) {
            throw new IllegalArgumentException("Word should contain at least 3 characters");
        } else {
            this.word = word.toLowerCase();
        }
        if (grade > 0) {
            this.grade = grade;
        } else {
            throw new NumberFormatException("Grade cannot be less than 1");
        }
    }

    public int getPoints() { return this.word.length() * this.grade; }

    public String getWord() {
        return this.word;
    }

    public int getGrade() {
        return this.grade;
    }

    public boolean isCorrect(String guess) {
        return (guess.toLowerCase().equals(this.word));
    }

    public int toDeduct(int currentPoints) {
        return (this.grade > 4 && currentPoints >= this.word.length()) ? this.word.length() : 0;
    }

    public static int[] GetGrades(int difficulty) {
        if (difficulty >= 0 && difficulty < Difficulty.values().length) {
            int[] grades = new int[2];
            switch (difficulty) {
                case 0:
                    grades[0] = 1;
                    grades[1] = 2;
                    break;
                case 1:
                    grades[0] = 3;
                    grades[1] = 4;
                    break;
                default:
                    grades[0] = 5;
                    grades[1] = 6;
                    break;
            }
            return grades;
        } else {
            throw new EnumConstantNotPresentException(Difficulty.class, "");
        }
    }

    public static int[] GetGrades(Difficulty difficulty) {
        return GetGrades(difficulty.ordinal());
    }
}
