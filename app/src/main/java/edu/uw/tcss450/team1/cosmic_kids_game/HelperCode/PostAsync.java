package edu.uw.tcss450.team1.cosmic_kids_game.HelperCode;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.NameValuePair;
import edu.uw.tcss450.team1.cosmic_kids_game.JSONParser;

/**
 * Created by Justin on 5/3/2016.
 */
public class PostAsync extends AsyncTask<String, String, String> {
    /* Static Variables */
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    /* Class-Level Variables */
    private ProgressDialog progDiag;
    private JSONParser jsonParser = new JSONParser();
    private Context activity;
    private String dialogMessage;
    private String connectionString;
    private ArrayList<NameValuePair> data;

    public PostAsync(Context context, String url, String dialogMsg, ArrayList<NameValuePair> args) {
        activity = context;
        dialogMessage = dialogMsg;
        connectionString = url;
        data = args;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progDiag = new ProgressDialog(activity);
        progDiag.setMessage(dialogMessage);
        progDiag.setIndeterminate(false);
        progDiag.setCancelable(true);
        progDiag.show();
    }

    @Override
    protected String doInBackground(String... args) {
        int result;
        String returnValue = null;
        try {
            Log.d("request...", "starting");

            JSONObject jsonObject =
                    jsonParser.makeHttpRequest(connectionString, "POST", data);
            Log.d("Attempting to post", jsonObject.toString());
            result = jsonObject.getInt(TAG_SUCCESS);
            returnValue = jsonObject.getString(TAG_MESSAGE);
            if(result == 1) {
                Log.d("Post Success", jsonObject.toString());
                //finish(); // ??
            } else {
                Log.d("Post Failure", returnValue);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            return returnValue;
        }
    }

    protected void onPostExecute(String file_url) {
        progDiag.dismiss();
        if(file_url != null) {
            Toast.makeText(activity, file_url, Toast.LENGTH_LONG).show();
        }
    }
}
