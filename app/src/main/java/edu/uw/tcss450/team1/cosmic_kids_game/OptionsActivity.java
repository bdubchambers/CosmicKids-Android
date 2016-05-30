/**
 * @Class OptionsActivity
 * @Version 1.0.0
 * @Author Justin Burch
 * @Author Brandon Chambers
 *
 * This class provides an Activity that will showcase and handle all of the logic related to
 * the current configuration of the game. It also allows for a user to change users or to
 * register a new user.
 *
 * This Activity may be changed into a Fragment in the next Phase.
 */

package edu.uw.tcss450.team1.cosmic_kids_game;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import edu.uw.tcss450.team1.cosmic_kids_game.HelperCode.GLOBAL;

/**
 * Activity to handle displaying and storing the options for the games and application.
 */
public class OptionsActivity extends Activity implements View.OnClickListener {

    /**
     * Override to set listeners for buttons.
     * @param savedInstanceState Carried over from super method
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Button register = (Button)this.findViewById(R.id.btnRegisterOptions);
        Button pass = (Button)this.findViewById(R.id.btnChangePass);
        Button user = (Button)this.findViewById(R.id.btnChangeUser);
        register.setOnClickListener(this);
        pass.setOnClickListener(this);
        user.setOnClickListener(this);
        setupDifficultySpinner();
    }

    private void setupDifficultySpinner() {
        try {
            final String difficulty = "difficulty";
            SharedPreferences sp = GLOBAL.GetPrefs(this);
            final SharedPreferences.Editor editor = sp.edit();
            int diff = sp.getInt(difficulty, -1);
            if (diff < 0) {
                diff = 1;
                editor.putInt(difficulty, diff);
                editor.apply();
            }
            Spinner spinner = (Spinner) this.findViewById(R.id.difficulty_spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.difficulty_options, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);
            spinner.setSelection(diff);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    editor.putInt(difficulty, position);
                    editor.apply();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    editor.putInt(difficulty, 1);
                    editor.apply();
                }
            });
        } catch (Exception e) {
            GLOBAL.Toast(this, "ERROR: " + e.getMessage());
        }
    }

    /**
     * Identify the source of a click and perform appropriate actions.
     * @param view Item within Activity that has triggered the event
     */
    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btnRegisterOptions:
                intent = new Intent(this, RegisterActivity.class);
                break;
            case R.id.btnChangePass:
                break;
            case R.id.btnChangeUser:
                intent = new Intent(this, LoginActivity.class);
                break;
            default:
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}