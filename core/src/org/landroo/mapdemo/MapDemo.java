package org.landroo.mapdemo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;


public class MapDemo extends Game {
	private static final String TAG = "MapDemo";
	private static final String HU_CHARS = "AÁBCDEÉFGHIÍJKLMNOÓÖŐPQRSTUÚÜŰVWXYZ"
			+ "aábcdeéfghiíjklmnoóöőpqrstuúüűvwxyz"
			+ "1234567890.,:;_¡!¿?\"'+-*/()[]={}";

	static public Skin mSkin;
	static public BitmapFont mFont;
	static public SpriteBatch mBatch;
	
	@Override
	public void create () {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 20;
		parameter.color = new Color(0, 230, 230, 255);
		parameter.characters = HU_CHARS;
		mFont = generator.generateFont(parameter);
		mBatch = new SpriteBatch();
		//mSkin = new Skin(Gdx.files.internal("skin/uiskin.json"));
		mSkin = new Skin();
		mSkin.add("arial", mFont, BitmapFont.class);
		mSkin.addRegions(new TextureAtlas(Gdx.files.internal("skin/uiskin.atlas")));
		mSkin.load(Gdx.files.internal("skin/uiskin.json"));

		setScreen(new MenuScreen(this));

		generator.dispose(); // don't forget to dispose to avoid memory leaks!
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		mBatch.dispose();
		mFont.dispose();
	}
}
