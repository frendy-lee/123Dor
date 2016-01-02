package com.mikroskil.dor;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mikroskil.dor.util.ScreenEnum;
import com.mikroskil.dor.util.ScreenManager;

public class CowboyDor extends Game {
    public SpriteBatch batch;
    public BitmapFont font;

    public void create() {
        ScreenManager.getInstance().initialize(this);
        ScreenManager.getInstance().showScreen( ScreenEnum.MAIN_MENU );
    }

    public void render() {
        super.render(); //important!
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
