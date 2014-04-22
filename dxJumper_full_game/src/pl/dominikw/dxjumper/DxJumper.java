/*
 * dxJumper game
 * Copyright (C) Dominik Wlazlowski <dominik-w@dominik-w.pl>
 */
package pl.dominikw.dxjumper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import pl.dominikw.dxjumper.framework.impl.GLGame;
import pl.dominikw.dxjumper.framework.Screen;

public class DxJumper extends GLGame {
	
	boolean isJustStarted = true;
	
	@Override
	public Screen getStartScreen() {
		return new MainMenuScreen(this);
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);
		if (isJustStarted) {
			Settings.load(getFileIO());
			Assets.load(this);
			isJustStarted = false;
		} else {
			Assets.reload();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (Settings.soundEnabled)
			Assets.music.pause();
	}
}
