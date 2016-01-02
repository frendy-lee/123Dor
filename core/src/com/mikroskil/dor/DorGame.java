package com.mikroskil.dor;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DorGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 0.8f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		/*left*/
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight());
		//Set up Camera with viewport
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();

		/*right*/
		Gdx.gl.glViewport(Gdx.graphics.getWidth() / 2, 0, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight());
		//Set up Camera with viewport
		batch.begin();
		batch.draw(img, Gdx.graphics.getWidth() / 2, 0);
		batch.end();
	}
}
