/*
 * Game over layer.
 */

package pl.dominikw.debug;

import android.view.MotionEvent;

import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCDelayTime;

public class GameOverLayer extends CCColorLayer {
	
	protected CCLabel gameLabel;
	
	public static CCScene scene(String message) {
		
		CCScene scene = CCScene.node();
		
		GameOverLayer layer = new GameOverLayer(ccColor4B.ccc4(255, 255, 255, 255));
		layer.getLabel().setString(message);
		
		scene.addChild(layer);
		
		return scene;
	}
	
	public CCLabel getLabel() {
		return gameLabel;
	}
	
	protected GameOverLayer(ccColor4B color) {
		
		super(color);
		
		this.setIsTouchEnabled(true);
		
		CGSize winSize = CCDirector.sharedDirector().displaySize();
		
		gameLabel = CCLabel.makeLabel("Game over!", "DeBug - The Game", 28);
		gameLabel.setColor(ccColor3B.ccBLACK);
		gameLabel.setPosition(winSize.width / 2.0f, winSize.height / 2.0f);
		addChild(gameLabel);
		
		this.runAction(CCSequence.actions(CCDelayTime.action(2.8f), CCCallFunc.action(this, "gameOverProc")));
	}
	
	public void gameOverProc() {
		CCDirector.sharedDirector().replaceScene(GameCoreLayer.scene());
	}
	
	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		gameOverProc();
		
		return true;
	}
}
