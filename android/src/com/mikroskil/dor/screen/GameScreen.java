package com.mikroskil.dor.screen;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.mikroskil.dor.Cowboy;
import com.mikroskil.dor.GameMode;
import com.mikroskil.dor.World;
import com.mikroskil.dor.util.ScreenEnum;
import com.mikroskil.dor.util.ScreenManager;
import com.mikroskil.dor.util.UIFactory;

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
    private TextureRegion cowboyHideLeft;
    private TextureRegion cowboyHideRight;
    private TextureRegion currentFrame;

    //world display on screen
    private static final float CAMERA_WIDTH = 10f;
    private static final float CAMERA_HEIGHT = 7f;
    private World world;
    private OrthographicCamera cam;
    public static Texture backgroundTexture;
    public static Sprite backgroundSprite;

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
    private String scoreBoardText,
            counterText,
            cowboy1Notif,
            cowboy2Notif;
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
    private boolean bothCowboyReady = false;
    float counterStateTime = 0;
    float shootSpeedTime = 0;
    int randomBang=0;
    int score_limit = 0;
    int highest_score = 0;
    Array<Sprite> cowboySprite = new Array<Sprite>();
    GlyphLayout glyphLayoutScore = new GlyphLayout(),
            glyphLayoutCowboy1Notif = new GlyphLayout(),
            glyphLayoutCowboy2Notif = new GlyphLayout();
    BitmapFont scoreBoardBitmap,
            counterBitmap,
            cowboy1NotifBitmap,
            cowboy2NotifBitmap;
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
        counterBitmap = new BitmapFont();
        cowboy1NotifBitmap = new BitmapFont();
        cowboy2NotifBitmap = new BitmapFont();
        scoreBoardText = "P1: 0 ; P2: 0 ;";
        if(gameMode.getGameType().equals(GameMode.GameType.CLASSIC))
            scoreBoardText+= "Round: 1";
        counterText = "Touch to Start";
        cowboy1Notif = "";
        cowboy2Notif = "";

        stateTime =0 ;
        loadTextures();
        loadSound();
        loadGameMode();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 1, 0, 0.8f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        drawBackground(spriteBatch);
        drawCounter(spriteBatch);
        drawCowboy();
        drawGame(spriteBatch);
        spriteBatch.end();

        this.draw();

        if(debug)
            drawDebug();
    }

    @Override
    public void resize(int w, int h) {
        this.width = w;
        this.height = h;
        ppuX = (float)width / CAMERA_WIDTH;
        ppuY = (float)height / CAMERA_HEIGHT;
        Log.d("HEIGHT", Float.toString(ppuX));
        Log.d("WIDTH", Float.toString(ppuY));
    }

    /**
     * load All Animation Texture
     */
    private void loadTextures(){
        backgroundTexture = new Texture(Gdx.files.internal("img/game-bg.jpg"));
        backgroundSprite = new Sprite(backgroundTexture);

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("cowboy.pack"));
        cowboyIdleLeft = atlas.findRegion("standing-sprite");
        cowboyIdleRight = new TextureRegion(cowboyIdleLeft);
        cowboyIdleRight.flip(true, false);
        cowboyHideLeft = atlas.findRegion("hide-sprite");
        cowboyHideRight = new TextureRegion(cowboyHideLeft);
        cowboyHideRight.flip(true, false);

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
            cowboy.setFlagReady(false);
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
                if(animation == null) {
                    if(cowboy.getState().equals(Cowboy.State.HIDE))
                        currentFrame = cowboyHideRight;
                    else
                        currentFrame = cowboyIdleRight;
                }
            }else{     //left side cowboy
                animation = getAnimation(cowboy.getState(), "right");
                if(animation == null)
                    if(cowboy.getState().equals(Cowboy.State.HIDE))
                        currentFrame = cowboyHideLeft;
                    else
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
        scoreBoardBitmap.draw(batch, scoreBoardText, (Gdx.graphics.getWidth() / 2) - (glyphLayoutScore.width / 2), Gdx.graphics.getHeight());

        glyphLayoutCowboy1Notif.setText(cowboy1NotifBitmap, cowboy1Notif);
        cowboy1NotifBitmap.setColor(0, 0, 0, 1);
        cowboy1NotifBitmap.getData().setScale(3, 3);
        cowboy1NotifBitmap.draw(batch, cowboy1Notif, 2.5f * ppuX, 1 * ppuY);

        glyphLayoutCowboy2Notif.setText(cowboy1NotifBitmap, cowboy2Notif);
        cowboy2NotifBitmap.setColor(0, 0, 0, 1);
        cowboy2NotifBitmap.getData().setScale(3, 3);
        cowboy2NotifBitmap.draw(batch, cowboy2Notif, 6.5f * ppuX, 1 * ppuY);
    }

    private void drawCounter(SpriteBatch batch){
        if(flagDrawShot) {
            counterText = "Draw!";
        }else if(flagCounterStart){
            counterStateTime += Gdx.graphics.getDeltaTime();
            if(Math.ceil(counterStateTime) <= 3) {
                counterText = Integer.toString((int) (Math.ceil(counterStateTime)));
            }else if(Math.ceil(counterStateTime) == (randomBang + 3)) {
                counterText = "DOR !";
                flagCounterStart = false;
                flagStartShot = true;
                counterStateTime = 0;
                cowboy1Notif = "";
                cowboy2Notif = "";
            }else {
                counterText = "";
            }
        }

        if(!flagStartGame){
            counterText = "Press Shot Button to Start";
        }else if(flagFinishShot){
            counterText = "Kill Speed \n";
            for(Cowboy cowboy : world.getCowboys()) {
                DecimalFormat df = new DecimalFormat("0.0000");
                if(cowboy.getCowboyId() == winId){
                    counterText+= df.format(cowboy.getSpeed());
                }

                if (cowboy.getCowboyId() == 1)
                    cowboy1Notif = "Top Speed \n"+df.format(cowboy.getTopSpeed());
                else
                    cowboy2Notif = "Top Speed \n"+df.format(cowboy.getTopSpeed());
            }
        }

        if(flagStartShot)
            shootSpeedTime+= Gdx.graphics.getDeltaTime();

        glyphLayoutScore.setText(counterBitmap, counterText);
        counterBitmap.setColor(1, 1, 1, 1);
        counterBitmap.getData().setScale(3, 3);
        counterBitmap.draw(batch, counterText, (Gdx.graphics.getWidth() / 2) - (glyphLayoutScore.width / 2),
                (Gdx.graphics.getHeight() / 2) - (glyphLayoutScore.height / 2));
    }

    private void drawBackground(SpriteBatch batch){
        backgroundSprite.draw(batch);
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

    private Cowboy getTouchedCowboy(int touchId){
        for (Cowboy cowboy : world.getCowboys()) {
            if(cowboy.getCowboyId() == touchId){
                return cowboy;
            }
        }
        return null;
    }
    private Cowboy getEnemyCowboy(int touchId){
        for (Cowboy cowboy : world.getCowboys()) {
            if(cowboy.getCowboyId() != touchId){
                return cowboy;
            }
        }
        return null;
    }

    private void buttonTouch(int touchId, String buttonType){
        Cowboy touchedCowboy = getTouchedCowboy(touchId);
        Cowboy enemyCowboy = getEnemyCowboy(touchId);
        if(flagEndGame){
            ScreenManager.getInstance().showScreen(ScreenEnum.LEVEL_SELECT, null);
        }else if(flagDrawShot){
            loadGameMode();
            reloadSound.play();
            flagDrawShot = false;
            flagFinishShot = true;
        }else if(!flagStartGame) {
            if(buttonType.equals("shoot")){
                if(!touchedCowboy.isFlagReady()){
                    reloadSound.play();
                    touchedCowboy.setFlagReady(true);
                }

                if(touchedCowboy.isFlagReady() && enemyCowboy.isFlagReady()){
                    bothCowboyReady = true;
                    flagStartGame = true;
                    flagCounterStart = true;

                    if(touchedCowboy.isFlagReady()) {
                        if (touchedCowboy.getCowboyId() == 1)
                            cowboy1Notif = "Ready";
                        else
                            cowboy2Notif = "Ready";
                    }

                    loadGameMode();
                }

                if(touchedCowboy.isFlagReady()) {
                    if (touchedCowboy.getCowboyId() == 1)
                        cowboy1Notif = "Ready";
                    else
                        cowboy2Notif = "Ready";
                }
            }
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
            if(winId == 0){
                if(buttonType.equals("hide")){
                    touchedCowboy.setState(Cowboy.State.HIDE);
                    touchedCowboy.setSpeed(shootSpeedTime);
                }else if(buttonType.equals("idle")) {
                    touchedCowboy.setState(Cowboy.State.IDLE);
                    touchedCowboy.setSpeed(0);
                }else if(buttonType.equals("shoot")){
                    if (!touchedCowboy.isFlagShot() && touchedCowboy.getState().equals(Cowboy.State.IDLE)) { //Cowboy who follow rule
                        touchedCowboy.setState(Cowboy.State.SHOOT);
                        touchedCowboy.setFlagShot(true);
                        touchedCowboy.setSpeed(shootSpeedTime);

                        if(enemyCowboy.getState().equals(Cowboy.State.IDLE)){
                            touchedCowboy.addScore();
                            winId = touchId;
                            hitSound.play();
                            winSound.play();
                            touchedCowboy.setFlagHit(true);
                            enemyCowboy.setState(Cowboy.State.LOSE);

                            Log.d("touched speed", Float.toString(touchedCowboy.getSpeed()));
                            if(touchedCowboy.getTopSpeed() == 0)
                                touchedCowboy.setTopSpeed(touchedCowboy.getSpeed());
                            else if(touchedCowboy.getSpeed() < touchedCowboy.getTopSpeed())
                                touchedCowboy.setTopSpeed(touchedCowboy.getSpeed());

                        }else if(enemyCowboy.getState().equals(Cowboy.State.SHOOT)){
                            if(touchedCowboy.getSpeed() < enemyCowboy.getSpeed() && enemyCowboy.getSpeed()!= 0) {
                                touchedCowboy.addScore();
                                winId = touchId;
                                hitSound.play();
                                winSound.play();
                                touchedCowboy.setFlagHit(true);
                                enemyCowboy.setState(Cowboy.State.LOSE);
                                if (touchedCowboy.getTopSpeed() == 0)
                                    touchedCowboy.setTopSpeed(touchedCowboy.getSpeed());
                                else if (touchedCowboy.getSpeed() < touchedCowboy.getTopSpeed())
                                    touchedCowboy.setTopSpeed(touchedCowboy.getSpeed());
                            }else if(touchedCowboy.getSpeed() > enemyCowboy.getSpeed() && !enemyCowboy.isFlagHit() && enemyCowboy.isFlagShot()){
                                touchedCowboy.addScore();
                                winId = touchId;
                                hitSound.play();
                                winSound.play();
                                touchedCowboy.setFlagHit(true);
                                enemyCowboy.setState(Cowboy.State.LOSE);
                                if (touchedCowboy.getTopSpeed() == 0)
                                    touchedCowboy.setTopSpeed(touchedCowboy.getSpeed());
                                else if (touchedCowboy.getSpeed() < touchedCowboy.getTopSpeed())
                                    touchedCowboy.setTopSpeed(touchedCowboy.getSpeed());
                            }
                        }else if(enemyCowboy.getState().equals((Cowboy.State.HIDE))){
                            missSound.play();
                            if(touchedCowboy.isFlagShot() && enemyCowboy.isFlagShot() && winId == 0){
                                flagDrawShot = true;
                            }
                        }
                    }
                }
            }

            for (Cowboy cowboy : world.getCowboys()) {
                scoreBoardText += "P" + cowboy.getCowboyId() + ": " + cowboy.getScore() + " ; ";
            }

            if(gameMode.getGameType().equals(GameMode.GameType.CLASSIC))
                scoreBoardText += "Round: " + gameMode.getRound();
        }
    }

    @Override
    public void buildStage() {
        float ppux = 320.0f / cam.viewportWidth;
        float ppuy = 240.0f / cam.viewportHeight;
        ImageButton btnShotLeft = UIFactory.createButton(new Texture(Gdx.files.internal("img/shot-left.png")));
        btnShotLeft.setPosition(0, 0 * ppuy);
        btnShotLeft.setSize(1.5f*ppux, 3*ppuy);
        addActor(btnShotLeft);

        ImageButton btnHideLeft = UIFactory.createButton(new Texture(Gdx.files.internal("img/hide-left.png")));
        btnHideLeft.setPosition(0, 4 * ppuy);
        btnHideLeft.setSize(1.5f * ppux, 3 * ppuy);
        addActor(btnHideLeft);

        ImageButton btnShotRight = UIFactory.createButton(new Texture(Gdx.files.internal("img/shot-right.png")));
        btnShotRight.setPosition((cam.viewportWidth - 1.5f) * ppux, 4 * ppuy);
        btnShotRight.setSize(1.5f * ppux, 3 * ppuy);
        addActor(btnShotRight);

        ImageButton btnHideRight = UIFactory.createButton(new Texture(Gdx.files.internal("img/hide-right.png")));
        btnHideRight.setPosition((cam.viewportWidth - 1.5f) * ppux, 0 * ppuy);
        btnHideRight.setSize(1.5f * ppux, 3 * ppuy);
        addActor(btnHideRight);

        btnShotLeft.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                buttonTouch(1, "shoot");
                return true;
            }
        });
        btnShotRight.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                buttonTouch(2, "shoot");
                return true;
            }
        });

        btnHideLeft.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                buttonTouch(1, "hide");
                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                buttonTouch(1, "idle");
            }
        });
        btnHideRight.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                buttonTouch(2, "hide");
                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                buttonTouch(2, "idle");
            }
        });
        //Gdx.input.setInputProcessor(this);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
