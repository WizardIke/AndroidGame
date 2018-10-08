package wizardike.assignment3;

public class LoadingScreen {

    public interface Callback {
        void onLoadComplete(LoadingScreen loadingScreen);
    }

    LoadingScreen(Engine engine, Callback callback) {
        callback.onLoadComplete(this);
    }

    public void start(Engine engine) {

    }

    public void stop(Engine engine) {

    }
}
