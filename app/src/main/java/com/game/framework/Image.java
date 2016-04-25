package com.game.framework;

import com.game.framework.Graphics.ImageFormat;

/**
 * Created by Brandon on 4/25/2016.
 */
public interface Image {

    public int getWidth();
    public int getHeight();
    public ImageFormat getFormat();
    public void dispose();

}
