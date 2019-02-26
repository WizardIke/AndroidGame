package wizardike.assignment3.networking;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import wizardike.assignment3.Engine;
import wizardike.assignment3.levels.Level;

/**
 * Created by Isaac on 29/01/2017.
 */
public class ClientConnection implements NetworkConnection, Closeable {
    private Socket clientSocket;
    private DataInputStream networkIn;
    private DataOutputStream networkOut;
    private int nextMessageLength = 4;
    private boolean hasMessageLength = false;

    public ClientConnection(Socket clientSocket) throws java.io.IOException{
        this.clientSocket = clientSocket;
        this.networkIn = new DataInputStream(clientSocket.getInputStream());
        this.networkOut = new DataOutputStream(clientSocket.getOutputStream());
    }

    @Override
    public DataOutputStream getNetworkOut(){
        return this.networkOut;
    }

    @Override
    public void update(Engine engine, float dt){
        try {
            while(networkIn.available() >= nextMessageLength){
                if(hasMessageLength){
                    int levelIndex = networkIn.readInt();
                    Level level = engine.getMainWorld().getLevelById(levelIndex);
                    level.handleMessage(networkIn);
                    nextMessageLength = 4;
                    hasMessageLength = false;
                }
                else{
                    nextMessageLength = networkIn.readInt();
                    hasMessageLength = true;
                }
            }
        } catch (Exception e) {
            engine.onError();
        }
    }

    @Override
    public void close(){
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
