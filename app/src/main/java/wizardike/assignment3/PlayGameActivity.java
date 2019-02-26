package wizardike.assignment3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class PlayGameActivity extends AppCompatActivity {
    private AmbientMusicPlayer musicPlayer;
    private Engine engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        musicPlayer = new AmbientMusicPlayer(MusicTypes.peacefulTracks, this);

        setContentView(R.layout.activity_play_game);

        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null) {
            actionbar.hide();
        }

        boolean succeeded = false;
        Intent intent = getIntent();
        Uri saveFile = null;
        if(intent != null) {
            saveFile = intent.getData();
            if(saveFile != null) {
                try {
                    engine = new Engine(this, new PlayGameRequest(saveFile) {
                        @Override
                        public void onPlayingEnded(final GameState state) {
                            final Uri saveFile = this.saveFile;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    switch(state) {
                                        case error:
                                            onGameError(saveFile);
                                            break;
                                        case finished:
                                            onGameFinished(saveFile);
                                            break;
                                        case suspended:
                                            onGameSuspended();
                                            break;
                                    }
                                }
                            });
                        }

                        @Override
                        public void playMusic(int[] resourceIds) {
                            musicPlayer.changeFileIDs(resourceIds);
                        }
                        @Override
                        public void addFragment(int id, Fragment fragment) {
                            FragmentManager fm = getSupportFragmentManager();
                            if(fm == null) {
                                return;
                            }
                            fm.beginTransaction()
                                    .add(id, fragment)
                                    .commit();
                        }
                    });
                    succeeded = true;
                } catch (Exception e) {/*empty*/}
            }
        }
        if(!succeeded) {
            onGameError(saveFile);
        }

        //default to successful run
        Intent intent2 = new Intent();
        intent2.setData(null);
        setResult(RESULT_OK, intent2);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        int newVolumeInt = settings.getInt("music_volume", 50);
        float newQuietness = (float)(Math.log(100 - newVolumeInt) / Math.log(100));
        musicPlayer.setVolume(1 - newQuietness);
        musicPlayer.start();

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
        Log.v("PlayGameActivity", "Finished resuming");
    }

    @Override
    protected void onPause() {
        Log.v("PlayGameActivity", "Paused");
        super.onPause();
        musicPlayer.stop();
        engine.pause();
    }

    private void onGameError(Uri saveFile) {
        Intent intent = new Intent();
        intent.setData(saveFile);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void onGameFinished(Uri saveFile) {
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
}
