package edu.uw.tcss450.team1.cosmic_kids_game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnSingle = (Button)this.findViewById(R.id.btnSingle);
        Button btnMulti = (Button)this.findViewById(R.id.btnMulti);
        Button btnOptions = (Button)this.findViewById(R.id.btnOptions);
        Button btnExit = (Button)this.findViewById(R.id.btnExit);
        btnSingle.setOnClickListener(this);
        btnMulti.setOnClickListener(this);
        btnOptions.setOnClickListener(this);
        btnExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btnSingle:
                //intent = new Intent(this, SingleActivity.class);
                break;
            case R.id.btnMulti:
                //intent = new Intent(this, MultiActivity.class);
                break;
            case R.id.btnOptions:
                intent = new Intent(this, OptionsActivity.class);
                startActivity(intent);
                break;
            case R.id.btnExit:
                // TODO exit
                break;
            default:
                break;
        }
    }
}