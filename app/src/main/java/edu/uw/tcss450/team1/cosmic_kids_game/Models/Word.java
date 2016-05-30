package edu.uw.tcss450.team1.cosmic_kids_game.Models;

/**
 * Created by Justin on 5/29/2016.
 */
public class Word {
    private int grade;
    private String word;

    public enum Difficulty {
        Easy, Normal, Advanced
    }

    public Word(String word, int grade) {
        this.word = word;
        this.grade = grade;
    }

    public String getWord() {
        return this.word;
    }

    public int getGrade() {
        return this.grade;
    }

    public static int[] GetGrades(int difficulty) {
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
    }

    public static int[] GetGrades(Difficulty difficulty) {
        return GetGrades(difficulty.ordinal());
    }
}
