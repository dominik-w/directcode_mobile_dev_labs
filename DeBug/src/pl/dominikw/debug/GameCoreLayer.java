/*
 * Game core layer.
 */

package pl.dominikw.debug;

import pl.dominikw.debug.R;

import java.util.Random;
import java.util.LinkedList;

import android.content.Context;
import android.util.FloatMath;
import android.view.MotionEvent;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCRotateTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCScene;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor4B;

// import static javax.microedition.khronos.opengles.GL10.GL_COLOR_ARRAY;
// import static javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA;
// import static javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA;

// import org.cocos2d.nodes.CCLabel.TextAlignment;
// import org.cocos2d.opengl.CCTexture2D;
// import org.cocos2d.config.ccConfig;
// import javax.microedition.khronos.opengles.GL10;

public class GameCoreLayer extends CCColorLayer {
	
	protected CCSprite _player;
	protected CCSprite _nextBullet;
	protected LinkedList<CCSprite> _bugs;
	protected LinkedList<CCSprite> _bullets;
	protected int _killedBugs;
	
	// public int score; // with factor
	
	public static CCScene scene() {
		
		CCScene scene = CCScene.node();
		CCColorLayer layer = new GameCoreLayer(ccColor4B.ccc4(248, 248, 248, 255));
		
		scene.addChild(layer);
		
		return scene;
	}
	
	protected GameCoreLayer(ccColor4B color) {
		
		super(color);
		
		this.setIsTouchEnabled(true);
		
		_bugs = new LinkedList<CCSprite>();
		_bullets = new LinkedList<CCSprite>();
		_killedBugs = 0;
		
		// score = 0;
		
		CGSize winSize = CCDirector.sharedDirector().displaySize();
		
		// create the canon sprite
		_player = CCSprite.sprite("gun2.png");
		_player.setPosition(CGPoint.ccp(_player.getContentSize().width / 2.0f, winSize.height / 2.0f));
		
		addChild(_player);
		
		// sound
		Context context = CCDirector.sharedDirector().getActivity();
		SoundEngine.sharedEngine().preloadEffect(context, R.raw.bang);
		
		// background sound
		// SoundEngine.sharedEngine().playSound(context, R.raw.bg_music, true);
		
		// callbacks
		this.schedule("gameLogic", 1.0f);
		this.schedule("update");
	}
	
	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		
		CGPoint location = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
		
		// bullet setup
		CGSize winSize = CCDirector.sharedDirector().displaySize();
		_nextBullet = CCSprite.sprite("bullet.png");
		
		_nextBullet.setPosition(52, winSize.height / 2.0f); // pos
		
		// determine offset
		int dx = (int) (location.x - _nextBullet.getPosition().x);
		int dy = (int) (location.y - _nextBullet.getPosition().y);
		
		// direction control
		if (dx <= 0)
			return true;
		
		_nextBullet.setTag(2);
		
		// determine direction of the bullet
		int real_x = (int) (winSize.width + (_nextBullet.getContentSize().width / 2.0f));
		float ratio = (float) dy / (float) dx;
		int real_y = (int) ((real_x * ratio) + _nextBullet.getPosition().y);
		CGPoint realDest = CGPoint.ccp(real_x, real_y);
		
		// calculate the length of shoot
		int off_real_x = (int) (real_x - _nextBullet.getPosition().x);
		int off_real_y = (int)(real_y - _nextBullet.getPosition().y);
		float length = FloatMath.sqrt((off_real_x * off_real_x) + (off_real_y * off_real_y));
		float velocity = 480.0f / 1.0f; // 480 pixels / 1 sec
		float realMoveDuration = length / velocity;
		
		// move bullet to actual endpoint
		_nextBullet.runAction(CCSequence.actions(
				CCMoveTo.action(realMoveDuration, realDest),
				CCCallFuncN.action(this, "spriteMoveFinish")));
		
		// angle
		double angleRadians = Math.atan((double) off_real_y / (double) off_real_x);
		double angleDegrees = Math.toDegrees(angleRadians);
		double cocosAngle = -1 * angleDegrees; // rev
		double rotationSpeed = 0.5 / Math.PI;
		double rotationDuration = Math.abs(angleRadians * rotationSpeed);
		
		_player.runAction(CCSequence.actions(
				CCRotateTo.action((float)rotationDuration, (float)cocosAngle),
				CCCallFunc.action(this, "shootFinish")));
		
