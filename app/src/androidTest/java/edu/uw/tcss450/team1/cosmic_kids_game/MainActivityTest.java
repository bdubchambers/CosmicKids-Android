/**
 * @Class MainActivityTest
 * @Version 1.0.0
 * @Author Justin Burch
 * @Author Brandon Chambers
 *
 * Robotium test to make certain the MainActivity properly sends to LoginActivity if the
 * user is not logged in. Tests both cases of a user being logged in, as well as a user not being
 * logged in.
 */
package edu.uw.tcss450.team1.cosmic_kids_game;

import android.content.res.Resources;
import android.test.ActivityInstrumentationTestCase2;
import com.robotium.solo.Solo;
import edu.uw.tcss450.team1.cosmic_kids_game.Activities.MainActivity;

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
        solo.finishOpenedActivities();
    }

    /**
     * Checks if the user is logged in by identifying if the app has not redirected
     * to the LoginActivity upon start. If so, it will force a log out and verify the user is
     * redirected to the login screen. Otherwise, it will attempt to login.
     */
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

    /**
     * Will call the testLoginOrLogout method in order to test the second branch of possibilities.
     */
    public void testOpposite() {
        testLoginOrLogout();
    }
}
