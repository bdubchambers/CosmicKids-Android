/**
 * @Class RegisterActivity
 * @Version 1.0.0
 * @Author Justin Burch
 * @Author Brandon Chambers
 *
 * This class provides an Activity that will showcase and handle all of the logic related to
 * registering a user with the MySQL database.
 *
 * This Activity may be changed into a Fragment in the next Phase.
 */
package edu.uw.tcss450.team1.cosmic_kids_game;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import edu.uw.tcss450.team1.cosmic_kids_game.HelperCode.PostAsync;

/**
 * Activity to handle the registration aspect of the application.
 */
public class RegisterActivity extends Activity implements OnClickListener {
    /* Static Variables */
    private static final String REGISTER_PHP_URL =
            "http://cssgate.insttech.washington.edu/~_450btm1/webservices/register.php";

    /* Class-Level Variables */
    private EditText user, pass, pass2;
    private Button mRegister;

    /**
     * Override to allow for text reading while setting listeners for buttons.
     * @param savedInstanceState Carried over from super method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        setTheme(R.style.FullscreenTheme);
        user = (EditText)findViewById(R.id.user_input);
        pass = (EditText)findViewById(R.id.pass_input);
        pass2 = (EditText)findViewById(R.id.pass_verify_input);
        mRegister = (Button)findViewById(R.id.btnRegister);
        mRegister.setOnClickListener(this);
    }

    /**
     * Attempt to register user after passing info contained in fields.
     * @param view Item within Activity that has triggered the event
     */
    @Override
    public void onClick(View view) {
        String username = user.getText().toString();
        String password = pass.getText().toString();
        String verify = pass2.getText().toString();

        if (verifyInputs(username, password, verify)) {
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            PostAsync post = new PostAsync(this, REGISTER_PHP_URL, "Registering", params);
            post.execute();
            try {
                String result = post.get();
                if (result != null) {
                    toastMe(result);
                    if (result.startsWith("Username")) {
                        SharedPreferences sp = getSharedPreferences(getString(R.string.LOGIN_PREFS),
                                MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("username", username);
                        editor.commit();
                        if (!sp.getBoolean("loggedIn", false)) {
                            toastMe("Logging in as " + user);
                            editor.putBoolean("loggedIn", true);
                        }
                        setResult(RESULT_OK);
                        finish();
                    }
                }
            } catch (Exception e) {
                toastMe("Connection Error: " + e.getMessage());
            }
        }
    }

    /**
     * Easy-to-use method of outputting Toast.
     * @param msg Message to be put into Toast
     */
    void toastMe(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Verify the inputs in the fields before sending to php service.
     * @param user Username the user wishes to use
     * @param password Password the user wishes to use
     * @param verify Password re-typed for verification
     * @return True if all basic criteria met, false otherwise
     */
    boolean verifyInputs(String user, String password, String verify) {
        if (user.length() < 3) {
            toastMe("Username should be at least 3 characters long!");
        }
        else if (!password.equals(verify)) {
            toastMe("Passwords do not match!");
            pass.setText("");
            pass2.setText("");
        } else if (password.length() < 6) {
            toastMe("Password must be at least 6 characters long!");
        } else {
            boolean hasUpper = false;
            boolean hasDigit = false;
            for (char c : password.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    hasUpper = true;
                }
                if (Character.isDigit(c)) {
                    hasDigit = true;
                }
            }
            if (!hasUpper) {
                toastMe("Password must contain at least one uppercase!");
            } else if (!hasDigit) {
                toastMe("Password must contain at least one number!");
            } else{
                return true;
            }
        }
        return false;
    }
}
