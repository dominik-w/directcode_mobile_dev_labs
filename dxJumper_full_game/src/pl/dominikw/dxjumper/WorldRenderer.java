package pl.dominikw.dxjumper;

import javax.microedition.khronos.opengles.GL10;

import pl.dominikw.dxjumper.framework.gl.Animation;
import pl.dominikw.dxjumper.framework.gl.Camera2D;
import pl.dominikw.dxjumper.framework.gl.SpriteBatcher;
import pl.dominikw.dxjumper.framework.gl.TextureRegion;
import pl.dominikw.dxjumper.framework.impl.GLGraphics;

public class WorldRenderer {
	
    static final float FRUSTUM_WIDTH = 10;
    static final float FRUSTUM_HEIGHT = 15;
    
    SpriteBatcher batcher;
    GLGraphics glGraphics;
    World world;
    Camera2D cam;
    
    public WorldRenderer(GLGraphics glGraphics, SpriteBatcher batcher, World world) {
        this.glGraphics = glGraphics;
        this.world = world;
        this.cam = new Camera2D(glGraphics, FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        this.batcher = batcher;        
    }
    
    public void render() {
        if (world.dude.position.y > cam.position.y)
        	cam.position.y = world.dude.position.y;
        
        cam.setViewportAndMatrices();
        renderBackground();
        renderObjects();        
    }
    
    public void renderBackground() {
        batcher.beginBatch(Assets.background);
        
        batcher.drawSprite(cam.position.x, cam.position.y,
                           FRUSTUM_WIDTH, FRUSTUM_HEIGHT, 
                           Assets.backgroundRegion);
        batcher.endBatch();
    }
    
    public void renderObjects() {
        GL10 gl = glGraphics.getGL();
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        
        batcher.beginBatch(Assets.items);
        renderBob();
        renderPlatforms();
        renderItems();
        renderSquirrels();
        renderCastle();
        batcher.endBatch();
        gl.glDisable(GL10.GL_BLEND);
    }
    
    private void renderBob() {
        TextureRegion keyFrame;
        switch(world.dude.state) {
        case Dude.DUDE_STATE_FALL:
            keyFrame = Assets.dudeFall.getKeyFrame(world.dude.stateTime, Animation.ANIMATION_LOOPING);
            break;
        case Dude.DUDE_STATE_JUMP:
            keyFrame = Assets.dudeJump.getKeyFrame(world.dude.stateTime, Animation.ANIMATION_LOOPING);
            break;
        case Dude.DUDE_STATE_HIT:
        default:
            keyFrame = Assets.dudeHit;                       
        }
        
        float side = world.dude.velocity.x < 0? -1: 1;        
        batcher.drawSprite(world.dude.position.x, world.dude.position.y, side * 1, 1, keyFrame);        
    }
    
    private void renderPlatforms() {
        int len = world.platforms.size();
        for (int i = 0; i < len; i++) {
            Platform platform = world.platforms.get(i);
            TextureRegion keyFrame = Assets.platform;
            if (platform.state == Platform.PLATFORM_STATE_PULVERIZING) {                
                keyFrame = Assets.brakingPlatform.getKeyFrame(platform.stateTime, Animation.ANIMATION_NONLOOPING);
            }            
                                   
            batcher.drawSprite(platform.position.x, platform.position.y, 2, 0.5f, keyFrame);            
        }
    }
    
    private void renderItems() {
        int len = world.springs.size();
        for (int i = 0; i < len; i++) {
            Spring spring = world.springs.get(i);            
            batcher.drawSprite(spring.position.x, spring.position.y, 1, 1, Assets.spring);
        }
        
        len = world.coins.size();
        for (int i = 0; i < len; i++) {
            Coin coin = world.coins.get(i);
            TextureRegion keyFrame = Assets.coinAnim.getKeyFrame(coin.stateTime, Animation.ANIMATION_LOOPING);
            batcher.drawSprite(coin.position.x, coin.position.y, 1, 1, keyFrame);
        }
    }
    
    private void renderSquirrels() {
        int len = world.killers.size();
        for (int i = 0; i < len; i++) {
            Killer squirrel = world.killers.get(i);
            TextureRegion keyFrame = Assets.killerFly.getKeyFrame(squirrel.stateTime, Animation.ANIMATION_LOOPING);
            float side = squirrel.velocity.x < 0?-1:1;
            batcher.drawSprite(squirrel.position.x, squirrel.position.y, side * 1, 1, keyFrame);
        }
    }
    
    private void renderCastle() {
        Castle castle = world.castle;
        batcher.drawSprite(castle.position.x, castle.position.y, 2, 2, Assets.castle);
    }
}
