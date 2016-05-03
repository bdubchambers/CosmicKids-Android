package edu.uw.tcss450.team1.cosmic_kids_game.main;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import edu.uw.tcss450.team1.cosmic_kids_game.R;

public class MainActivity extends FragmentActivity implements
        MainFragment.OnFragmentInteractionListener,
        OptionsFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener,
        LoginFragment.OnFragmentInteractionListener {

    private static final String LOGIN_PHP_URL = "http://192.168.1.9/webservice/login.php";
    private static final String REGISTER_PHP_URL = "http://192.168.1.9/webservice/register.php";
    //private static final String LOGIN_PHP_URL = "http://www.MYDOMAIN.com/webservice/login.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private ProgressDialog progDiag;
    private JSONParser jsonParser;
    private LoginFragment loginFrag;

    public enum Fragments {
        Main, Options, Login, Register
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            MainFragment firstFragment = new MainFragment();
            firstFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }

    public void onLoginFragmentInteraction(String user, String pass) {
        AttemptLogin login = new AttemptLogin(user, pass);
        login.execute();
    }

    @Override
    public void onFragmentInteraction(Fragments fragment) {
    }

    public void attemptLogin(View view) {
        String user = ((EditText)this.findViewById(R.id.user_input)).getText().toString();
        String pass = ((EditText)this.findViewById(R.id.pass_input)).getText().toString();
        onLoginFragmentInteraction(user, pass);
    }

    public void launchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
    }

    public void onOptionsButtonPressed(View view) {
        OptionsFragment fragment = new OptionsFragment();
        launchFragment(fragment);
    }

    public void onRegisterButtonPressed(View view) {
        RegisterFragment fragment = new RegisterFragment();
        launchFragment(fragment);
    }

    public void onMultiplayerButtonPressed(View view) {
        Boolean isRegistered = false;
        // TODO Change above to a check on login/registered
/*        if (!isRegistered) {
            Toast.makeText(MainActivity.this, "You must register first!", Toast.LENGTH_SHORT).show();
            RegisterFragment fragment = new RegisterFragment();
            launchFragment(fragment);
        }*/
        onLoginButtonPressed(view);
    }

    public void onLoginButtonPressed(View view) {
        loginFrag = new LoginFragment();
        launchFragment(loginFrag);
    }

    public void registerUser(View view) {
        String email = ((EditText)findViewById(R.id.email_input)).getText().toString();
        String user = ((EditText)findViewById(R.id.user_input)).getText().toString();
        String pass = ((EditText)findViewById(R.id.pass_input)).getText().toString();
        String pass2 = ((EditText)findViewById(R.id.pass_verify_input)).getText().toString();
        if (!pass.equals(pass2)) {
            Toast.makeText(MainActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            ((EditText)findViewById(R.id.pass_input)).setText("");
            ((EditText)findViewById(R.id.pass_verify_input)).setText("");
        } else if (pass.length() < 6) {
            Toast.makeText(MainActivity.this, "Password must be at least 6 characters long",
                    Toast.LENGTH_SHORT).show();
        } else {
            try {
                int n = Integer.parseInt(pass);
            } catch (Exception e) {
            }
            boolean hasUpper = false;
            boolean hasDigit = false;
            for (char c : pass.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    hasUpper = true;
                }
                if (Character.isDigit(c)) {
                    hasDigit = true;
                }
            }
            if (!hasUpper) {
                Toast.makeText(MainActivity.this, "Password must contain at least one uppercase",
                        Toast.LENGTH_SHORT).show();
            } else if (!hasDigit) {
                Toast.makeText(MainActivity.this, "Password must contain at least one number",
                        Toast.LENGTH_SHORT).show();
            } else{
                // TODO send registration
/*                Toast.makeText(MainActivity.this, "Registration successful!",
                        Toast.LENGTH_SHORT).show();*/
                CreateUser create = new CreateUser(user, pass);
                create.execute();
            }
        }
    }

    class AttemptLogin extends AsyncTask<String, String, String> {

        String username;
        String password;

        public AttemptLogin(String user, String pass) {
            username = user;
            password = pass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDiag = new ProgressDialog(MainActivity.this);
            progDiag.setMessage("...Attempting Login...");
            progDiag.setIndeterminate(false);
            progDiag.setCancelable(true);
            progDiag.show();
        }

        /**
         * We will save the username from the current/active login credentials
         * using SharedPreferences
         *
         * @param args
         * @return
         */
        @Override
        protected String doInBackground(String... args) {

            int success;

            try {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));

                Log.d("request...", "starting");

                JSONObject jsonObject = jsonParser.makeHttpRequest(LOGIN_PHP_URL, "POST", params);

                Log.d("Login attempt...", jsonObject.toString());

                success = jsonObject.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Login Successful", jsonObject.toString());
                    /**
                     * Saving username data in SharedPrefs
                     */
                    SharedPreferences sp =
                            PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("username", username);
                    editor.commit();
                    /**
                     * Now the username is stored in the SharedPrefs folder after login.
                     * Any class that needs this data can retrieve it programmatically.
                     */
                    //Intent intent = new Intent(LoginActivity.this, ReadScoresActivity.class);
                    finish();
                    //startActivity(intent);
                    return jsonObject.getString(TAG_MESSAGE);
                } else {
                    Log.d("Login Failure", jsonObject.getString(TAG_MESSAGE));
                    return jsonObject.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        // @Override
        protected void onPostExecute(String file_url) {
            progDiag.dismiss();
            if (file_url != null)
                Toast.makeText(MainActivity.this, file_url, Toast.LENGTH_LONG).show();
        }
    }//end of inner Class
    class CreateUser extends AsyncTask<String, String, String> {
        boolean failure = false;

        String username;
        String password;

        public CreateUser(String user, String pass) {
            username = user;
            password = pass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDiag = new ProgressDialog(MainActivity.this);
            progDiag.setMessage("...Creating User...");
            progDiag.setIndeterminate(false);
            progDiag.setCancelable(true);
            progDiag.show();
        }

        @Override
        protected String doInBackground(String... args) {
            int success;
            //TODO: Problems with using 'getText()' below
            //String username = user.getText().toString();
//            String password = pass.getText().toString();

            try {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));

                Log.d("request...", "starting");

                JSONObject jsonObject = jsonParser.makeHttpRequest(REGISTER_PHP_URL, "POST", params);

                Log.d("Registration attempt...", jsonObject.toString());

                success = jsonObject.getInt(TAG_SUCCESS);
                if(success == 1) {
                    Log.d("User Created.", jsonObject.toString());
                    finish();
                    return jsonObject.getString(TAG_MESSAGE);
                } else {
                    Log.d("Registration Failure", jsonObject.getString(TAG_MESSAGE));
                    return jsonObject.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
        // @Override
        protected void onPostExecute(String file_url) {
            progDiag.dismiss();
            if(file_url != null)
                Toast.makeText(MainActivity.this, file_url, Toast.LENGTH_LONG).show();
        }
    }//end of inner Class
}