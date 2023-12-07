package org.landroo.mapdemo;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen implements Screen {
    private static final String TAG = "MenuScreen";
    private MapDemo mGame;
    private Stage mStage;
    private Skin mSkin;
    private MapScreen mMapScreen;

    public MenuScreen(MapDemo game) {

        mGame = game;
        mSkin = MapDemo.mSkin;
        mStage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(mStage);
        createUI(mSkin);

    }

    private void createUI(Skin skin) {

        Table rootTable = new Table(skin);
        if(Gdx.app.getType() != Application.ApplicationType.Desktop) {
            float pxrat =Gdx.graphics.getDensity();
            Gdx.app.log(TAG, "Density: " + pxrat);
            rootTable.setTransform(true);
            rootTable.setScale(pxrat);
            rootTable.setWidth(Gdx.graphics.getWidth() / pxrat);
            rootTable.setHeight(Gdx.graphics.getHeight() / pxrat);
        }
        else {
            rootTable.setFillParent(true);
        }
        //rootTable.debug();
        mStage.addActor(rootTable);

        TextButton mapButton = new TextButton("Map", skin);
        mapButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mMapScreen = new MapScreen(mGame);
                mGame.setScreen(mMapScreen);
            }
        });

        rootTable.add(mapButton).padBottom(10);

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(mStage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mStage.act();

        mGame.mBatch.begin();
        mStage.draw();
        mGame.mBatch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        mStage.dispose();
    }
}
