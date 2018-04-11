/**
 * @Class WordTest
 * @Version 1.0.0
 * @Author Justin Burch
 * @Author Brandon Chambers
 *
 * This class provides a JUnit Test for our Word model.
 */
package edu.uw.tcss450.team1.cosmic_kids_game;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import edu.uw.tcss450.team1.cosmic_kids_game.Models.Word;
import static org.junit.Assert.*;


public class WordTest {
    private static final String WORD_TEXT = "foobar";
    private static final int INDEX_MIN = 0;
    private Word word;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        word = new Word(WORD_TEXT, Word.GRADE_MIN);
    }

    /**
     * Test against improper constructors, including: null words, short words and
     * edge case numbers.
     */
    @Test(expected = NullPointerException.class)
    public void testWord() {
        assertNotNull(word);
        word = new Word(null, Word.GRADE_MIN);
        thrown.expect(IllegalArgumentException.class);
        word = new Word(WORD_TEXT.substring(INDEX_MIN, Word.WORD_LENGTH_MIN - 1), Word.GRADE_MIN);
        thrown.expect(NumberFormatException.class);
        word = new Word(WORD_TEXT, Word.GRADE_MIN - 1);
        thrown.expect(NumberFormatException.class);
        word = new Word(WORD_TEXT, Word.GRADE_MAX + 1);
    }

    /**
     * Verify that the word is properly saved and compared against.
     */
    @Test
    public void testCorrectWord() {
        assertEquals(word.getWord(), WORD_TEXT);
        assertTrue(word.isCorrect(WORD_TEXT));
        assertTrue(word.isCorrect(WORD_TEXT.toLowerCase()));
        assertTrue(word.isCorrect(WORD_TEXT.toUpperCase()));
    }

    /**
     * Verify that the grade is properly saved.
     */
    @Test
    public void testGetGrade() {
        assertEquals(word.getGrade(), Word.GRADE_MIN);
    }

    /**
     * Verify that the static method to return a grade based on difficulty handles the
     * inputs correctly for all parameters of the method.
     */
    @Test
    public void testGetGradesInt() {
        Word.GradeRange range = Word.GetGrades(INDEX_MIN);
        assertEquals(range.getMin(), Word.GRADE_MIN);
        range = Word.GetGrades(Word.Difficulty.Advanced);
        assertEquals(range.getMax(), Word.GRADE_MAX);
        thrown.expect(EnumConstantNotPresentException.class);
        Word.GetGrades(INDEX_MIN - 1);
        thrown.expect(EnumConstantNotPresentException.class);
        Word.GetGrades(Word.Difficulty.values().length);
    }
}
