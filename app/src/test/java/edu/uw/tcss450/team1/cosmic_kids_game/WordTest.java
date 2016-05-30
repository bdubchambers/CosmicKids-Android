package edu.uw.tcss450.team1.cosmic_kids_game;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

import edu.uw.tcss450.team1.cosmic_kids_game.Models.Word;

/**
 * Testing our Model of the Word object.
 */
public class WordTest {
    private static final String WORD_TEXT = "foobar";
    private static final int GRADE_MIN = 1;
    private static final int GRADE_MAX = 6;
    private Word word;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        word = new Word(WORD_TEXT, GRADE_MIN);
    }

    @Test(expected = NullPointerException.class)
    public void testWord() {
        assertNotNull(word);
        word = new Word(null, GRADE_MIN);
        thrown.expect(IllegalArgumentException.class);
        word = new Word("fb", GRADE_MIN);
        thrown.expect(NumberFormatException.class);
        word = new Word(WORD_TEXT, 0);
    }

    @Test
    public void testGetWord() {
        assertEquals(word.getWord(), WORD_TEXT);
    }

    @Test
    public void testGetGrade() {
        assertEquals(word.getGrade(), GRADE_MIN);
    }

    @Test
    public void testGetGradesInt() {
        int[] grades = Word.GetGrades(0);
        assertEquals(grades[0], GRADE_MIN);
        grades = Word.GetGrades(Word.Difficulty.Advanced);
        assertEquals(grades[1], GRADE_MAX);
        thrown.expect(EnumConstantNotPresentException.class);
        Word.GetGrades(-1);
        thrown.expect(EnumConstantNotPresentException.class);
        Word.GetGrades(3);
    }
}
