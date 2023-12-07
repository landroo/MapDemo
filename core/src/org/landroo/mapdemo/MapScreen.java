package org.landroo.mapdemo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Timer;
import java.util.TimerTask;


public class MapScreen implements Screen, InputProcessor {

    private static final String TAG = "MapScreen";

    final static int MAP_WIDTH = 2;
    final static int MAP_HEIGHT = 3;

    private Stage mStage;
    private Stage mUIStage;
    private Game mGame;

    private Slider slider;
    private InputMultiplexer mMultiplexer;

    private Label mLabel;

    private Image map[] = new Image[MAP_WIDTH * MAP_HEIGHT];

    // zoom
    private float sx1 = 0, sy1 = 0;
    private float sx2 = 0, sy2 = 0;
    private float sx = 0, sy = 0;

    private float px = 0, py = 0;
    private float currentZoom = 1;
    private float lastZoom = 1;

    // swipe
    private Timer swipeTimer = null;
    private float speedX = 0;
    private float speedY = 0;

    // construct
    public MapScreen(Game aGame) {
        mGame = aGame;

        mStage = new Stage(new ScreenViewport());
        mUIStage = new Stage(new ScreenViewport());

        mMultiplexer = new InputMultiplexer();
        mMultiplexer.addProcessor(mUIStage);
        mMultiplexer.addProcessor(mStage);
        mMultiplexer.addProcessor(this);

        Skin mySkin = MapDemo.mSkin;

        mLabel = new Label("", mySkin);
        mLabel.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getWidth() / 12);
        mLabel.setPosition(10,100);
        mLabel.setWrap(true);
        //mLabel.setFontScale(1);
        mUIStage.addActor(mLabel);

        Texture mt = new Texture("map.jpg");
        for(int c = 0, i = 0; i < MAP_WIDTH; i++) {
            for(int j = 0; j < MAP_HEIGHT; j++) {
                map[c] = new Image(mt);
                mStage.addActor(map[c]);
                map[c].setPosition(i * map[c].getWidth(), j * map[c].getHeight());
                c++;
            }
        }

        swipeTimer = new Timer();
        swipeTimer.scheduleAtFixedRate(new SwipeTask(), 0, 10);

