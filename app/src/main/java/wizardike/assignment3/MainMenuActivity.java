package wizardike.assignment3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import wizardike.assignment3.fragments.CharacterCreationFragment;
import wizardike.assignment3.fragments.LoadGameFragment;
import wizardike.assignment3.fragments.MainMenuFragment;
import wizardike.assignment3.fragments.SettingsFragment;

/**
 * Plays the games title sound track and manages fragments for the main menu
 */
public class MainMenuActivity extends AppCompatActivity implements
        MainMenuFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        LoadGameFragment.OnFragmentInteractionListener,
        CharacterCreationFragment.OnFragmentInteractionListener {
    private static final int PLAY_GAME_REQUEST_CODE = 0;

    private AmbientMusicPlayer musicPlayer;

    // Used to load the 'native-lib' library on application startup.
    //static {
    //    System.loadLibrary("native-lib");
    //}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicPlayer = new AmbientMusicPlayer(MusicTypes.mainMenuTrack, this);

        //create default preferences if the preferences haven't been created yet
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        setContentView(R.layout.activity_main_menu);

        FragmentManager fm = getSupportFragmentManager();
        if(fm != null) {
            MainMenuFragment fragment = new MainMenuFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        int newVolumeInt = settings.getInt("music_volume", 50);
        float newQuietness = (float)(Math.log(100 - newVolumeInt) / Math.log(100));
        setMusicVolume(1 - newQuietness);
        musicPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        musicPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicPlayer.close();
    }

    @Override
    public void playGame(Uri location) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings.edit().putString("last_saved_game", location.toString()).apply();

        Intent intent = new Intent(this, PlayGameActivity.class);
        intent.setData(location);
        startActivityForResult(intent, PLAY_GAME_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == PLAY_GAME_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                if(intent != null) {
                    final Uri save = intent.getData();
                    if (save != null) {
                        SaveFileManager.deleteSave(save); //the user has finished playing this character

                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                        settings.edit().remove("last_saved_game").apply();
                    }
                    //else the character has been suspended
                }
            } else {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                settings.edit().remove("last_saved_game").apply();

                Toast toast = Toast.makeText(this, R.string.play_game_failed, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public void startNewGame(String name, int playerClass, int race) {
        File filesDir = getFilesDir();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        int fileNumber = settings.getInt("fileName", 0);
        String filename = String.valueOf(fileNumber);
        ++fileNumber;
        settings.edit().putInt("fileName", fileNumber).apply();
        File file = new File(filesDir, filename);
        Uri save = Uri.fromFile(file);
        try {
            SaveFileManager.createSave(save, name, playerClass, race);
            playGame(save);
        } catch (IOException e) {
            Toast toast = Toast.makeText(MainMenuActivity.this, R.string.create_new_game_failed, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void quit() {
        finish();
    }

    @Override
    public void setMusicVolume(float newVolume) {
        musicPlayer.setVolume(newVolume);
    }

    /*
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //public native String stringFromJNI();
}
