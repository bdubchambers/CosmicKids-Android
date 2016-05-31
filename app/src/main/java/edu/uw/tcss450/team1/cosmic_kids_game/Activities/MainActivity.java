/**
 * @Class Main Activity
 * @Version 1.0.0
 * @Author Justin Burch
 * @Author Brandon Chambers
 * This class is intended to provide an Activity that will showcase our base menu, which will allow
 * a user to start a game (Single or Online) or to access the Options screen for additional
 * resources.
 * This Activity may be the container to hold fragments related to all non-game screens in the
 * next Phase.
 */

package edu.uw.tcss450.team1.cosmic_kids_game.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.uw.tcss450.team1.cosmic_kids_game.HelperCode.General;
import edu.uw.tcss450.team1.cosmic_kids_game.R;

public class MainActivity extends Activity implements View.OnClickListener {

    /*LOG TAG*/
    private static final String TAG = "MAINACTIVITY class";

    /**
     * Override to set listeners for buttons.
     *
     * @param savedInstanceState Carried over from super method
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnSingle = (Button) this.findViewById(R.id.btnSingle);
        Button btnMulti = (Button) this.findViewById(R.id.btnMulti);
        Button btnOptions = (Button) this.findViewById(R.id.btnOptions);
        Button btnExit = (Button) this.findViewById(R.id.btnExit);

        btnSingle.setOnClickListener(this);
        btnMulti.setOnClickListener(this);
        btnOptions.setOnClickListener(this);
        btnExit.setOnClickListener(this);
    }

    private void checkLoggedIn() {
        SharedPreferences sp = General.GetPrefs(this);
        if(!sp.getBoolean(this.getString(R.string.loggedIn), false)) {
            General.Toast(this, "You must be logged in to proceed!");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            General.Toast(this, "Hello, " + sp.getString(this.getString(R.string.username),
                    "you") + "!");
        }
    }

    /**
     * Identify the source of a click and perform appropriate actions.
     *
     * @param view Item within Activity that has triggered the event...intent
     */
    @Override
    public void onClick(View view) {
        Intent intent = null;
        SharedPreferences sp = General.GetPrefs(this);
        switch(view.getId()) {
            case R.id.btnSingle:
                /*
                 * Ideally our app will navigate from either the 'SinglePlayer' or 'MultiPlayer'
                 * buttons in the MainScreen into a unified (as possible) 'GameSelectionScreen'
                 * with context sensitive data displays where necessary.  The multiplayer logic
                 * would be handled transparently, especially since we will not have a server
                 * selection or game lobby screen--there is no need to differentiate between a
                 * local and multiplayer game except for a simple Toast message or progress bar.
                 *
                 * For now we will just launch directly into the Spelling Bee game (via the
                 * SpellGameActivity) after the user clicks 'SinglePlayer' button.
                 */
                intent = new Intent(MainActivity.this, SpellGameActivity.class);
                break;
            case R.id.btnMulti:
                if(!sp.getBoolean("loggedIn", false)) {
                    intent = new Intent(this, LoginActivity.class);
                } else {
                    General.Toast(this, "Sorry.. you have no friends.");
                }
                break;
            case R.id.btnOptions:
                intent = new Intent(this, OptionsActivity.class);
                break;
            case R.id.btnExit:
                intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            default:
                break;
        }
        if(intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLoggedIn();
    }
}