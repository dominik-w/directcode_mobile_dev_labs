/*
 * Castle.java
 */
package pl.dominikw.dxjumper;

import pl.dominikw.dxjumper.framework.GameObject;

public class Castle extends GameObject {
	// setup
	public static float CASTLE_WIDTH = 1.7f;
    public static float CASTLE_HEIGHT = 1.7f;

    public Castle(float x, float y) {
        super(x, y, CASTLE_WIDTH, CASTLE_HEIGHT);
    }

}
