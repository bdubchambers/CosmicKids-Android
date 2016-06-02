/**
 * @Class General
 * @Version 1.0.0
 * @Author Justin Burch
 * @Author Brandon Chambers
 *
 * This class provides a collection of static helper methods that can be used universally.
 */
package edu.uw.tcss450.team1.cosmic_kids_game.HelperCode;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.Toast;

import edu.uw.tcss450.team1.cosmic_kids_game.R;

import static android.widget.Toast.*;

public class General {

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(context.getString(R.string.LOGIN_PREFS),
                context.MODE_PRIVATE);
    }

    public static void toast(Context context, String message) {
        makeText(context, message, LENGTH_SHORT).show();
    }

    public static void toastGravity(Context context, String message,
                                    int gravity, int xOffset, int yOffset) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        if(toast != null){
            toast.setGravity(gravity, xOffset, yOffset);
            toast.show();
        }
    }

    public static void toastTop(Context context, String message) {
        toastGravity(context, message, Gravity.TOP|Gravity.CENTER, 0, 175);
    }

    public static String getUsername(Context context) {
        SharedPreferences sp = getPrefs(context);
        return getUsername(context, sp);
    }

    public static String getUsername(Context context, SharedPreferences sp) {
        return sp.getString(context.getResources().getString(R.string.username), "Guest");
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences sp = getPrefs(context);
        return isLoggedIn(context, sp);
    }

    public static boolean isLoggedIn(Context context, SharedPreferences sp) {
        return sp.getBoolean(context.getResources().getString(R.string.loggedIn), false);
    }
}