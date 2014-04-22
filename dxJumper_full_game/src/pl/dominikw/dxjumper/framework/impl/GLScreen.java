package pl.dominikw.dxjumper.framework.impl;

import pl.dominikw.dxjumper.framework.Game;
import pl.dominikw.dxjumper.framework.Screen;

public abstract class GLScreen extends Screen {
	
    protected final GLGraphics glGraphics;
    protected final GLGame glGame;
    
    public GLScreen(Game game) {
        super(game);
        glGame = (GLGame)game;
        glGraphics = ((GLGame)game).getGLGraphics();
    }
}
