/**
 * @Class EndGameTransitionActivity
 * @Version 1.0.0
 * @author Brandon Chambers
 * @author Justin Burch
 * This is the general transitional results screen that appears upon
 * completion of any mini game inside the app, displaying your score
 * and giving the option to share via many outlets (text, email, etc.)
 * A button allows the user to return to the MainScreen.
 */
package edu.uw.tcss450.team1.cosmic_kids_game.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import edu.uw.tcss450.team1.cosmic_kids_game.R;


public class EndGameTransitionActivity extends Activity {

    Button btnReturn, btnShare;
    TextView tvUser, tvScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game_transition);
        setTheme(R.style.FullscreenTheme);

        tvUser = (TextView) findViewById(R.id.tvUser);
        tvScore = (TextView) findViewById(R.id.tvScore);
        try {
            InputStream in =
                    this.openFileInput(getString(R.string.SCORES_FILE));

            if(in != null) {
                InputStreamReader input = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(input);
                String receiveStr;
                StringBuilder sb = new StringBuilder();

                while((receiveStr = br.readLine()) != null) {
                    sb.append(receiveStr);
                }

                in.close();

                final String finalStr = sb.toString();
                final String[] splitter = finalStr.split(" ; ");
                Log.d("EndGAME***", "User:" + splitter[0] + "Score:" + splitter[1]);
                tvUser.setText(splitter[0]);
                tvScore.setText(splitter[1]);

                btnShare = (Button) findViewById(R.id.btnEmailScore);
                btnShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareScore(splitter[0], splitter[1]);
                    }
                });

                btnReturn = (Button) findViewById(R.id.btnRtrnMain);
                btnReturn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EndGameTransitionActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Uses the user's devices default sharing dialog to pop up with
     * providing numerous methods of communication: multiple email apps,
     * text messaging, add to Web Browser, Android Beam, Bluetooth, and
     * many more (device specific).
     *
     * @param player
     * @param score
     */
    public void shareScore(final String player, final String score) {
        String message = "Player " + player +
                " earned " + score + " points in Cosmic Kids Spelling Bee!";
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(emailIntent,
                "Share " + player + "'s Score"));

    }

}
