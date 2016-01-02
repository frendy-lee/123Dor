package com.mikroskil.dor.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mikroskil.dor.Cowboy;
import com.mikroskil.dor.GameMode;
import com.mikroskil.dor.World;
import com.mikroskil.dor.util.ScreenEnum;
import com.mikroskil.dor.util.ScreenManager;

import java.text.DecimalFormat;
import java.util.Random;

public class GameScreen extends AbstractScreen{

    private Sound hitSound;
    private Sound missSound;
    private Sound reloadSound;
    private Sound winSound;

    //Cowboy Animation for left and right player
    private Animation shotRightAnimation;
    private Animation winRightAnimation;
    private Animation dieRightAnimation;
    private Animation shotLeftAnimation;
    private Animation winLeftAnimation;
    private Animation dieLeftAnimation;

    //Cowboy Texture for left and right player
    private TextureRegion cowboyIdleLeft;
    private TextureRegion cowboyIdleRight;
    private TextureRegion currentFrame;

    //world display on screen
    private static final float CAMERA_WIDTH = 10f;
    private static final float CAMERA_HEIGHT = 7f;
    private World world;
    private OrthographicCamera cam;

    //debug mode
    ShapeRenderer debugRenderer = new ShapeRenderer();

    //sprite setting
    private SpriteBatch spriteBatch;
    private boolean debug = false;
    private int width;
    private int height;
    private float ppuX; //pixel per unit X
    private float ppuY; //pixel per unit Y
    float stateTime=0; //state time for animation
    float winStateTime=0; //state time for win animation

    //game setting
    private String scoreBoardText;
    private String counterText;
    private int winId=0;
    public int roundFlag;
    public GameMode gameMode;
    private boolean flagStartShot = false;
    private boolean flagFinishShot = false;
    private boolean flagDrawShot = false;
    private boolean flagStartGame = false;
    private boolean flagEndGame = false;
    private boolean flagCounterEnd = false;
    private boolean flagCounterStart = false;
    float counterStateTime = 0;
    float shootSpeedTime = 0;
    int randomBang=0;
    int score_limit = 0;
    int highest_score = 0;
    Array<Sprite> cowboySprite = new Array<Sprite>();
    BitmapFont scoreBoardBitmap;
    GlyphLayout glyphLayoutScore = new GlyphLayout();
    BitmapFont counterBitmap;
    Random rn = new Random();

    public GameScreen(GameMode.GameType GT, boolean debug) {
        super();
        this.world = new World();
        this.gameMode = new GameMode(GT);
        this.cam = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
        this.cam.position.set(CAMERA_WIDTH / 2f, CAMERA_HEIGHT / 2f, 0);
        this.cam.update();
        this.debug = debug;
        spriteBatch = new SpriteBatch();
        scoreBoardBitmap = new BitmapFont();
        scoreBoardText = "P1: 0 ; P2: 0 ;";
        if(gameMode.getGameType().equals(GameMode.GameType.CLASSIC))
            scoreBoardText+= "Round: 1";
        counterBitmap = new BitmapFont();
        counterText = "Touch to Start";
        stateTime =0 ;
        loadTextures();
        loadSound();
        Gdx.input.setInputProcessor(this);
        loadGameMode();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 1, 0, 0.8f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        drawCounter(spriteBatch);
        drawCowboy();
        drawGame(spriteBatch);
        spriteBatch.end();

        if(debug)
            drawDebug();
    }

    @Override
    public void resize(int w, int h) {
        this.width = w;
        this.height = h;
        ppuX = (float)width / CAMERA_WIDTH;
        ppuY = (float)height / CAMERA_HEIGHT;
    }

    /**
     * load All Animation Texture
     */
    private void loadTextures(){
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("cowboy.pack"));
        cowboyIdleLeft = atlas.findRegion("standing-sprite");
        cowboyIdleRight = new TextureRegion(cowboyIdleLeft);
        cowboyIdleRight.flip(true, false);
        TextureRegion[] shootingRightFrames = new TextureRegion[4];
        TextureRegion[] winRightFrames = new TextureRegion[10];
        TextureRegion[] shootingLeftFrames = new TextureRegion[4];
        TextureRegion[] winLeftFrames = new TextureRegion[10];
        TextureRegion[] dieRightFrames = new TextureRegion[9];
        TextureRegion[] dieLeftFrames = new TextureRegion[9];

