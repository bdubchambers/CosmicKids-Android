/**
 * @Class Main Activity
 * @Version 1.0.0
 * @Author Justin Burch
 * @Author Brandon Chambers
 *
 * This class is intended to provide an Activity that will showcase our base menu, which will allow
 * a user to start a game (Single or Online) or to access the Options screen for additional
 * resources.
 *
 * This Activity may be the container to hold fragments related to all non-game screens in the
 * next Phase.
 */

package edu.uw.tcss450.team1.cosmic_kids_game;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

    /**
     * Override to set listeners for buttons.
     * @param savedInstanceState Carried over from super method
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnSingle = (Button)this.findViewById(R.id.btnSingle);
        Button btnMulti = (Button)this.findViewById(R.id.btnMulti);
        Button btnOptions = (Button)this.findViewById(R.id.btnOptions);
        Button btnExit = (Button)this.findViewById(R.id.btnExit);
        btnSingle.setOnClickListener(this);
        btnMulti.setOnClickListener(this);
        btnOptions.setOnClickListener(this);
        btnExit.setOnClickListener(this);

        SharedPreferences sp = getSharedPreferences(getString(R.string.LOGIN_PREFS),
                        MODE_PRIVATE);
        if (!sp.getBoolean("loggedIn", false)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "Hello, " + sp.getString("username", "you") + "!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Identify the source of a click and perform appropriate actions.
     * @param view Item within Activity that has triggered the event
     */
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btnSingle:
                //intent = new Intent(this, SingleActivity.class);
                break;
            case R.id.btnMulti:
                //intent = new Intent(this, MultiActivity.class);
                break;
            case R.id.btnOptions:
                intent = new Intent(this, OptionsActivity.class);
                startActivity(intent);
                break;
            case R.id.btnExit:
                // TODO exit
                break;
            default:
                break;
        }
    }
}