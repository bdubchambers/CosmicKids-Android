package edu.uw.tcss450.team1.cosmic_kids_game;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
 * Activity that handles the logic for the Login screen, including setting listeners on the
 * appropriate buttons and granting access to the text within the EditText views.
 */
public class LoginActivity extends Activity implements OnClickListener {

    /* Static Variables */
    private static final String LOGIN_PHP_URL =
            "http://cssgate.insttech.washington.edu/~_450btm1/webservices/login.php";

    /* Class-Level Variables */
    private EditText user, pass;
    private Button mSubmit, mRegister;


    /**
     * Override to allow for text reading while setting listeners for buttons.
     * @param savedInstanceState Carried over from super method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        user = (EditText) findViewById(R.id.login_user);
        pass = (EditText) findViewById(R.id.login_pass);
        mSubmit = (Button) findViewById(R.id.btnLoginSubmit);
        mRegister = (Button) findViewById(R.id.btnRegisterLogin);

        mSubmit.setOnClickListener(this);
        mRegister.setOnClickListener(this);
    }

    /**
     * Identify the source of a click and perform appropriate actions.
     * @param view Item within Activity that has triggered the event
     */
    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btnLoginSubmit: {
                String username = user.getText().toString();
                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", pass.getText().toString()));
                PostAsync post = new PostAsync(this, LOGIN_PHP_URL, "Attempting Login", params);
                post.execute();
                try {
                    String result = post.get();
                    if (result != null) {
                        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                        if (result.startsWith("Login")) {
                            SharedPreferences sp =
                                    PreferenceManager.getDefaultSharedPreferences(this);
                            Editor editor = sp.edit();
                            editor.putString("username", username);
                            editor.commit();
                            intent = new Intent(this, MainActivity.class);
                            finish();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Connection Error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.btnRegisterLogin: {
                intent = new Intent(this, RegisterActivity.class);
                break;
            }
            default:
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}
