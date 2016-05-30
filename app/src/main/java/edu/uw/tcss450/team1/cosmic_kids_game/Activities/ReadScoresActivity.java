/**
 * @Class ReadScoresActivity
 * @Version 1.0.0
 * @Author Justin Burch
 * @Author Brandon Chambers
 *
 * This class provides will provide an Activity that will showcase the scores of a completed
 * game. It is not yet implemented.
 *
 * This Activity may be changed into a Fragment in the next Phase.
 */
package edu.uw.tcss450.team1.cosmic_kids_game.Activities;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uw.tcss450.team1.cosmic_kids_game.HelperCode.JSONParser;
import edu.uw.tcss450.team1.cosmic_kids_game.R;

/**
 * Not yet implemented.
 */
public class ReadScoresActivity extends ListActivity {

    private ProgressDialog progDiag;

    /*TODO:
    Send the location of our php script, SCORES.php, which connects us to the mysql db.

    Here is where we decide which 'host' we are using by entering the url into this
    String.  Do we need separate urls for local and remote?  Still researching....

    For local use your ip address, mine is: ipv4=192.168.1.9
    For remote, enter the web address.
    For the UWT INSTTECH shared server retrieve my password first (TODO)
     */
    private static final String SCORES_PHP_URL =
            "http://192.168.1.9/webservice/comments.php";

    /*
    private static final String COMMENTS_PHP_URL =
            "http://www.MYDOMAIN.com/webservice/SCORES.php";
    */

    /*json tags*/
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_TITLE = "title";
    private static final String TAG_SCORES_SET = "scores";
    private static final String TAG_ENTRY_ID = "entry_id";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_SCORE = "score";

    /* Store all scores in array, then organize them in a large List of HashMaps*/
    private JSONArray mScores = null;
    private ArrayList<HashMap<String, String>> mScoreList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.readscores);
        setTheme(R.style.FullscreenTheme);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new LoadScores().execute();
    }

    public void addScore(View view) {
        Intent intent = new Intent(ReadScoresActivity.this, AddScoreActivity.class);
        startActivity(intent);
    }

    /**
     * Updates latest scores from server/DB with json
     */
    public void updateJSONData() {
        mScoreList = new ArrayList<>();

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = jsonParser.getJSONFromUrl(SCORES_PHP_URL);

        try{
            mScores = jsonObject.getJSONArray(TAG_SCORES_SET);

            for(int i = 0; i < mScores.length(); i++) {
                JSONObject score = mScores.getJSONObject(i);
                String gamename = score.getString(TAG_TITLE);
                String points = score.getString(TAG_SCORES_SET);
                String uname = score.getString(TAG_USERNAME);

                HashMap<String, String> hashMap = new HashMap<>();

                hashMap.put(TAG_TITLE, gamename);
                hashMap.put(TAG_SCORES_SET, points);
                hashMap.put(TAG_USERNAME, uname);

                mScoreList.add(hashMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Now that we have stored the data from server/DB into our
     * List of HashMaps, add it to a ListView
     */
    public void updateList() {

/*        *//* *//*
        ListAdapter listAdapter = new SimpleAdapter(this, mScoreList, R.layout.single_score,
                new String[] {TAG_TITLE, TAG_SCORE, TAG_USERNAME},
                new int[] {R.id.textViewGameName, R.id.textViewScore, R.id.textViewUsername});

        setListAdapter(listAdapter);

        *//*TODO: Do something here when a score is clicked on???? (TODO)*//*
        ListView listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: code here for on item click action (go to the user's profile, etc)
            }
        });*/
    }

    public class LoadScores extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDiag = new ProgressDialog(ReadScoresActivity.this);
            progDiag.setMessage("...Loading Scores...");
            progDiag.setIndeterminate(false);
            progDiag.setCancelable(true);
            progDiag.show();
        }

        @Override
        protected Boolean doInBackground(Void... args) {
            updateJSONData();
            return null;
        }

        @Override
        protected void  onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progDiag.dismiss();
            updateList();

        }
    }
}
