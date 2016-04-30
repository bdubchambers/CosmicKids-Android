package edu.uw.tcss450.team1.cosmic_kids_game;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by Brandon on 4/28/2016.
 */
public class LoginActivity extends Activity implements OnClickListener {

    private EditText user, pass;
    private Button mSubmit, mRegister;

    private ProgressDialog progDiag;

    JSONParser jsonParser = new JSONParser();

    /*TODO:
    Send the location of our php script, login.php, which connects us to the mysql db.

    Here is where we decide which 'host' we are using by entering the url into this
    String.  Do we need separate urls for local and remote?  Still researching....

    For local use your ip address, mine is: ipv4=192.168.1.9
    For remote, enter the web address.
    For the UWT INSTTECH shared server retrieve my password first (TODO)
     */
    private static final String LOGIN_PHP_URL = "http://192.168.1.9/webservice/login.php";

    //private static final String LOGIN_PHP_URL = "http://www.MYDOMAIN.com/webservice/login.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);
        mSubmit = (Button) findViewById(R.id.login);
        mRegister = (Button) findViewById(R.id.register);

        mSubmit.setOnClickListener(this);
        mRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                new AttemptLogin().execute();
                break;
            case R.id.register:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    class AttemptLogin extends AsyncTask<String, String, String> {

        //TODO: had to put this here:
        String username = user.getText().toString();
        String password = pass.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDiag = new ProgressDialog(LoginActivity.this);
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
                            PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    Editor editor = sp.edit();
                    editor.putString("username", username);
                    editor.commit();
                    /**
                     * Now the username is stored in the SharedPrefs folder after login.
                     * Any class that needs this data can retrieve it programmatically.
                     */
                    Intent intent = new Intent(LoginActivity.this, ReadScoresActivity.class);
                    finish();
                    startActivity(intent);
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
                Toast.makeText(LoginActivity.this, file_url, Toast.LENGTH_LONG).show();
        }
    }//end of inner Class

}
