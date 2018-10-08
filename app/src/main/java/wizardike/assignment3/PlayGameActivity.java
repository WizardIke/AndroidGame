package wizardike.assignment3;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.Enumeration;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

public class PlayGameActivity extends AppCompatActivity {
    private static boolean entityClassesLoaded = false;

    private Engine engine;
    private Uri saveFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null) {
            actionbar.hide();
        }

        if(!entityClassesLoaded) {
            loadEntityClasses();
            entityClassesLoaded = true;
        }

        boolean succeeded = false;
        Intent intent = getIntent();
        if(intent != null) {
            saveFile = intent.getData();
            if(saveFile != null) {
                try {
                    engine = new Engine(this, new PlayGameRequest(saveFile) {
                        @Override
                        public void onPlayingEnded(final GameState state) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    switch(state) {
                                        case error:
                                            onGameError();
                                            break;
                                        case finished:
                                            onGameFinished();
                                            break;
                                        case suspended:
                                            onGameSuspended();
                                            break;
                                    }
                                }
                            });
                        }
                    });
                    succeeded = true;
                } catch (Exception e) {/*empty*/}
            }
        }
        if(!succeeded) {
            onGameError();
        }

        //default to successful run
        Intent intent2 = new Intent();
        intent2.setData(null);
        setResult(RESULT_OK, intent2);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }

        engine.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        engine.pause();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setData(null);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    private void onGameError() {
        Intent intent = new Intent();
        intent.setData(saveFile);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void onGameFinished() {
        Intent intent = new Intent();
        intent.setData(saveFile);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void onGameSuspended() {
        Intent intent = new Intent();
        intent.setData(null);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void loadEntityClasses() {
        try {
            Log.d("PlayGameActivity", "loading classes");
            Class.forName("wizardike.assignment3.levels.LevelGenerator");
            Class.forName("wizardike.assignment3.levels.EntityLoader");

            PathClassLoader classLoader = (PathClassLoader)getClassLoader();
            DexFile df = new DexFile(getApplicationInfo().sourceDir);
            for (Enumeration<String> iterator = df.entries(); iterator.hasMoreElements(); ) {
                String className = iterator.nextElement();
                Log.d("PlayGameActivity", "found class: " + className);
                if (className.contains("wizardike.assignment3.entities")) {
                    classLoader.loadClass(className);
                    Log.d("PlayGameActivity", "loaded class: " + className);
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            onGameError();
        }
    }
}
