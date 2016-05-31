package edu.uw.tcss450.team1.cosmic_kids_game.HelperCode;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import edu.uw.tcss450.team1.cosmic_kids_game.R;

import static android.widget.Toast.*;

/**
 * Created by Justin on 5/29/2016.
 */
public class General {

    public static SharedPreferences GetPrefs(Context context) {
        return context.getSharedPreferences(context.getString(R.string.LOGIN_PREFS),
                context.MODE_PRIVATE);
    }

    public static void Toast(Context context, String message) {
        makeText(context, message, LENGTH_SHORT).show();
    }


}
