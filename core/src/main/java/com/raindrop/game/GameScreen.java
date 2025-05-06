package com.raindrop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
    final RaindropGame game;

    // Assets
    Texture raindropImage;
    Texture bucketImage;
    Sound raindropSound;
    Music rainMusic;

    // Camera and Input handling
    OrthographicCamera camera;
    Vector3 touchPos;

    // Game objects
    Rectangle bucket;
    Array<Rectangle> raindrops;

    // Game state
    long lastDropTime;
    int dropsGathered;

    public GameScreen(final RaindropGame game) {
        this.game = game;

        try {
            // Load images
            raindropImage = new Texture(Gdx.files.internal("raindrop.png"));
            bucketImage = new Texture(Gdx.files.internal("bucket.png"));

            // Load sounds
            raindropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
            rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Error loading assets: " + e);
        }

        rainMusic.setLooping(true);
        rainMusic.play();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 400);

        // Initialize touch pos vector
        touchPos = new Vector3();

        // Create bucket
        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2; // Center horizontally
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;

        raindrops = new Array<Rectangle>();
        spawnRaindrop();
    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800 - 64);
        raindrop.y = 470;
        raindrop.width = 20;
        raindrop.height = 20;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        // Clear screen
        ScreenUtils.clear(0, 0, 0.2f, 1);

        // Update camera
        camera.update();

        Gdx.app.log("GameScreen", "Rendering with " + dropsGathered + " drops collected");

        if (game.batch != null && bucketImage != null && raindropImage != null) {
            game.batch.setProjectionMatrix(camera.combined);

            game.batch.begin();

            game.font.setColor(1, 1, 0, 1); // Yellow color (RGBA)

            game.font.draw(game.batch, "Raindrops Collected: " + dropsGathered, 10, 450);
            game.font.setColor(1, 1, 1, 1); // Reset to white for other text


            game.batch.draw(bucketImage, bucket.x, bucket.y, bucket.width, bucket.height);

            for (Rectangle raindrop : raindrops) {
                game.batch.draw(raindropImage, raindrop.x, raindrop.y, raindrop.width, raindrop.height);
            }

            game.batch.end();
        }

        // User input
        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - 64 / 2;
        }
        if (Gdx.input.isKeyPressed(Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();

        // Keep bucket within screen bounds
        if (bucket.x < 0) bucket.x = 0;
        if (bucket.x > 800 - 64) bucket.x = 800 - 64;

        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) {
            spawnRaindrop();
        }

        // Update raindrop positions, remove those overlapped below or hit bucket
        Iterator<Rectangle> iter = raindrops.iterator();
        while (iter.hasNext()) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 100 * Gdx.graphics.getDeltaTime();

            if (raindrop.y + 64 < 0) iter.remove();

            if (raindrop.overlaps(bucket)) {
                dropsGathered++;
                raindropSound.play();
                iter.remove();
            }
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {
        rainMusic.play();
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
        raindropImage.dispose();
        bucketImage.dispose();
        raindropSound.dispose();
        rainMusic.dispose();
    }
}
