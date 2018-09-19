package wizardike.assignment3;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainMenuActivity extends AppCompatActivity {
    private static final int PLAY_GAME_REQUEST_CODE = 0;

    private AmbientMusicPlayer musicPlayer;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(getFilesDir(), "test465560783456187684805642087653456034");
                Uri save = Uri.fromFile(file);
                try {
                    SaveFileManager.createSave(save);
                    playGame(save);
                } catch (IOException e) {
                    Toast toast = Toast.makeText(MainMenuActivity.this, R.string.play_game_failed, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        musicPlayer = new AmbientMusicPlayer(new int[]{R.raw.tropic_strike, R.raw.soliloquy}, this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        musicPlayer.reset();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void playGame(Uri location) {
        Intent intent = new Intent(MainMenuActivity.this, PlayGameActivity.class);
        intent.setData(location);
        MainMenuActivity.this.startActivityForResult(intent, PLAY_GAME_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == PLAY_GAME_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                if(intent != null) {
                    final Uri save = intent.getData();
                    if (save != null) {
                        SaveFileManager.deleteSave(save); //the user has finished playing this character
                    }
                    //else the character has been suspended
                }
            } else {
                Toast toast = Toast.makeText(this, R.string.play_game_failed, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