        mStage.addListener(new ActorGestureListener(){
            @Override
            public boolean longPress(Actor actor, float x, float y) {
                speedX = 0;
                speedY = 0;

                for(int c = 0, i = 0; i < MAP_WIDTH; i++) {
                    for (int j = 0; j < MAP_HEIGHT; j++) {
                        map[c].setPosition(i * map[c].getWidth(), j * map[c].getHeight());
                        map[c].setScale(1);
                        c++;
                    }
                }
                lastZoom = 1;

                return true;
            }

            @Override
            public void fling(InputEvent event, float velocityX, float velocityY, int button) {
                super.fling(event, velocityX, velocityY, button);
                //Gdx.app.log("fling", "" + event.getPointer() + " " + velocityX + " " + velocityY);
                if(event.getPointer() == 0) {
                    speedX = velocityX;
                    speedY = velocityY;
                }
            }

            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                if(deltaX == 0 && deltaY == 0)
                    return;
                //Gdx.app.log("pan", "" + event.getPointer() + " " + x + " " + y);
                //Gdx.app.log("pan", "" + deltaX + " " + deltaY);
                if(event.getPointer() == 0) {
                    for(int c = 0, i = 0; i < MAP_WIDTH; i++) {
                        for(int j = 0; j < MAP_HEIGHT; j++) {
                            map[c].setPosition(map[c].getX() + deltaX, map[c].getY() + deltaY);
                            c++;
                        }
                    }
                }
            }

            @Override
            public void zoom(InputEvent event, float initialDistance, float distance){
                if(currentZoom == distance / initialDistance){
                    //Gdx.app.log("zoom", "EQ");
                    return;
                }

                if(event.getPointer() == 0) {
                    sx1 = event.getStageX();
                    sy1 = event.getStageY();
                }
                else if(event.getPointer() == 1) {
                    sx2 = event.getStageX();
                    sy2 = event.getStageY();

                    currentZoom = distance / initialDistance;

                    float cx = (sx2 - sx1) / 2 + sx1;
                    float cy = (sy2 - sy1) / 2 + sy1;

                    float ox = px * currentZoom * lastZoom - (map[0].getX() - cx);
                    float oy = py * currentZoom * lastZoom - (map[0].getY() - cy);

                    //Gdx.app.log("zoom", " o " + ox + ":" + oy + " c " + cx + ":" + cy + " " + (currentZoom * lastZoom));
                    //Gdx.app.log("zoom", " c " + cx + ":" + cy + " " + " " + (currentZoom * lastZoom));

                    float x = map[0].getX();
                    float y = map[0].getY();
                    for(int c = 0, i = 0; i < MAP_WIDTH; i++) {
                        for(int j = 0; j < MAP_HEIGHT; j++) {
                            map[c].setPosition(x + ox + i * map[c].getWidth() * currentZoom * lastZoom, y + oy + j * map[c].getHeight() * currentZoom * lastZoom);
                            map[c].setScale(currentZoom * lastZoom);
                            c++;
                        }
                    }
                }
            }

            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
            }

            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(pointer == 0) {
                    //Gdx.app.log("down0", "" + x + ":" + y);
                    sx1 = x;
                    sy1 = y;

                    speedX = 0;
                    speedY = 0;
                }
                if(pointer == 1) {
                    //Gdx.app.log("down1", "" + x + ":" + y);
                    sx2 = x;
                    sy2 = y;

                    float cx = (sx2 - sx1) / 2 + sx1;
                    float cy = (sy2 - sy1) / 2 + sy1;

                    px = (map[0].getX() - cx) / lastZoom;
                    py = (map[0].getY() - cy) / lastZoom;
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //Gdx.app.log("up", "" + currentZoom);
                if(pointer == 1) {
                    lastZoom = currentZoom * lastZoom;
                }
            }
        });
    }

    @Override
    public void show() {
        Gdx.app.log("PanScreen","show");
        Gdx.input.setInputProcessor(mMultiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mLabel.setText("x: " + (int)map[0].getX() + "\ny: " + (int)map[0].getY() + "\nw: " + (int)(map[0].getWidth() * (currentZoom * lastZoom)) + "\nh: " + (int)(map[0].getHeight() * (currentZoom * lastZoom)) + "\nz:" + (currentZoom * lastZoom));

        mUIStage.act();
        mStage.act();

        mStage.draw();
        mUIStage.draw();

        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            //mGame.setScreen(new MenuScreen(mGame));
        }
    }


    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
        swipeTimer.cancel();
    }

    @Override
    public void resume() {
        swipeTimer = new Timer();
        swipeTimer.scheduleAtFixedRate(new SwipeTask(), 0, 10);
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        mUIStage.dispose();
        mStage.dispose();
    }

    public double getDist(float x1, float y1, float x2, float y2){
        double nDelX = x2 - x1;
        double nDelY = y2 - y1;

        return Math.sqrt(nDelY * nDelY + nDelX * nDelX);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        sx = screenX;
        sy = Gdx.graphics.getHeight() - screenY;
        //Gdx.app.log(TAG, "sx " + sx + " sy " + sy);
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        float zoom = currentZoom;
        currentZoom += -amountY / 10;
        //Gdx.app.log(TAG, "amountY " + amountY);

        float x = map[0].getX();
        float y = map[0].getY();
        float w = map[0].getWidth();
        float h = map[0].getHeight();
        float ox = x - (w * currentZoom - w * zoom) * ((sx - x) / (w * zoom));
        float oy = y - (h * currentZoom - h * zoom) * ((sy - y) / (h * zoom));

        //Gdx.app.log(TAG, "x " + x + " y " + y + " w " + w + " h " + h);
        //Gdx.app.log(TAG, "sx " + sx + " sy " + sy + " zoom " + currentZoom);
        //Gdx.app.log(TAG, "ox " + ox + " oy: " + oy);

        for(int c = 0, i = 0; i < MAP_WIDTH; i++) {
            for(int j = 0; j < MAP_HEIGHT; j++) {
                map[c].setPosition( ox + i * map[c].getWidth() * currentZoom, oy + j * map[c].getHeight() * currentZoom);
                map[c].setScale(currentZoom);
                c++;
            }
        }


        return true;
    }

    class SwipeTask extends TimerTask {
        public void run() {
            if (Math.abs(speedX) > 0 || Math.abs(speedY) > 0) {

                float vx = 0;
                float vy = 0;

                if(speedX < 0) speedX += Math.sqrt(-speedX);
                else speedX -= Math.sqrt(speedX);

                if(Math.abs(speedX) < 1) speedX = 0;
                else vx = speedX / 100;

                if(speedY < 0) speedY += Math.sqrt(-speedY);
                else speedY -= Math.sqrt(speedY);

                if(Math.abs(speedY) < 1) speedY = 0;
                else vy = speedY / 100;

                //Gdx.app.log("swipe","" + vx + " " + vy);

                for(int c = 0, i = 0; i < MAP_WIDTH; i++) {
                    for(int j = 0; j < MAP_HEIGHT; j++) {
                        map[c].setPosition(map[c].getX() + vx, map[c].getY() + vy);
                        c++;
                    }
                }
            }
        }
    }
}

