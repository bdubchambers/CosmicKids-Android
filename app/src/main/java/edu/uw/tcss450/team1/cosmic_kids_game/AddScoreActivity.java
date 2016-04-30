package edu.uw.tcss450.team1.cosmic_kids_game;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
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
public class AddScoreActivity extends Activity implements View.OnClickListener {

    private EditText gamename, score;
    private Button mSubmit;

    private ProgressDialog progDiag;

    JSONParser jsonParser = new JSONParser();

    /*TODO:
    Send the location of our php script, addscore.php, which connects us to the mysql db.

    Here is where we decide which 'host' we are using by entering the url into this
    String.  Do we need separate urls for local and remote?  Still researching....

    For local use your ip address, mine is: ipv4=192.168.1.9
    For remote, enter the web address.
    For the UWT INSTTECH shared server retrieve my password first (TODO)
     */
    private static final String ADDSCORE_PHP_URL = "http://192.168.1.9/webservice/addscore.php";

    //private static final String ADDSCORE_PHP_URL = "http://www.MYDOMAIN.com/webservice/addscore.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addscore);

        gamename = (EditText)findViewById(R.id.gamename);
        score = (EditText)findViewById(R.id.score);

        mSubmit = (Button)findViewById(R.id.submit);
        mSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        new PostScore().execute();
    }

    class PostScore extends AsyncTask<String, String, String> {

        //TODO: Investigate .getText() outside of UI thread...in the meantime, declare here:
        String post_gamename = gamename.getText().toString();
        String post_score = score.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDiag = new ProgressDialog(AddScoreActivity.this);
            progDiag.setMessage("...Posting Score...");
            progDiag.setIndeterminate(false);
            progDiag.setCancelable(true);
            progDiag.show();
        }

        @Override
        protected String doInBackground(String... args) {
            int success;
            /**
             * Retrieving the SharePref username data that was saved after
             * successful login in LoginActivity.java under AsyncTask
             */
            SharedPreferences sp =
                    PreferenceManager.getDefaultSharedPreferences(AddScoreActivity.this);
            String post_username = sp.getString("username", "anon");

            try {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("username", post_username));
                params.add(new BasicNameValuePair("gamename", post_gamename));
                params.add(new BasicNameValuePair("score", post_score));

                Log.d("request...", "starting");

                JSONObject jsonObject =
                        jsonParser.makeHttpRequest(ADDSCORE_PHP_URL, "POST", params);
                Log.d("Attempting to add score", jsonObject.toString());
                success = jsonObject.getInt(TAG_SUCCESS);

                if(success == 1) {
                    Log.d("Add Score Success", jsonObject.toString());
                    finish();
                    return jsonObject.getString(TAG_MESSAGE);
                } else {
                    Log.d("Failed to add score!", jsonObject.getString(TAG_MESSAGE));
                    return jsonObject.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            progDiag.dismiss();
            if(file_url != null) {
                Toast.makeText(AddScoreActivity.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }//end of inner Class PostScore
}
