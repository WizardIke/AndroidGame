package wizardike.assignment3;

import wizardike.assignment3.Engine;

public interface Startable {
    //must be called on the game thread
    void start(Engine engine);
    //must be called on the game thread
    void stop(Engine engine);
}
