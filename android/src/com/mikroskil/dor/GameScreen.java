package com.mikroskil.dor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Frendy on 12/30/2015.
 */
public class GameScreen implements Screen {
    final CowboyDor game;

    SpriteBatch batch;
    Texture img;
    private World world;
    private WorldRenderer renderer;
    private GameMode gameMode;

    public GameScreen(final CowboyDor gam){
        this.game = gam;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 1, 0, 0.8f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();
    }

    @Override
    public void resize(int width, int height) {
        renderer.setSize(width, height);
    }

    @Override
    public void show() {
        world = new World();
        gameMode = new GameMode(GameMode.GameType.FIRST_TO_1);
        renderer = new WorldRenderer(world, gameMode, false);
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
    }
}
