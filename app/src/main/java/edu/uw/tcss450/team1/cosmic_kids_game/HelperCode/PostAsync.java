/**
 * @Class PostAsync
 * @Version 1.0.0
 * @Author Justin Burch
 * @Author Brandon Chambers
 *
 * This class is intended to provide a base Helper Class to handle all asynchronous POST calls
 * to our CSSGate-hosted PHP/MySQL services.
 */
package edu.uw.tcss450.team1.cosmic_kids_game.HelperCode;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.NameValuePair;
import edu.uw.tcss450.team1.cosmic_kids_game.JSONParser;

/**
 * Asynchronous class to handle all POST calls to php server.
 */
public class PostAsync extends AsyncTask<Void, Void, String> {
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

    /**
     * Overridden constructor to take arguments we will use.
     * @param context Used to identify where to show dialog and toast calls
     * @param url URL of the PHP file to POST to
     * @param dialogMsg Message we want to show while handling POST call
     * @param args Arguments to be passed to the JSONParser
     */
    public PostAsync(Context context, String url, String dialogMsg, ArrayList<NameValuePair> args) {
        activity = context;
        dialogMessage = dialogMsg;
        connectionString = url;
        data = args;
    }

    /**
     * Start a dialog any time we initiate this task.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progDiag = new ProgressDialog(activity);
        progDiag.setMessage(dialogMessage);
        progDiag.setIndeterminate(false);
        progDiag.setCancelable(true);
        progDiag.show();
    }

    /**
     * Background POST process that will return the JSON Object's message supplied by PHP service.
     * @param v Passing all data through the constructor.
     * @return Message associated with the JSON Object.
     */
    @Override
    protected String doInBackground(Void... v) {
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
            progDiag.dismiss();
            return returnValue;
        }
    }
}
