package com.raindrop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {
    final RaindropGame game;
    OrthographicCamera camera;

    public MainMenuScreen(final RaindropGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);
    }

    @Override
    public void render(float delta) {
        // Clear screen with dark blue color
        ScreenUtils.clear(0, 0, 0.2f, 1);

        // Update cam
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        // Drawing
        game.batch.begin();
        game.font.draw(game.batch, "Welcome to RainDrop!", 100, 300);
        game.font.draw(game.batch, "Tap anywhere to begin", 100, 220);
        game.batch.end();

        if(Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

}
