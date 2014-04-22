package pl.dominikw.dxjumper;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import pl.dominikw.dxjumper.framework.Game;
import pl.dominikw.dxjumper.framework.Input.TouchEvent;
import pl.dominikw.dxjumper.framework.gl.Camera2D;
import pl.dominikw.dxjumper.framework.gl.SpriteBatcher;
import pl.dominikw.dxjumper.framework.gl.Texture;
import pl.dominikw.dxjumper.framework.gl.TextureRegion;
import pl.dominikw.dxjumper.framework.impl.GLScreen;
import pl.dominikw.dxjumper.framework.math.OverlapTester;
import pl.dominikw.dxjumper.framework.math.Rectangle;
import pl.dominikw.dxjumper.framework.math.Vector2;

public class Help2 extends GLScreen {
	
	SpriteBatcher batcher;
    Camera2D guiCam;
    Rectangle nextBounds;
    Vector2 touchPoint;
    Texture helpImage;
    TextureRegion helpRegion;    
    
    public Help2(Game game) {
        super(game);
        
        guiCam = new Camera2D(glGraphics, 320, 480);
        nextBounds = new Rectangle(320 - 64, 0, 64, 64);
        touchPoint = new Vector2();
        batcher = new SpriteBatcher(glGraphics, 1);
    }
    
    @Override
    public void resume() {
        helpImage = new Texture(glGame, "help2.png");
        helpRegion = new TextureRegion(helpImage, 0, 0, 320, 480);
    }
    
    @Override
    public void pause() {
        helpImage.dispose();
    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            touchPoint.set(event.x, event.y);
            guiCam.touchToWorld(touchPoint);
            
            if (event.type == TouchEvent.TOUCH_UP) {
            	if (OverlapTester.pointInRectangle(nextBounds, touchPoint)) {
            		Assets.playSound(Assets.clickSound);
                    game.setScreen(new Help3(game));
                    
                    return;
                }
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        GL10 gl = glGraphics.getGL();        
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        guiCam.setViewportAndMatrices();
        
        gl.glEnable(GL10.GL_TEXTURE_2D);
        
        batcher.beginBatch(helpImage);
        batcher.drawSprite(160, 240, 320, 480, helpRegion);
        batcher.endBatch();
        
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        
        batcher.beginBatch(Assets.items);          
        batcher.drawSprite(320 - 32, 32, -64, 64, Assets.arrow);
        batcher.endBatch();
        
        gl.glDisable(GL10.GL_BLEND);
    }

    @Override
    public void dispose() {
    }
}
