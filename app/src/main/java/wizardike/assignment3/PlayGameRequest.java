package wizardike.assignment3;

import android.net.Uri;

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
}
