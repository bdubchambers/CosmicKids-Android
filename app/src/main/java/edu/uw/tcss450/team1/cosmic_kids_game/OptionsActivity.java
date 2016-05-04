package edu.uw.tcss450.team1.cosmic_kids_game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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