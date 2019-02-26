package wizardike.assignment3;

import android.net.Uri;
import android.support.v4.app.Fragment;

public abstract class PlayGameRequest {
    public enum GameState {
        suspended,
        finished,
        error
    }
    public Uri saveFile;

    PlayGameRequest(Uri saveFile) {
        this.saveFile = saveFile;
    }

    public abstract void onPlayingEnded(GameState state);

    public abstract void playMusic(int[] resourceIds);
    public abstract void addFragment(int id, Fragment fragment);
}
