package edu.uw.tcss450.team1.cosmic_kids_game;

import android.content.res.Resources;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import edu.uw.tcss450.team1.cosmic_kids_game.Activities.LoginActivity;
import edu.uw.tcss450.team1.cosmic_kids_game.Activities.MainActivity;

/**
 * Created by Justin on 5/30/2016.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;

    public MainActivityTest() {
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

    public void testLoginOrLogout() {
        // Following method of obtaining resources by user cody @ stackoverflow.com/5216509
        Resources res = getInstrumentation().getTargetContext().getResources();
        String optionsBtnText = res.getString(R.string.options);
        boolean isOnMain = solo.searchButton(optionsBtnText);
        if (isOnMain) {
            solo.clickOnButton(optionsBtnText);
            String changeUserBtnText = res.getString(R.string.change_user);
            boolean isOnOptions = solo.searchButton(changeUserBtnText);
            if (isOnOptions) {
                solo.clickOnButton(changeUserBtnText);
                assertTrue("Redirected to login on logout",
                        solo.searchEditText(res.getString(R.string.enter_username)));
            } else {
                fail("Unable to find logout");
            }
        } else {
            solo.enterText(0, "jb21");
            solo.enterText(1, "Passw0rd");
            solo.clickOnButton(res.getString(R.string.submit));
            assertTrue("Redirected to Main after forced login",
                    solo.searchButton(optionsBtnText));
        }
    }

    public void testOpposite() {
        testLoginOrLogout();
    }
}
