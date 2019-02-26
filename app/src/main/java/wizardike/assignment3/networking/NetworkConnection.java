package wizardike.assignment3.networking;

import java.io.Closeable;
import java.io.DataOutputStream;

import wizardike.assignment3.Engine;

/**
 * Created by Isaac on 29/01/2017.
 */
public interface NetworkConnection extends Closeable{
    void update(Engine engine, float frameTime) throws Exception;
    DataOutputStream getNetworkOut();
}
