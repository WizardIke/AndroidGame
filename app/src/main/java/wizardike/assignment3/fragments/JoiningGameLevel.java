package wizardike.assignment3.fragments;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import wizardike.assignment3.networking.ClientConnection;


/**
 * Created by Isaac on 6/02/2017.
 */
/*
public class JoiningGameLevel implements GameLevel {
    private static final int port = 12000;
    private static final int timeOut = 1000;

    private static final float cancelButtonPosX = 0.34f;
    private static final float cancelButtonPosY = 0.87f;
    private static final float cancelButtonWidth = 0.1f;


    private CharacterCreation previousGameLevel;
    private Socket clientSocket = new Socket();

    public JoiningGameLevel(CharacterCreation previousGameLevel){
        this.previousGameLevel = previousGameLevel;
    }

    public void activate(Main main) {
        try {
            clientSocket.connect(new InetSocketAddress(previousGameLevel.ipBox.text, port), timeOut);
            main.networkConnection = new ClientConnection(clientSocket);
            new NecromancerClient(main, 0.0f, 0.0f);
            main.player = new FireMageClientPlayer(main, previousGameLevel.nameBox.text, 0.5f, 0.5f);
            previousGameLevel.startGame(main);
        } catch (IOException e) {
            previousGameLevel.hostNotFound = true;
            main.gameLevel = previousGameLevel;
        }
    }

    @Override
    public void render(Main main, Graphics2D graphics){
        previousGameLevel.render(main, graphics);
        graphics.setFont(main.arial);
        graphics.setColor(Color.white);
        graphics.drawString("cancel", main.settings.screenSizeX * cancelButtonPosX, main.settings.screenSizeY
                * cancelButtonPosY);
    }

    @Override
    public void update(Main main, float dt){
        previousGameLevel.update(main, dt);
    }

    @Override
    public void keyPressed(KeyEvent event, Main main){
        previousGameLevel.ipBox.keyPressed(event);
    }

    @Override
    public void mousePressed(MouseEvent event, Main main){
        if (event.getX() > main.settings.screenSizeX * cancelButtonPosX && event.getX() < main.settings.screenSizeX * cancelButtonPosX +
                main.settings.screenSizeX * cancelButtonWidth &&
                event.getY() > main.settings.screenSizeY * cancelButtonPosY - main.arial.getSize() &&
                event.getY() < (main.settings.screenSizeY * cancelButtonPosY)) {
            main.gameLevel = previousGameLevel;
        }
        else if (event.getX() > main.settings.screenSizeX * CharacterCreation.finishBoxPosX &&
                event.getX() < main.settings.screenSizeX * CharacterCreation.finishBoxPosX +
                        main.settings.screenSizeX * CharacterCreation.finishBoxWidth &&
                event.getY() > main.settings.screenSizeY * CharacterCreation.finishBoxPosY &&
                event.getY() < (main.settings.screenSizeY * CharacterCreation.finishBoxPosY + main.arial.getSize())) {
            activate(main);
        }
        boolean active = previousGameLevel.ipBox.active;
        previousGameLevel.ipBox.mousePressed(event);
        if(active != previousGameLevel.ipBox.active){
            if(previousGameLevel.ipBox.active && previousGameLevel.hostNotFound){
                previousGameLevel.hostNotFound = false;
            }
        }
    }
}
*/