        for(int i = 0; i < 4; i++){
            shootingRightFrames[i] = atlas.findRegion("shooting-sprite", (i+1));
        }
        shotRightAnimation = new Animation(0.1f, shootingRightFrames);

        for(int i = 0; i < 4; i++){
            shootingLeftFrames[i] = new TextureRegion(shootingRightFrames[i]);
            shootingLeftFrames[i].flip(true, false);
        }
        shotLeftAnimation = new Animation(0.1f, shootingLeftFrames);


        for(int i = 0; i < 10; i++){
            winRightFrames[i] = atlas.findRegion("win-sprite", (i+1));
        }
        winRightAnimation = new Animation(0.2f, winRightFrames);
        for(int i = 0; i < 10; i++){
            winLeftFrames[i] = new TextureRegion(winRightFrames[i]);
            winLeftFrames[i].flip(true,false);
        }
        winLeftAnimation = new Animation(0.2f, winLeftFrames);

        for(int i = 0; i < 9; i++){
            dieRightFrames[i] = atlas.findRegion("dead-sprite", (i+1));
        }
        dieRightAnimation = new Animation(0.1f, dieRightFrames);
        for(int i = 0; i < 9; i++){
            dieLeftFrames[i] = new TextureRegion(dieRightFrames[i]);
            dieLeftFrames[i].flip(true,false);
        }
        dieLeftAnimation = new Animation(0.1f, dieLeftFrames);
    }

    private void loadSound(){
        hitSound = Gdx.audio.newSound(Gdx.files.internal("hit.mp3"));
        missSound = Gdx.audio.newSound(Gdx.files.internal("miss.mp3"));
        reloadSound = Gdx.audio.newSound(Gdx.files.internal("reload.mp3"));
        winSound = Gdx.audio.newSound(Gdx.files.internal("win.mp3"));
    }

    /**
     * assign Game mode onto player
     */
    private void loadGameMode(){
        randomBang = rn.nextInt(3) + 1;
        winId = 0;
        for(Cowboy cowboy : world.getCowboys()) {
            cowboy.setState(Cowboy.State.IDLE);
            cowboy.setFlagShot(false);
            cowboy.setFlagHit(false);
        }

        if(gameMode.getGameType().equals(GameMode.GameType.CLASSIC)) {
            if (gameMode.getRound() == 1 && !flagFinishShot && flagStartGame) {
                roundFlag = 1;
                gameMode.setRound(roundFlag);
                for (Cowboy cowboy : world.getCowboys()) {
                    cowboy.setScore(0);
                }
            } else {
                if (flagStartGame && flagFinishShot) {
                    if (gameMode.nextRound()) {
                        roundFlag = gameMode.getRound();
                        counterStateTime = 0;
                        winStateTime = 0;
                        stateTime = 0;
                        shootSpeedTime = 0;
                        flagStartGame = false;
                        flagCounterStart = false;
                        flagFinishShot = false;
                        flagStartShot = false;
                        flagDrawShot = false;
                        winId = 0;
                    }else{
                        flagEndGame = true;
                    }
                }
            }
        }else{
            if (score_limit == 0 && !flagFinishShot && flagStartGame){
                score_limit = gameMode.getScore_limit();
                for (Cowboy cowboy : world.getCowboys()) {
                    cowboy.setScore(0);
                }
            } else {
                if(flagStartGame && flagFinishShot){
                    for (Cowboy cowboy : world.getCowboys()) {
                        if(cowboy.getScore() > highest_score)
                            this.highest_score = cowboy.getScore();
                    }

                    if(this.highest_score < gameMode.getScore_limit()) {
                        winStateTime = 0;
                        stateTime = 0;
                        counterStateTime = 0;
                        shootSpeedTime = 0;
                        flagStartGame = false;
                        flagCounterStart = false;
                        flagFinishShot = false;
                        flagStartShot = false;
                        flagDrawShot = false;
                    }else{
                        flagEndGame = true;
                    }
                }
            }
        }
    }

    private Animation getAnimation(Cowboy.State state, String side){
        if(state.equals(Cowboy.State.SHOOT)){
            if(side.equals("left"))
                return shotLeftAnimation;
            else
                return shotRightAnimation;
        }else if(state.equals(Cowboy.State.LOSE)){
            if(side.equals("left"))
                return dieLeftAnimation;
            else
                return dieRightAnimation;
        }else if(state.equals(Cowboy.State.WIN)){
            if(side.equals("left"))
                return winLeftAnimation;
            else
                return winRightAnimation;
        }else{
            return null;
        }
    }

    /**
     * Draw Cowboy State and Animation
     */
    private void drawCowboy(){
        int i=0;
        for(Cowboy cowboy : world.getCowboys()){
            Animation animation;
            if(i==1) { //right side cowboy
                animation = getAnimation(cowboy.getState(), "left");
                if(animation == null)
                    currentFrame = cowboyIdleRight;
            }else{     //left side cowboy
                animation = getAnimation(cowboy.getState(), "right");
                if(animation == null)
                    currentFrame = cowboyIdleLeft;
            }

            if (animation != null) {
                if (flagFinishShot && cowboy.getState().equals(Cowboy.State.WIN) && cowboy.getCowboyId() == winId) {
                    winStateTime += Gdx.graphics.getDeltaTime();
                    currentFrame = animation.getKeyFrame(winStateTime, true);
                } else {
                    if (animation.isAnimationFinished(stateTime)) {
                        if (cowboy.isFlagHit() && cowboy.getCowboyId() == winId) {
                            cowboy.setState(Cowboy.State.WIN);
                            flagFinishShot = true;
                            stateTime = 0;
                        }else {
                            currentFrame = animation.getKeyFrame(stateTime);
                        }
                    } else {
                        stateTime += Gdx.graphics.getDeltaTime();
                        currentFrame = animation.getKeyFrame(stateTime, true);
                    }
                }
            }

            spriteBatch.draw(currentFrame, cowboy.getPosition().x * ppuX, cowboy.getPosition().y * ppuY,
                    cowboy.getSize() * ppuX, cowboy.getSize() * ppuY);
            i++;
        }
    }

    /**
     * Draw game score
     */
    private void drawGame(SpriteBatch batch){
        glyphLayoutScore.setText(scoreBoardBitmap, scoreBoardText);
        scoreBoardBitmap.setColor(0, 0, 0, 1);
        scoreBoardBitmap.getData().setScale(3, 3);
        scoreBoardBitmap.draw(batch, scoreBoardText, (Gdx.graphics.getWidth() / 2)-(glyphLayoutScore.width/2), Gdx.graphics.getHeight());
    }

    private void drawCounter(SpriteBatch batch){
        if(flagDrawShot) {
            counterText = "Guys... \nWait For My Mark";
        }else if(flagCounterStart){
            counterStateTime += Gdx.graphics.getDeltaTime();
            if(Math.ceil(counterStateTime) <= 3) {
                counterText = Integer.toString((int) (Math.ceil(counterStateTime)));
            }else if(Math.ceil(counterStateTime) == (randomBang + 3)) {
                counterText = "DOR !";
                flagCounterStart = false;
                flagStartShot = true;
                counterStateTime = 0;
            }else {
                counterText = "";
            }
        }

        if(!flagStartGame){
            counterText = "Touch to Start";
        }else if(flagFinishShot){
            counterText = "Speed : ";
            for(Cowboy cowboy : world.getCowboys()) {
                if(cowboy.getCowboyId() == winId){
                    DecimalFormat df = new DecimalFormat("0.0000");
                    counterText+= df.format(cowboy.getSpeed());
                }
            }
        }

        if(flagStartShot)
            shootSpeedTime+= Gdx.graphics.getDeltaTime();

        glyphLayoutScore.setText(counterBitmap, counterText);
        counterBitmap.setColor(1, 1, 1, 1);
        counterBitmap.getData().setScale(3, 3);
        counterBitmap.draw(batch, counterText, (Gdx.graphics.getWidth() / 2)-(glyphLayoutScore.width/2),
                (Gdx.graphics.getHeight()/2)-(glyphLayoutScore.height/2));
    }

    /**
     * Draw debug mode position
     */
    private void drawDebug(){
        //render Cowboy
        debugRenderer.setProjectionMatrix(cam.combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for(Cowboy cowboy : world.getCowboys()){
            Rectangle rect = cowboy.getBounds();
            float x1 = cowboy.getPosition().x + rect.x;
            float y1 = cowboy.getPosition().y + rect.y;
            debugRenderer.setColor(new Color(1, 0, 0, 1));
            debugRenderer.rect(x1, y1, rect.width, rect.height);
        }
        debugRenderer.end();
    }

    @Override public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        int touchId;
        if (screenX < (Gdx.graphics.getWidth() / 2)) {
            touchId = 1;
        } else {
            touchId = 2;
        }

        if(flagEndGame){
            ScreenManager.getInstance().showScreen(ScreenEnum.LEVEL_SELECT, null);
            return false;
        }else if(flagDrawShot){
            loadGameMode();
            reloadSound.play();
            flagDrawShot = false;
            flagFinishShot = true;
        }else if(!flagStartGame) {
            reloadSound.play();
            flagStartGame = true;
            flagCounterStart = true;
            loadGameMode();
        }else if(flagFinishShot){
            loadGameMode();
            scoreBoardText = "";
            for (Cowboy cowboy : world.getCowboys()) {
                scoreBoardText += "P" + cowboy.getCowboyId() + ": " + cowboy.getScore() + " ; ";
            }
            if (gameMode.getGameType().equals(GameMode.GameType.CLASSIC))
                scoreBoardText += "Round: " + gameMode.getRound();
        }else if(flagStartShot){
            scoreBoardText="";
            for (Cowboy cowboy : world.getCowboys()) {
                if(flagDrawShot) {
                    cowboy.setState(Cowboy.State.IDLE);
                    flagFinishShot = true;
                }else if (cowboy.getCowboyId() == touchId) { //Cowboy who get touch in time
                    if(!cowboy.isFlagShot() && !cowboy.isFlagHit()) { //Cowboy who follow rule
                        cowboy.setState(Cowboy.State.SHOOT);
                        cowboy.addScore();
                        cowboy.setFlagShot(true);
                        cowboy.setFlagHit(true);
                        cowboy.setSpeed(shootSpeedTime);
                        winId = cowboy.getCowboyId();
                        hitSound.play();
                        winSound.play();
                    }else if(cowboy.isFlagShot() && !cowboy.isFlagHit()){ //Cowboy who not follow rule
                        //cowboy do nothing
                    }else{ //Cowboy who win at the round and still get touch
                        //cowboy do nothing
                    }
                }else{  //Cowboy who not first touch
                    //check if this is the winner cowboy
                    if(winId == 0 || winId != cowboy.getCowboyId()) {
                        //Cowboy automatically lost
                        cowboy.setState(Cowboy.State.LOSE);
                        missSound.play();
                    }
                }

                scoreBoardText+= "P"+cowboy.getCowboyId()+": " + cowboy.getScore() + " ; ";
            }

            if(gameMode.getGameType().equals(GameMode.GameType.CLASSIC))
                scoreBoardText += "Round: " + gameMode.getRound();
        }else{
            boolean allShot = true;
            for (Cowboy cowboy : world.getCowboys()) {
                if (cowboy.getCowboyId() == touchId) {
                    if(!cowboy.isFlagShot()) {
                        cowboy.setState(Cowboy.State.SHOOT);
                        cowboy.setFlagShot(true);
                        cowboy.setFlagHit(false);
                        missSound.play();
                    }
                }
                if(!cowboy.isFlagShot()) {
                    allShot = false;
                }
            }

            if(allShot)
                flagDrawShot = true;
        }

        this.cam.unproject(tp.set(screenX, screenY, 0));
        dragging = true;
        return true;
    }

    @Override public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        if (pointer > 0) return false;
        this.cam.unproject(tp.set(screenX, screenY, 0));
        dragging = false;
        return true;
    }

    Vector3 tp = new Vector3();
    boolean dragging;
    @Override public boolean mouseMoved (int screenX, int screenY) {
        // we can also handle mouse movement without anything pressed
//      camera.unproject(tp.set(screenX, screenY, 0));
        return false;
    }

    @Override public boolean touchDragged (int screenX, int screenY, int pointer) {
        if (!dragging) return false;
        this.cam.unproject(tp.set(screenX, screenY, 0));
        return true;
    }

    @Override public boolean keyDown (int keycode) {
        return false;
    }

    @Override public boolean keyUp (int keycode) {
        return false;
    }

    @Override public boolean keyTyped (char character) {
        return false;
    }

    @Override public boolean scrolled (int amount) {
        return false;
    }

    @Override
    public void buildStage() {

    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
