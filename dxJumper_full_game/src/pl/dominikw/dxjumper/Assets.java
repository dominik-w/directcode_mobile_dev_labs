/*
 * Game assets.
 */
package pl.dominikw.dxjumper;

import pl.dominikw.dxjumper.framework.Music;
import pl.dominikw.dxjumper.framework.Sound;
import pl.dominikw.dxjumper.framework.gl.Animation;
import pl.dominikw.dxjumper.framework.gl.Font;
import pl.dominikw.dxjumper.framework.gl.Texture;
import pl.dominikw.dxjumper.framework.gl.TextureRegion;
import pl.dominikw.dxjumper.framework.impl.GLGame;

public class Assets {
	
	public static Texture background;
	public static TextureRegion backgroundRegion;
	
	public static Texture items;
	
	public static TextureRegion mainMenu;
	public static TextureRegion pauseMenu;
	public static TextureRegion ready;
	public static TextureRegion gameOver;
	public static TextureRegion highScoresRegion;
	public static TextureRegion soundOn;
	public static TextureRegion soundOff;
	public static TextureRegion logo;
	public static TextureRegion arrow;
	public static TextureRegion pause;
	public static TextureRegion spring;
	public static TextureRegion castle;
	public static TextureRegion platform;
	public static TextureRegion dudeHit;
	
	public static Animation coinAnim;
	public static Animation dudeJump;
	public static Animation dudeFall;
	public static Animation brakingPlatform;
	public static Animation killerFly;
	
	public static Font font;
	
	public static Music music;
	public static Sound coinSound;
	public static Sound clickSound;
	public static Sound jumpSound;
	public static Sound highJumpSound;
	public static Sound hitSound;
	
	/**
	 * Loader.
	 * @param game object
	 */
	public static void load(GLGame game) {
		
		// base
		background = new Texture(game, "background.png");
		backgroundRegion = new TextureRegion(background, 0, 0, 320, 480);
		
		// processing
    	items = new Texture(game, "gfxcore.png");
    	mainMenu = new TextureRegion(items, 0, 224, 300, 110);
    	pauseMenu = new TextureRegion(items, 224, 128, 192, 96);
    	ready = new TextureRegion(items, 320, 224, 192, 32);
    	gameOver = new TextureRegion(items, 352, 256, 160, 96);
    	highScoresRegion = new TextureRegion(Assets.items, 0, 257, 300, 110 / 3);
    	logo = new TextureRegion(items, 0, 352, 274, 142);
    	soundOff = new TextureRegion(items, 0, 0, 64, 64);
    	soundOn = new TextureRegion(items, 64, 0, 64, 64);
    	arrow = new TextureRegion(items, 0, 64, 64, 64);
    	pause = new TextureRegion(items, 64, 64, 64, 64);
    	
    	spring = new TextureRegion(items, 128, 0, 32, 32);
    	castle = new TextureRegion(items, 128, 64, 64, 64);
    	
    	coinAnim = new Animation(0.2f,
    			new TextureRegion(items, 128, 32, 32, 32),
    			new TextureRegion(items, 160, 32, 32, 32),
    			new TextureRegion(items, 192, 32, 32, 32),
    			new TextureRegion(items, 160, 32, 32, 32));
    	
    	dudeJump = new Animation(0.2f,
    			new TextureRegion(items, 0, 128, 32, 32),
    			new TextureRegion(items, 32, 128, 32, 32));
    	
    	dudeFall = new Animation(0.2f,
    			new TextureRegion(items, 64, 128, 32, 32),
    			new TextureRegion(items, 96, 128, 32, 32));
    	
        dudeHit = new TextureRegion(items, 128, 128, 32, 32);
        
        killerFly = new Animation(0.2f,
        		new TextureRegion(items, 0, 160, 32, 32),
        		new TextureRegion(items, 32, 160, 32, 32));
        
        platform = new TextureRegion(items, 64, 160, 64, 16);
        
        brakingPlatform = new Animation(0.2f,
        		new TextureRegion(items, 64, 160, 64, 16),
        		new TextureRegion(items, 64, 176, 64, 16),
        		new TextureRegion(items, 64, 192, 64, 16),
        		new TextureRegion(items, 64, 208, 64, 16));
        
        font = new Font(items, 224, 0, 16, 16, 20);
        
        // play music in background 
        music = game.getAudio().newMusic("music_bg.mp3");
        music.setLooping(true);
        music.setVolume(0.8f);
        if (Settings.soundEnabled)
        	music.play();
        
        coinSound = game.getAudio().newSound("coin.ogg");
        clickSound = game.getAudio().newSound("click.ogg");
        jumpSound = game.getAudio().newSound("jump.ogg");
        highJumpSound = game.getAudio().newSound("highjump.ogg");
        hitSound = game.getAudio().newSound("hit.ogg");
    }
    
    public static void playSound(Sound sound) {
    	if (Settings.soundEnabled)
    		sound.play(1);
    }
    
    public static void reload() {
    	background.reload();
    	items.reload();
    	if (Settings.soundEnabled)
    		music.play();
    }
    
}
