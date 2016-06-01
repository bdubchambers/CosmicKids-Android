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
import android.view.Gravity;
import android.widget.Toast;

import edu.uw.tcss450.team1.cosmic_kids_game.R;

import static android.widget.Toast.*;

public class General {

    public static SharedPreferences GetPrefs(Context context) {
        return context.getSharedPreferences(context.getString(R.string.LOGIN_PREFS),
                context.MODE_PRIVATE);
    }

    public static void Toast(Context context, String message) {
        makeText(context, message, LENGTH_SHORT).show();
    }

    public static void ToastGravity(Context context, String message, int gravity, int xOffset, int yOffset) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        if(toast != null){
            toast.setGravity(gravity, xOffset, yOffset);
            toast.show();
        }
    }


    public static void ToastGravityTop(Context context, String message){

        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        if(toast != null){
            toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 175);
        }

    }
}
