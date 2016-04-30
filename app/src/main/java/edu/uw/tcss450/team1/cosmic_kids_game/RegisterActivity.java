package edu.uw.tcss450.team1.cosmic_kids_game;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
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
public class RegisterActivity extends Activity implements OnClickListener {

    private EditText user, pass;
    private Button mRegister;

    private ProgressDialog progDiag;

    JSONParser jsonParser = new JSONParser();

    /*TODO:
    Send the location of our php script, register.php, which connects us to the mysql db.

    Here is where we decide which 'host' we are using by entering the url into this
    String.  Do we need separate urls for local and remote?  Still researching....

    For local use your ip address, mine is: ipv4=192.168.1.9
    For remote, enter the web address.
    For the UWT INSTTECH shared server retrieve my password first (TODO)
     */
    private static final String REGISTER_PHP_URL = "http://192.168.1.9/webservice/register.php";

    //private static final String REGISTER_PHP_URL = "http://www.MYDOMAIN.com/webservice/register.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        user = (EditText)findViewById(R.id.username);
        pass = (EditText)findViewById(R.id.password);

        mRegister = (Button)findViewById(R.id.btnRegister);
        mRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
       new CreateUser().execute();
    }

    class CreateUser extends AsyncTask<String, String, String> {
        boolean failure = false;

        String username = user.getText().toString();
        String password = pass.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDiag = new ProgressDialog(RegisterActivity.this);
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
                Toast.makeText(RegisterActivity.this, file_url, Toast.LENGTH_LONG).show();
        }
    }//end of inner Class

}
