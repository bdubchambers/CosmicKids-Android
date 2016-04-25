package com.game.framework.implementation;

import java.util.List;

import android.view.View.OnTouchListener;

import com.game.framework.Input.TouchEvent;

/**
 * Created by Brandon on 4/25/2016.
 */
public interface TouchHandler extends OnTouchListener {
    public boolean isTouchDown(int pointer);
    public int getTouchX(int pointer);
    public int getTouchY(int pointer);
    public List<TouchEvent> getTouchEvents();
}
