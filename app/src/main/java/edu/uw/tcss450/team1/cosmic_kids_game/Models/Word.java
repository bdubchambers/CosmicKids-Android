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

/**
 * Object to hold data related to the Spelling Bee.
 */
public class Word {
    public static final int WORD_LENGTH_MIN = 3;
    public static final int GRADE_MIN = 1;
    public static final int GRADE_MAX = 6;

    private int grade;
    private String word;

    /**
     * Possible difficulties, mirroring the Spinner values of the Options menu.
     */
    public enum Difficulty {
        Easy, Normal, Advanced
    }

    private Word() {}

    /**
     * Creates a Word object so long as there is a valid word and a valid grade.
     * @param word String literal to be saved
     * @param grade Grade value associated with the word
     */
    public Word(String word, int grade) {
        if (word == null) {
            throw new NullPointerException("Word must not be null");
        } else if (word.length() < WORD_LENGTH_MIN) {
            throw new IllegalArgumentException("Word should contain at least " + WORD_LENGTH_MIN +
                    " characters");
        } else {
            this.word = word;
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
        return guess.toLowerCase().equals(this.word.toLowerCase());
    }

    /**
     * Returns the amount of points to deduct for an incorrect guess - limited to grades 5+.
     * @param currentPoints The current points in the game, prevents negative scores
     * @return The value to deduct, based on grade and current points
     */
    public int toDeduct(int currentPoints) {
        return (this.grade > 4 && currentPoints >= this.word.length()) ? this.word.length() : 0;
    }

    /**
     * Return a range of grades based on the difficulty being passed.
     * @param difficulty Ordinal value of the Difficulty enumeration
     * @return Range of grades as an Object with a min and a max
     */
    public static GradeRange GetGrades(int difficulty) {
        if (difficulty >= 0 && difficulty < Difficulty.values().length) {
            int min = GRADE_MIN;
            int max = GRADE_MAX;

            switch (difficulty) {
                case 0:
                    max = 2;
                    break;
                case 1:
                    min = 3;
                    max = 4;
                    break;
                default:
                    min = 5;
                    break;
            }
            return new Word().new GradeRange(min, max);
        } else {
            throw new EnumConstantNotPresentException(Difficulty.class, "Invalid enum");
        }
    }

    public static GradeRange GetGrades(Difficulty difficulty) {
        return GetGrades(difficulty.ordinal());
    }

    /**
     * Represent a minimum and maximum grade.
     */
    public class GradeRange {
        private int min;
        private int max;

        /**
         * Allow only the parent class to construct to prevent breaking conditions.
         * @param min Minimum grade to be allowed
         * @param max Maximum grade to be allowed
         */
        private GradeRange(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public int getMin() {
            return this.min;
        }

        public int getMax() {
            return this.max;
        }
    }
}
