package edu.uw.tcss450.team1.cosmic_kids_game;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import edu.uw.tcss450.team1.cosmic_kids_game.Activities.MainActivity;

/**
 * Created by Justin on 5/30/2016.
 */
public class OptionsActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;

    public OptionsActivityTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        //tearDown() is run after a test case has finished.
        //finishOpenedActivities() will finish all the activities that have been opened during the test execution.
        solo.finishOpenedActivities();
    }
}
