package edu.uw.tcss450.team1.cosmic_kids_game;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class SpellGameActivity extends Activity implements View.OnClickListener {

    //Animates our GIF for background
    AnimationDrawable ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spell_game);
        setTheme(R.style.FullscreenTheme);

        /*Grab ImageView by id and use the animation-list created inside
        animation xml to string together frames of a GIF (Android does
        not provide native GIF support)*/
        ImageView iv = (ImageView) findViewById(R.id.imgBGSpaceGIF02);
        iv.setBackgroundResource(R.drawable.spacegif_02_animation);
        ad = (AnimationDrawable) iv.getBackground();

    }

    /**
     * Starts the AnimationDrawable to string together frames of a GIF
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            ad.start();
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }
}
