/**
 * @Class AddScoreActivity
 * @Version 1.0.0
 * @Author Justin Burch
 * @Author Brandon Chambers
 *
 * This class will be a Helper class that will send the scores of a completed game to the MySQL
 * database to be stored for later use. It is not yet implemented, and therefore temporarily
 * an Activity while we test features in a visual environment.
 *
 * This Activity will be a standard class in the next Phase.
 */
package edu.uw.tcss450.team1.cosmic_kids_game.Activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import edu.uw.tcss450.team1.cosmic_kids_game.HelperCode.General;
import edu.uw.tcss450.team1.cosmic_kids_game.HelperCode.PostAsync;
import edu.uw.tcss450.team1.cosmic_kids_game.R;

/**
 * Activity that will add a score to the database,
 * ultimately allowing for the sharing of the score.
 */
public class AddScoreActivity extends Activity implements View.OnClickListener {
    /* Static Variables */
    private static final String ADDSCORE_PHP_URL =
            "http://cssgate.insttech.washington.edu/~_450btm1/webservices/addscore.php";

    /* Class-Level Variables */
    private TextView gameName, score;
    private Button mSubmit;

    /**
     * Override to allow for text reading while setting listeners for buttons.
     * @param savedInstanceState Carried over from super method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addscore);
        gameName = (TextView)findViewById(R.id.gamename);
        score = (TextView)findViewById(R.id.score);
        mSubmit = (Button)findViewById(R.id.btnSubmitScore);
        mSubmit.setOnClickListener(this);
    }

    /**
     * Add the score to the database.
     * @param view Item within Activity that has triggered the event
     */
    @Override
    public void onClick(View view) {
        SharedPreferences sp = General.GetPrefs(this);
        String username = sp.getString(view.getContext().getString(R.string.username), "Guest");
        String post_gameName = gameName.getText().toString();
        String post_score = score.getText().toString();

        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("gameName", post_gameName));
        params.add(new BasicNameValuePair("score", post_score));
        PostAsync post = new PostAsync(this, ADDSCORE_PHP_URL, "Posting Score", params);
        post.execute();
        try {
            String result = post.get();
            if (result != null) {
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Connection Error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
