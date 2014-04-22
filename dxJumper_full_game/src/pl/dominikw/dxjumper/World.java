/*
 * World.java - the game world.
 */
package pl.dominikw.dxjumper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.dominikw.dxjumper.framework.math.OverlapTester;
import pl.dominikw.dxjumper.framework.math.Vector2;

public class World {
	
    public interface WorldListener {
        public void jump();

        public void highJump();

        public void hit();

        public void coin();
    }

    public static final float WORLD_WIDTH = 10;
    public static final float WORLD_HEIGHT = 15 * 20;
    
    public static final int WORLD_STATE_RUNNING = 0;
    public static final int WORLD_STATE_NEXT_LEVEL = 1;
    public static final int WORLD_STATE_GAME_OVER = 2;
    
    public static final Vector2 gravity = new Vector2(0, -12);

    public final Dude dude;
    public final List<Platform> platforms;
    public final List<Spring> springs;
    public final List<Killer> killers;
    public final List<Coin> coins;
    public Castle castle;
    public final WorldListener listener;
    public final Random rand;

    public float heightSoFar;
    public int score;
    public int state;

    public World(WorldListener listener) {
        
    	this.dude = new Dude(5, 1);
        
        this.platforms = new ArrayList<Platform>();
        this.springs = new ArrayList<Spring>();
        this.killers = new ArrayList<Killer>();
        this.coins = new ArrayList<Coin>();
        this.listener = listener;
        rand = new Random();
        generateLevel();

        this.heightSoFar = 0;
        this.score = 0;
        this.state = WORLD_STATE_RUNNING;
    }

	private void generateLevel() {
		// calculations
	    float y = Platform.PLATFORM_HEIGHT / 2;
	    float maxJumpHeight = Dude.DUDE_JUMP_VELOCITY * Dude.DUDE_JUMP_VELOCITY / (2 * -gravity.y);
	    
	    while (y < WORLD_HEIGHT - WORLD_WIDTH / 2) {
	        int type = rand.nextFloat() > 0.8f ? Platform.PLATFORM_TYPE_MOVING : Platform.PLATFORM_TYPE_STATIC;
	        
	        float x = rand.nextFloat()
	                * (WORLD_WIDTH - Platform.PLATFORM_WIDTH)
	                + Platform.PLATFORM_WIDTH / 2;
	
	        Platform platform = new Platform(type, x, y);
	        platforms.add(platform);
	
	        if (rand.nextFloat() > 0.9f && type != Platform.PLATFORM_TYPE_MOVING) {
	            Spring spring = new Spring(platform.position.x, 
	                    platform.position.y + Platform.PLATFORM_HEIGHT / 2
	                      + Spring.SPRING_HEIGHT / 2);
	            springs.add(spring);
	        }
	
	        if (y > WORLD_HEIGHT / 3 && rand.nextFloat() > 0.8f) {
	            Killer killer = new Killer(platform.position.x
	                    + rand.nextFloat(), platform.position.y
	                    + Killer.KILLER_HEIGHT + rand.nextFloat() * 2);
	            killers.add(killer);
	        }
	
	        if (rand.nextFloat() > 0.6f) {
	            Coin coin = new Coin(platform.position.x + rand.nextFloat(),
	                    platform.position.y + Coin.COIN_HEIGHT
	                            + rand.nextFloat() * 3);
	            coins.add(coin);
	        }
	
	        y += (maxJumpHeight - 0.5f);
	        y -= rand.nextFloat() * (maxJumpHeight / 3);
	    }
	
	    castle = new Castle(WORLD_WIDTH / 2, y);
	}
	
	public void update(float deltaTime, float accelX) {
		updateBob(deltaTime, accelX);
		updatePlatforms(deltaTime);
		updateKillers(deltaTime);
		updateCoins(deltaTime);
		if (dude.state != Dude.DUDE_STATE_HIT)
			checkCollisions();
		checkGameOver();
	}
	
	private void updateBob(float deltaTime, float accelX) {
		if (dude.state != Dude.DUDE_STATE_HIT && dude.position.y <= 0.5f)
			dude.hitPlatform();
		if (dude.state != Dude.DUDE_STATE_HIT)
			dude.velocity.x = -accelX / 10 * Dude.DUDE_MOVE_VELOCITY;
		dude.update(deltaTime);
		heightSoFar = Math.max(dude.position.y, heightSoFar);
	}
	
	private void updatePlatforms(float deltaTime) {
		int len = platforms.size();
		for (int i = 0; i < len; i++) {
			Platform platform = platforms.get(i);
			platform.update(deltaTime);
			
			if (platform.state == Platform.PLATFORM_STATE_PULVERIZING
					&& platform.stateTime > Platform.PLATFORM_PULVERIZE_TIME) {
				platforms.remove(platform);
				len = platforms.size();
			}
		}
	}
	
	private void updateKillers(float deltaTime) {
		int len = killers.size();
		for (int i = 0; i < len; i++) {
			Killer killer = killers.get(i);
			killer.update(deltaTime);
		}
	}
	
	private void updateCoins(float deltaTime) {
		int len = coins.size();
		for (int i = 0; i < len; i++) {
			Coin coin = coins.get(i);
			coin.update(deltaTime);
		}
	}
	
	private void checkCollisions() {
		checkPlatformCollisions();
		checkKillerCollisions();
		checkItemCollisions();
		checkCastleCollisions();
	}
	
	private void checkPlatformCollisions() {
		
		if (dude.velocity.y > 0)
			return;
		
		int len = platforms.size();
	    for (int i = 0; i < len; i++) {
	        Platform platform = platforms.get(i);
	        if (dude.position.y > platform.position.y) {
	            if (OverlapTester.overlapRectangles(dude.bounds, platform.bounds)) {
	            	dude.hitPlatform();
	                listener.jump();
	                if (rand.nextFloat() > 0.5f) {
	                    platform.pulverize();
	                }
	                break;
	            }
	        }
	    }
	}
	
	private void checkKillerCollisions() {
	    int len = killers.size();
	    for (int i = 0; i < len; i++) {
	        Killer killer = killers.get(i);
	        if (OverlapTester.overlapRectangles(killer.bounds, dude.bounds)) {
	            dude.hitKiller();
	            listener.hit();
	        }
	    }
	}
	
	private void checkItemCollisions() {
	    int len = coins.size();
	    for (int i = 0; i < len; i++) {
	        Coin coin = coins.get(i);
	        if (OverlapTester.overlapRectangles(dude.bounds, coin.bounds)) {
	            coins.remove(coin);
	            len = coins.size();
	            listener.coin();
	            score += Coin.COIN_SCORE;
	        }
	    }
	
	    if (dude.velocity.y > 0)
	        return;
	
	    len = springs.size();
	    for (int i = 0; i < len; i++) {
	        Spring spring = springs.get(i);
	        if (dude.position.y > spring.position.y) {
	            if (OverlapTester.overlapRectangles(dude.bounds, spring.bounds)) {
	                dude.hitSpring();
	                listener.highJump();
	            }
	        }
	    }
	}
	
	private void checkCastleCollisions() {
	    if (OverlapTester.overlapRectangles(castle.bounds, dude.bounds)) {
	        state = WORLD_STATE_NEXT_LEVEL;
	    }
	}

    private void checkGameOver() {
        if (heightSoFar - 7.5f > dude.position.y) {
            state = WORLD_STATE_GAME_OVER;
        }
    }
    
}
