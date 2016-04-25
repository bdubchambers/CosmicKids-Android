package com.game.framework;

/**
 * Created by Brandon on 4/25/2016.
 */
public interface Game {

    public Audio getAudio();
    public Input getInput();
    public FileIO getFileIO();
    public Graphics getGraphics();
    public void setScreen(Screen screen);
    public Screen getCurrentScreen();
    public Screen getInitScreen();


}
