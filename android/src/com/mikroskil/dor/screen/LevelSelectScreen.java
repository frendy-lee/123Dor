package com.mikroskil.dor.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.utils.Align;
import com.mikroskil.dor.GameMode;
import com.mikroskil.dor.util.ScreenEnum;
import com.mikroskil.dor.util.UIFactory;

public class LevelSelectScreen extends AbstractScreen {

    private Texture txtrBg;
    private Texture txtrBack;
    private Texture txtrClassic;
    private Texture txtrFirstto1;
    private Texture txtrFirstto3;
    private Texture txtrFirstto5;

    public LevelSelectScreen() {
        super();
        txtrBg   = new Texture( Gdx.files.internal("img/game-bg.jpg") );
        txtrBack = new Texture( Gdx.files.internal("img/btn_back.png") );
        txtrClassic = new Texture( Gdx.files.internal("img/classic.png") );
        txtrFirstto1 = new Texture( Gdx.files.internal("img/first_to_1.png") );
        txtrFirstto3 = new Texture( Gdx.files.internal("img/first_to_3.png") );
        txtrFirstto5 = new Texture( Gdx.files.internal("img/first_to_5.png") );
    }

    @Override
    public void buildStage() {

        // Adding actors
        Image bg = new Image(txtrBg);
        bg.setSize(480,360);
        addActor(bg);

        ImageButton btnBack = UIFactory.createButton(txtrBack);
        btnBack.setPosition(260.f, 40.f, Align.center);
        btnBack.setSize(50, 30);
        addActor(btnBack);

        //position is from bottom left ( x from left, y from bottom )
        ImageButton btnClassic = UIFactory.createButton(txtrClassic);
        btnClassic.setPosition(160, 200.f, Align.center);
        btnClassic.setSize(80, 50);
        addActor(btnClassic);

        ImageButton btnFirstto1 = UIFactory.createButton(txtrFirstto1);
        btnFirstto1.setPosition(160, 150.f, Align.center);
        btnFirstto1.setSize(80, 50);
        addActor(btnFirstto1);

        ImageButton btnFirstto3 = UIFactory.createButton(txtrFirstto3);
        btnFirstto3.setPosition(160, 100.f, Align.center);
        btnFirstto3.setSize(80, 50);
        addActor(btnFirstto3);

        ImageButton btnFirstto5 = UIFactory.createButton(txtrFirstto5);
        btnFirstto5.setPosition(160, 50.f, Align.center);
        btnFirstto5.setSize(80, 50);
        addActor(btnFirstto5);

        btnBack.addListener(UIFactory.createListener(ScreenEnum.MAIN_MENU));
        btnClassic.addListener( UIFactory.createListener(ScreenEnum.GAME, GameMode.GameType.CLASSIC) );
        btnFirstto1.addListener( UIFactory.createListener(ScreenEnum.GAME, GameMode.GameType.FIRST_TO_1) );
        btnFirstto3.addListener( UIFactory.createListener(ScreenEnum.GAME, GameMode.GameType.FIRST_TO_3) );
        btnFirstto5.addListener( UIFactory.createListener(ScreenEnum.GAME, GameMode.GameType.FIRST_TO_5) );
    }

    @Override
    public void dispose() {
        super.dispose();
        txtrBg.dispose();
        txtrBack.dispose();
        txtrClassic.dispose();
        txtrFirstto1.dispose();
        txtrFirstto3.dispose();
        txtrFirstto5.dispose();
    }
}