		// play sound
		Context context = CCDirector.sharedDirector().getActivity();
		SoundEngine.sharedEngine().playEffect(context, R.raw.bang);
		
		return true;
	}
	
	public void shootFinish() {
		addChild(_nextBullet);
		_bullets.add(_nextBullet);
	}
	
	public void update(float dt) {
		
		LinkedList<CCSprite> bulletsToDelete = new LinkedList<CCSprite>();
		
		for (CCSprite bullet : _bullets) {
			CGRect bulletRect = CGRect.make(bullet.getPosition().x - (bullet.getContentSize().width / 2.0f),
					bullet.getPosition().y - (bullet.getContentSize().height / 2.0f),
					bullet.getContentSize().width,
					bullet.getContentSize().height);
			
			LinkedList<CCSprite> targetsToDelete = new LinkedList<CCSprite>();
			
			for (CCSprite target : _bugs) {
				CGRect targetRect = CGRect.make(target.getPosition().x - (target.getContentSize().width),
						target.getPosition().y - (target.getContentSize().height),
						target.getContentSize().width,
						target.getContentSize().height);
				
				if (CGRect.intersects(bulletRect, targetRect))
					targetsToDelete.add(target);
			}
			
			for (CCSprite target : targetsToDelete) {
				_bugs.remove(target);
				removeChild(target, true);
			}
			
			if (targetsToDelete.size() > 0)
				bulletsToDelete.add(bullet);
		}
		
		for (CCSprite bullet : bulletsToDelete) {
			_bullets.remove(bullet);
			removeChild(bullet, true);
			
			_killedBugs++;
			
			if (_killedBugs > 45) {
				CCDirector.sharedDirector().replaceScene(GameOverLayer.scene("You Win! Score: " + _killedBugs));
				_killedBugs = 0;
			}
		}
	}
	
	public void gameLogic(float dt) {
		addTarget();
	}
	
	protected void addTarget() {
		Random rand = new Random();
		CCSprite target = CCSprite.sprite("bug.png");
		
		CGSize winSize = CCDirector.sharedDirector().displaySize();
		int min_y = (int) (target.getContentSize().height / 2.0f);
		int max_y = (int) (winSize.height - target.getContentSize().height / 2.0f);
		int range_y = max_y - min_y;
		int actual_y = rand.nextInt(range_y) + min_y;
		
		target.setPosition(winSize.width + (target.getContentSize().width / 2.0f), actual_y);
		addChild(target);
		
		target.setTag(1);
		_bugs.add(target);
		
		// target speed
		int minDuration = 2;
		int maxDuration = 4;
		int rangeDuration = maxDuration - minDuration;
		int actualDuration = rand.nextInt(rangeDuration) + minDuration;
		
		// actions
		CCMoveTo actionMove = CCMoveTo.action(actualDuration, CGPoint.ccp(-target.getContentSize().width / 2.0f, actual_y));
		CCCallFuncN actionMoveDone = CCCallFuncN.action(this, "spriteMoveFinish");
		CCSequence actions = CCSequence.actions(actionMove, actionMoveDone);
		
		target.runAction(actions);
	}
	
	public void spriteMoveFinish(Object sender) {
		
		CCSprite sprite = (CCSprite)sender;
		
		if (sprite.getTag() == 1) {
			_bugs.remove(sprite);
			
			// _killedBugs = 0;
			// CCDirector.sharedDirector().replaceScene(GameOverLayer.scene("You Lose!"));
		} else if (sprite.getTag() == 2)
			_bullets.remove(sprite);
		
		this.removeChild(sprite, true);
	}
	
	/*
	@Override
	public void draw(GL10 gl) {
		super.draw(gl);
		
		showScore(gl);
	}
	
	public void showScore(GL10 gl) {
		
		String str = "Score: " + _killedBugs;
		
		CCTexture2D texture = new CCTexture2D();
		// no need to register Loader for this subordinate texture
		texture.initWithText(str, CGSize.make(100, 30), TextAlignment.LEFT, "DroidSans", 20);
		
		gl.glDisableClientState(GL_COLOR_ARRAY);
		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4f(224.f / 255, 224.f / 255, 244.f / 255, 200.f / 255);
		texture.drawAtPoint(gl, CGPoint.ccp(5, 2));
		
		gl.glBlendFunc(ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST);
		
		// restore default GL state
		gl.glEnableClientState(GL_COLOR_ARRAY);
	}
	*/
}
