package pl.dominikw.dxjumper;

import pl.dominikw.dxjumper.framework.DynamicGameObject;

public class Killer extends DynamicGameObject {
	
    public static final float KILLER_WIDTH = 1;
    public static final float KILLER_HEIGHT = 0.6f;
    public static final float KILLER_VELOCITY = 2.9f;
    
    float stateTime = 0;
    
    public Killer(float x, float y) {
        super(x, y, KILLER_WIDTH, KILLER_HEIGHT);
        velocity.set(KILLER_VELOCITY, 0);
    }
    
    public void update(float deltaTime) {
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        bounds.lowerLeft.set(position).sub(KILLER_WIDTH / 2, KILLER_HEIGHT / 2);
        
        if (position.x < KILLER_WIDTH / 2 ) {
            position.x = KILLER_WIDTH / 2;
            velocity.x = KILLER_VELOCITY;
        }
        
        if (position.x > World.WORLD_WIDTH - KILLER_WIDTH / 2) {
            position.x = World.WORLD_WIDTH - KILLER_WIDTH / 2;
            velocity.x = -KILLER_VELOCITY;
        }
        
        stateTime += deltaTime;
    }
}
