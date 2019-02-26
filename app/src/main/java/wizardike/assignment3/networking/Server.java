package wizardike.assignment3.networking;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import wizardike.assignment3.Engine;
import wizardike.assignment3.levels.Level;

/**
 * Created by Isaac on 25/01/2017.
 */
public class Server implements NetworkConnection, Closeable {
    private ServerSocket serverSocket;
    private Socket clientSocket = null;
    private DataInputStream networkIn;
    private DataOutputStream networkOut;
    private int nextMessageLength = 4;
    private boolean hasMessageLength = false;

    public Server(int portNumber) throws java.io.IOException{
        serverSocket = new ServerSocket(portNumber);
    }

    @Override
    public DataOutputStream getNetworkOut(){
        return this.networkOut;
    }

    private synchronized void synchronizedSetNetworkIn(DataInputStream networkIn){
        this.networkIn = networkIn;
    }

    private synchronized void synchronizedSetNetworkOut(DataOutputStream networkOut){
        this.networkOut = networkOut;
    }

    private synchronized void setClientSocket(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    public synchronized boolean hasFoundClient(){
        return clientSocket != null;
    }

    public synchronized Socket getClientSocket(){ //might not need to be synchronized as hasFoundClient is synchronized
        return clientSocket;
    }

    public void lookForClient(){
        clientSocket = null;
        Thread searchThread = new Thread(){
            @Override
            public void run(){
                try {
                    Socket client = serverSocket.accept();
                    setClientSocket(client);
                    synchronizedSetNetworkIn(new DataInputStream(client.getInputStream()));
                    synchronizedSetNetworkOut(new DataOutputStream(client.getOutputStream()));
                } catch(java.net.SocketException e) {
                    //Do nothing
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        searchThread.start();
    }

    @Override
    public void update(Engine engine, float dt) {
        try {
            while (networkIn.available() >= nextMessageLength) {
                if (hasMessageLength) {
                    int levelIndex = networkIn.readInt();
                    Level level = engine.getMainWorld().getLevelById(levelIndex);
                    level.handleMessage(networkIn);
                    nextMessageLength = 4;
                    hasMessageLength = false;
                } else {
                    nextMessageLength = networkIn.readInt();
                    hasMessageLength = true;
                }
            }
        }
        catch (IOException e) {
            engine.onError();
        }
    }

    @Override
    public void close(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
