package wizardike.assignment3.fragments;

import wizardike.assignment3.networking.Server;

import java.io.IOException;

/**
 * Created by Isaac on 6/02/2017.
 */
/*
public class WaitingForOtherPlayerLevel implements GameLevel {
    private static final int port = 12000;

    private static final float cancelButtonPosX = 0.34f;
    private static final float cancelButtonPosY = 0.97f;
    private static final float cancelButtonWidth = 0.1f;

    private static final float waitingForOtherPlayerLabelPosX = 0.34f;
    private static final float waitingForOtherPlayerLabelPosY = 0.92f;

    private static final float rectPosX = 0.33f;
    private static final float rectPosY = 0.86f;
    private static final float rectWidth = 0.35f;
    private static final float rectHeight = 0.12f;


    private CharacterCreation previousGameLevel;
    private Server server;
    private Rectangle.Float cancelButtonRect;

    public WaitingForOtherPlayerLevel(Main main, CharacterCreation previousGameLevel) throws IOException{
        this.previousGameLevel = previousGameLevel;
        this.cancelButtonRect = new Rectangle2D.Float(main.settings.screenSizeX * rectPosX, main.settings.screenSizeY * rectPosY,
                main.settings.screenSizeX * rectWidth, main.settings.screenSizeY * rectHeight);
        this.server = new Server(port);
        this.server.lookForClient();
    }

    @Override
    public void render(Main main, Graphics2D graphics){
        previousGameLevel.render(main, graphics);
        graphics.setFont(main.arial);
        graphics.setColor(Color.white);
        graphics.draw(cancelButtonRect);
        graphics.drawString("waiting for other player", main.settings.screenSizeX *
                waitingForOtherPlayerLabelPosX, main.settings.screenSizeY * waitingForOtherPlayerLabelPosY);
        graphics.drawString("cancel", main.settings.screenSizeX * cancelButtonPosX,
                main.settings.screenSizeY * cancelButtonPosY);
    }

    @Override
    public void update(Main main, float dt){
        previousGameLevel.update(main, dt);
        if (server.hasFoundClient()) {
            main.networkConnection = server;
            try {
                main.player =  new NecromancerHostPlayer(main, previousGameLevel.nameBox.text, 0.0f, 0.0f);
                new FireMageHost(main, 0.5f, 0.5f);
            } catch (IOException e) {
                try {
                    main.networkConnection.close();
                    main.gameLevel = new MainMenu(main);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            try {
                previousGameLevel.startGame(main);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
