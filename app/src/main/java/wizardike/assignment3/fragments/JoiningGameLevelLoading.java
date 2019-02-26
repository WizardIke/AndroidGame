package wizardike.assignment3.fragments;

import wizardike.assignment3.networking.ClientConnection;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Isaac on 31/01/2017.
 */
/*
public class JoiningGameLevelLoading implements GameLevel {
    private static final float cancelButtonPosX = 0.31f;
    private static final float cancelButtonPosY = 0.97f;
    private static final float cancelButtonWidth = 0.1f;
    private static final float okButtonPosX = 0.71f;
    private static final float okButtonPosY = 0.97f;
    private static final float okButtonWidth = 0.05f;

    private static final float textBoxPosX = 0.45f;
    private static final float textBoxPosY = 0.915f;
    private static final float textBoxWidth = 0.3f;

    private static final float hostsIPPosX = 0.31f;
    private static final float hostsIPPosY = 0.90f;


    private GameLevel previousGameLevel;
    private TextBox ipBox;
    private boolean ipNotFound = false;
    private Socket clientSocket = new Socket();
    private DataInputStream saveData;
    private int gameObjectCount;

    public JoiningGameLevelLoading(Main main, GameLevel previousGameLevel, DataInputStream saveData,
                                   int gameObjectCount){
        this.previousGameLevel = previousGameLevel;
        this.saveData = saveData;
        this.ipBox = new TextBox(main.settings.screenSizeX * textBoxPosX, main.settings.screenSizeY * textBoxPosY,
                main.settings.screenSizeX * textBoxWidth, (float)main.arial.getSize() * 1.2f);
        this.gameObjectCount = gameObjectCount;
    }

    @Override
    public void render(Main main, Graphics2D graphics){
        this.previousGameLevel.render(main, graphics);
        graphics.setFont(main.arial);
        graphics.setColor(Color.white);
        graphics.drawString("Host's IP:", main.settings.screenSizeX * hostsIPPosX, main.settings.screenSizeY * hostsIPPosY);
        graphics.drawString("cancel", main.settings.screenSizeX * cancelButtonPosX, main.settings.screenSizeY
                * cancelButtonPosY);
        graphics.drawString("ok", main.settings.screenSizeX * okButtonPosX, main.settings.screenSizeY * okButtonPosY);
        if(ipNotFound){
            graphics.setColor(Color.red);
            ipBox.render(graphics);
        }
        else{
            ipBox.render(graphics);
        }
    }

    @Override
    public void update(Main main, float dt){
        ipBox.update(dt);
    }

    @Override
    public void keyPressed(KeyEvent event, Main main){
        ipBox.keyPressed(event);
    }

    @Override
    public void mousePressed(MouseEvent event, Main main){
        if (event.getX() > main.settings.screenSizeX * cancelButtonPosX && event.getX() < main.settings.screenSizeX * cancelButtonPosX +
                main.settings.screenSizeX * cancelButtonWidth &&
                event.getY() > main.settings.screenSizeY * cancelButtonPosY - main.arial.getSize() &&
                event.getY() < (main.settings.screenSizeY * cancelButtonPosY)) {
            main.gameLevel = previousGameLevel;
        }
        else if (event.getX() > main.settings.screenSizeX * okButtonPosX && event.getX() < main.settings.screenSizeX * okButtonPosX +
                main.settings.screenSizeX * okButtonWidth &&
                event.getY() > main.settings.screenSizeY * okButtonPosY - main.arial.getSize() &&
                event.getY() < (main.settings.screenSizeY * okButtonPosY)) {
            try {
                clientSocket.connect(new InetSocketAddress(ipBox.text, 12000), 1000);
                main.networkConnection = new ClientConnection(clientSocket);
                main.player = new FireMageClient(main, saveData);
                main.continueLoading(saveData, gameObjectCount);
            } catch (IOException e) {
                ipNotFound = true;
            }
        }
        boolean active = ipBox.active;
        ipBox.mousePressed(event);
        if(active != ipBox.active){
            if(ipBox.active && ipNotFound){
                ipNotFound = false;
            }
        }
    }
}
*/
