package wizardike.assignment3.fragments;

import wizardike.assignment3.networking.Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Isaac on 31/01/2017.
 */
/*
public class WaitingForOtherPlayerLevelLoading implements GameLevel {
    private static final float cancelButtonPosX = 0.31f;
    private static final float cancelButtonPosY = 0.97f;
    private static final float cancelButtonWidth = 0.1f;

    private static final float waitingForOtherPlayerLabelPosX = 0.31f;
    private static final float waitingForOtherPlayerLabelPosY = 0.92f;

    private static final float rectPosX = 0.3f;
    private static final float rectPosY = 0.86f;
    private static final float rectWidth = 0.65f;
    private static final float rectHeight = 0.12f;


    private GameLevel previousGameLevel;
    private Server server;
    private String ip = "Unknown";
    private DataInputStream saveData;
    private Rectangle.Float cancelButtonRect;
    private int gameObjectCount;

    public WaitingForOtherPlayerLevelLoading(Main main, GameLevel previousGameLevel, DataInputStream saveData,
                                             int gameObjectCount) throws IOException{

        this.previousGameLevel = previousGameLevel;
        this.saveData = saveData;
        this.cancelButtonRect = new Rectangle2D.Float(main.settings.screenSizeX * rectPosX, main.settings.screenSizeY * rectPosY,
                main.settings.screenSizeX * rectWidth, main.settings.screenSizeY * rectHeight);
        this.server = new Server(12000);
        this.server.lookForClient();
        this.gameObjectCount = gameObjectCount;

        //find IP
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp() || networkInterface.isVirtual() ||
                        networkInterface.isPointToPoint())
                    continue;

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                if(addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    ip = address.getHostAddress();
                }//else ip = "Unknown
            }
        } catch (SocketException e) {
            //ip = "Unknown"; //no need to handle
        }
    }

    @Override
    public void render(Main main, Graphics2D graphics){
        previousGameLevel.render(main, graphics);
        graphics.setFont(main.arial);
        graphics.setColor(Color.white);
        graphics.draw(cancelButtonRect);
        graphics.drawString("waiting for other player. Your IP:" + ip, main.settings.screenSizeX *
                waitingForOtherPlayerLabelPosX, main.settings.screenSizeY * waitingForOtherPlayerLabelPosY);
        graphics.drawString("cancel", main.settings.screenSizeX * cancelButtonPosX,
                main.settings.screenSizeY * cancelButtonPosY);
    }

    @Override
    public void update(Main main, float dt){
        if (server.hasFoundClient()) {
            main.networkConnection = server;
            try {
                main.player = new NecromancerHostPlayer(main, saveData);
            } catch (IOException e) {
                try {
                    main.networkConnection.close();
                    main.gameLevel = new MainMenu(main);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            main.continueLoading(saveData, gameObjectCount);
        }
    }

    @Override
    public void mousePressed(MouseEvent event, Main main){
        if (event.getX() > main.settings.screenSizeX * cancelButtonPosX && event.getX() < main.settings.screenSizeX * cancelButtonPosX +
                main.settings.screenSizeX * cancelButtonWidth &&
                event.getY() > main.settings.screenSizeY * cancelButtonPosY - main.arial.getSize() &&
                event.getY() < (main.settings.screenSizeY * cancelButtonPosY)) {
            server.close();
            main.gameLevel = previousGameLevel;
        }
    }
}
*/
