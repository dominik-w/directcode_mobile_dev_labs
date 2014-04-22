/*
 * Dude.java - game hero.
 */
package pl.dominikw.dxjumper;

import pl.dominikw.dxjumper.framework.DynamicGameObject;

public class Dude extends DynamicGameObject {
	
	public static final float DUDE_WIDTH  = 0.8f;
	public static final float DUDE_HEIGHT = 0.8f;
	
    public static final float DUDE_JUMP_VELOCITY = 12;
    public static final float DUDE_MOVE_VELOCITY = 20;
    
    public static final int DUDE_STATE_JUMP = 0;
    public static final int DUDE_STATE_FALL = 1;
    public static final int DUDE_STATE_HIT  = 2;
    
    float stateTime;
    int state;
    
    public Dude(float x, float y) {
    	super(x, y, DUDE_WIDTH, DUDE_HEIGHT);
    	state = DUDE_STATE_FALL;
    	stateTime = 0;
    }
    
    public void update(float deltaTime) {
    	velocity.add(World.gravity.x * deltaTime, World.gravity.y * deltaTime);
    	position.add(velocity.x * deltaTime, velocity.y * deltaTime);
    	bounds.lowerLeft.set(position).sub(bounds.width / 2, bounds.height / 2);
    	
    	// jump
    	if (velocity.y > 0 && state != DUDE_STATE_HIT) {
    		if (state != DUDE_STATE_JUMP) {
    			state = DUDE_STATE_JUMP;
    			stateTime = 0;
    		}
    	}
    	
    	if (velocity.y < 0 && state != DUDE_STATE_HIT) {
    		if (state != DUDE_STATE_FALL) {
    			state = DUDE_STATE_FALL;
    			stateTime = 0;
    		}
    	}
    	
    	if (position.x < 0)
    		position.x = World.WORLD_WIDTH;
    	
    	if (position.x > World.WORLD_WIDTH)
    		position.x = 0;
    	
    	stateTime += deltaTime;
    }
    
    public void hitKiller() {
    	velocity.set(0, 0);
    	state = DUDE_STATE_HIT;
    	stateTime = 0;
    }
    
    public void hitPlatform() {
    	velocity.y = DUDE_JUMP_VELOCITY;
    	state = DUDE_STATE_JUMP;
    	stateTime = 0;
    }
    
    public void hitSpring() {
    	velocity.y = DUDE_JUMP_VELOCITY * 1.5f;
    	state = DUDE_STATE_JUMP;
    	stateTime = 0;
    }
}
