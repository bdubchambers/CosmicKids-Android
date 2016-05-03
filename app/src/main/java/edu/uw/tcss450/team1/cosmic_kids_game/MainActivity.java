package edu.uw.tcss450.team1.cosmic_kids_game;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import edu.uw.tcss450.team1.cosmic_kids_game.main.*;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btnSingle:
                //intent = new Intent(this, SingleActivity.class);
                break;
            case R.id.btnMulti:
                //intent = new Intent(this, MultiActivity.class);
                break;
            case R.id.btnOptions:
                intent = new Intent(this, OptionsActivity.class);
                break;
            case R.id.btnExit:
                // TODO exit
                break;
            default:
                break;
        }
    }
}