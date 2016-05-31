package edu.uw.tcss450.team1.cosmic_kids_game.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import edu.uw.tcss450.team1.cosmic_kids_game.HelperCode.General;
import edu.uw.tcss450.team1.cosmic_kids_game.R;


public class EndGameTransitionActivity extends Activity {

    Button btnReturn;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game_transition);
        setTheme(R.style.FullscreenTheme);

        textView = (TextView)findViewById(R.id.tvScore);
    try{
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
            String finalStr = sb.toString();
            General.Toast(this, finalStr);
            textView.setText(finalStr);
        }
    } catch(Exception e) {
        e.printStackTrace();
    }

        btnReturn = (Button)findViewById(R.id.btnRtrnMain);

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EndGameTransitionActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
