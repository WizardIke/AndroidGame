package wizardike.assignment3;

public class LoadingScreen implements Startable {

    public interface Callback {
        void onLoadComplete(LoadingScreen loadingScreen);
    }

    LoadingScreen(Engine engine, Callback callback) {
        callback.onLoadComplete(this);
    }

    @Override
    public void start(Engine engine) {

    }

    @Override
    public void stop(Engine engine) {

    }
}
