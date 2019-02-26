package wizardike.assignment3.fragments;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Isaac on 16/12/2016.
 */
/*
public class PauseMenu implements GameLevel {
    private static final float optionsPosX = 0.03f;
    private static final float optionsPosY = 0.42f;
    private static final float optionsWidth = 0.18f;


    private GameLevel previousGameLevel;
    private Color backGroundColor = new Color(0.0f, 0.0f, 0.0f, 0.6f);
    private Rectangle2D.Float backgroundRect;
    private AffineTransform uiViewMatrix = new AffineTransform();
    private Font courierNew;
    private Color darkRed = new Color(200, 0, 0);
    private int hoveringOver = 4;

    public PauseMenu(Main main, GameLevel previousGameLevel) {
        this.previousGameLevel = previousGameLevel;
        backgroundRect = new Rectangle2D.Float(0.0f, 0.0f, main.settings.screenSizeX * 0.3f, main.settings.screenSizeY);
        courierNew = new Font("Courier New Bold", 1, (int) (main.settings.screenSizeX * 0.07));
    }

    public PauseMenu(DataInputStream saveData, Main main) throws java.io.IOException {
        backgroundRect = new Rectangle2D.Float(0.0f, 0.0f, main.settings.screenSizeX * 0.3f, main.settings.screenSizeY);
        courierNew = new Font("Courier New Bold", 1, (int) (main.settings.screenSizeX * 0.07));
        switch(saveData.readInt()) {
            case SaveType.Level1 :
                previousGameLevel = new Level1(main);
                break;
        }
    }

    @Override
    public void update(Main main, float dt) throws Exception{
        if(main.networkConnection != null){
            previousGameLevel.update(main, dt);
        }
    }

    @Override
    public void render(Main main, Graphics2D graphics) {
        this.previousGameLevel.render(main, graphics);
        graphics.setTransform(uiViewMatrix);
        graphics.setColor(backGroundColor);
        graphics.fill(backgroundRect);
        if(main.networkConnection == null){
            graphics.setColor(darkRed);
            graphics.setFont(courierNew);
            graphics.drawString("Paused", main.settings.screenSizeX * 0.025f, main.settings.screenSizeY * 0.2f);
            //  DRAW LINE UNDER PAUSE.
            graphics.fillRect((int) (main.settings.screenSizeX * 0.038), (int) (main.settings.screenSizeY * 0.22),
                    (int)(0.225f * main.settings.screenSizeX), (int)(0.00926f * main.settings.screenSizeY));
        }

        graphics.setFont(main.arial);
        graphics.setColor(Color.white);
        if(hoveringOver == 0){
            graphics.fillRect((int) (main.settings.screenSizeX * 0.001f), (int) (main.settings.screenSizeY * 0.31f),
                    (int)(0.229f * main.settings.screenSizeX), (int)(0.05556f * main.settings.screenSizeY));
            graphics.setColor(Color.black);
            graphics.drawString("Resume", main.settings.screenSizeX * 0.03f,
                    main.settings.screenSizeY * 0.36f);
            graphics.setColor(Color.white);
        }
        else{
            graphics.drawString("Resume", main.settings.screenSizeX * 0.03f,
                    main.settings.screenSizeY * 0.36f);
        }
        if(hoveringOver == 1){
            graphics.fillRect((int) (main.settings.screenSizeX * 0.001f), (int) (main.settings.screenSizeY * 0.37f),
                    (int)(0.229f * main.settings.screenSizeX), (int)(0.05556f * main.settings.screenSizeY));
            graphics.setColor(Color.black);
            graphics.drawString("Options", main.settings.screenSizeX * optionsPosX,
                    main.settings.screenSizeY * optionsPosY);
            graphics.setColor(Color.white);
        }
        else{
            graphics.drawString("Options", main.settings.screenSizeX * optionsPosX,
                    main.settings.screenSizeY * optionsPosY);
        }
        if(hoveringOver == 2){
            graphics.fillRect((int) (main.settings.screenSizeX * 0.001f), (int) (main.settings.screenSizeY * 0.43f),
                    (int)(0.229f * main.settings.screenSizeX), (int)(0.05556f * main.settings.screenSizeY));
            graphics.setColor(Color.black);
            graphics.drawString("Main Menu", main.settings.screenSizeX * 0.03f,
                    main.settings.screenSizeY * 0.48f);
            graphics.setColor(Color.white);
        }
        else{
            graphics.drawString("Main Menu", main.settings.screenSizeX * 0.03f,
                    main.settings.screenSizeY * 0.48f);
        }
        if(hoveringOver == 3){
            graphics.fillRect((int) (main.settings.screenSizeX * 0.001f), (int) (main.settings.screenSizeY * 0.49f),
                    (int)(0.229f * main.settings.screenSizeX), (int)(0.05556f * main.settings.screenSizeY));
            graphics.setColor(Color.black);
            graphics.drawString("Save and Quit", main.settings.screenSizeX * 0.03f,
                    main.settings.screenSizeY * 0.54f);
            graphics.setColor(Color.white);
        }
        else{
            graphics.drawString("Save and Quit", main.settings.screenSizeX * 0.03f,
                    main.settings.screenSizeY * 0.54f);
        }
    }

    @Override
    public void keyPressed(KeyEvent event, Main main) throws Exception {
        switch(event.getKeyCode()) {
            case KeyEvent.VK_TAB :
                main.gameLevel = this.previousGameLevel;
                break;
            case KeyEvent.VK_UP :
                --hoveringOver;
                if(hoveringOver == -1){
                    hoveringOver = 3;
                }
                if(main.settings.buttonSoundFx){
                    main.soundFX.beep.play();
                }
                break;
            case KeyEvent.VK_DOWN :
                ++hoveringOver;
                if(hoveringOver >= 4){
                    hoveringOver = 0;
                }
                if(main.settings.buttonSoundFx){
                    main.soundFX.beep.play();
                }
                break;
            case KeyEvent.VK_ENTER :
                switch(hoveringOver){
                    case 0 :
                        main.gameLevel = this.previousGameLevel;
                        break;
                    case 1 :
                        main.gameLevel = new OptionsMenu(main, this);
                        break;
                    case 2 :
                        try {
                            main.settings.updateSettingsFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        main.saveGame();
                        this.previousGameLevel.destroy(main);
                        main.gameLevel = new MainMenu(main);
                        if(main.networkConnection != null){
                            try {
                                main.networkConnection.getNetworkOut().writeInt(1);
                                main.networkConnection.getNetworkOut().write(connectionLost);
                                main.networkConnection.close();
                                main.networkConnection = null;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case 3 :
                        main.saveGame();
                        try {
                            main.settings.updateSettingsFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        this.previousGameLevel.destroy(main);
                        main.window.frame.dispatchEvent(new WindowEvent(main.window.frame, WindowEvent.WINDOW_CLOSING));
                        if(main.networkConnection != null){
                            try {
                                main.networkConnection.getNetworkOut().writeInt(1);
                                main.networkConnection.getNetworkOut().write(connectionLost);
                                main.networkConnection.close();
                                main.networkConnection = null;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
                break;
            default :
                if(main.networkConnection != null){
                    this.previousGameLevel.keyPressed(event, main);
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent event, Main main){
        if(main.networkConnection != null){
            previousGameLevel.keyReleased(event, main);
        }
    }

    @Override
    public void mousePressed(MouseEvent event, Main main) throws Exception {
        switch(event.getButton()) {
            case MouseEvent.BUTTON1:
                if(event.getX() > main.settings.screenSizeX * 0.03 && event.getX() < main.settings.screenSizeX * 0.03 +
                        main.settings.screenSizeX * 0.2 &&
                        event.getY() > main.settings.screenSizeY * 0.36 - main.arial.getSize() &&
                        event.getY() < (main.settings.screenSizeY * 0.36)){
                    main.gameLevel = previousGameLevel;
                }
                if (event.getX() > main.settings.screenSizeX * optionsPosX && event.getX() < main.settings.screenSizeX * optionsPosX +
                        main.settings.screenSizeX * optionsWidth &&
                        event.getY() > main.settings.screenSizeY * optionsPosY - main.arial.getSize() &&
                        event.getY() < (main.settings.screenSizeY * optionsPosY)) {
                    main.gameLevel = new OptionsMenu(main, this);
                }
                else if (event.getX() > main.settings.screenSizeX * 0.03f && event.getX() < main.settings.screenSizeX * 0.03f +
                        main.settings.screenSizeX * 0.2f &&
                        event.getY() > main.settings.screenSizeY * 0.48f - main.arial.getSize() &&
                        event.getY() < (main.settings.screenSizeY * 0.48f)) {
                    try {
                        main.settings.updateSettingsFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    main.saveGame();
                    this.previousGameLevel.destroy(main);
                    main.gameLevel = new MainMenu(main);
                    if(main.networkConnection != null){
                        try {
                            main.networkConnection.getNetworkOut().writeInt(1);
                            main.networkConnection.getNetworkOut().write(connectionLost);
                            main.networkConnection.close();
                            main.networkConnection = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if (event.getX() > main.settings.screenSizeX * 0.03f && event.getX() < main.settings.screenSizeX * 0.03f +
                        main.settings.screenSizeX * 0.2f &&
                        event.getY() > main.settings.screenSizeY * 0.54f - main.arial.getSize() &&
                        event.getY() < (main.settings.screenSizeY * 0.54f)) {
                    main.saveGame();
                    try {
                        main.settings.updateSettingsFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    this.previousGameLevel.destroy(main);
                    main.window.frame.dispatchEvent(new WindowEvent(main.window.frame, WindowEvent.WINDOW_CLOSING));
                    if(main.networkConnection != null){
                        try {
                            main.networkConnection.getNetworkOut().writeInt(1);
                            main.networkConnection.getNetworkOut().write(connectionLost);
                            main.networkConnection.close();
                            main.networkConnection = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
        if(main.networkConnection != null){
            this.previousGameLevel.mousePressed(event, main);
        }
    }

    @Override
    public void mouseReleased(MouseEvent event, Main main){
        if(main.networkConnection != null){
            this.previousGameLevel.mouseReleased(event, main);
        }
    }

    @Override
    public void save(DataOutputStream saveData) {
        try {
            saveData.writeInt(SaveType.PauseMenu);
            this.previousGameLevel.save(saveData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopMusic(Main main){
        previousGameLevel.stopMusic(main);
    }

    @Override
    public void startMusic(Main main){
        previousGameLevel.startMusic(main);
    }

    @Override
    public void mouseMoved (MouseEvent event, Main main) {
        double mouseX = event.getX();
        double mouseY = event.getY();
        if (((mouseX >= (main.settings.screenSizeX * 0.03f) &&
                mouseX <= (main.settings.screenSizeX * 0.03f) + 0.229f * main.settings.screenSizeX &&
                mouseY >= (main.settings.screenSizeY * 0.31f) &&
                mouseY <= (main.settings.screenSizeY * 0.31f) + 0.06f * main.settings.screenSizeY))) {
            if(hoveringOver != 0){
                hoveringOver = 0;
                if(main.settings.buttonSoundFx){
                    main.soundFX.beep.play();
                }
            }
        } else if (((mouseX >= (main.settings.screenSizeX * 0.03f) &&
                mouseX <= (main.settings.screenSizeX * 0.03f) + 0.229f * main.settings.screenSizeX &&
                mouseY >= (main.settings.screenSizeY * 0.37f) &&
                mouseY <= (main.settings.screenSizeY * 0.37f) + 0.06f * main.settings.screenSizeY))) {
            if(hoveringOver != 1){
                hoveringOver = 1;
                if(main.settings.buttonSoundFx){
                    main.soundFX.beep.play();
                }
            }
        } else if (((mouseX >= (main.settings.screenSizeX * 0.03f) &&
                mouseX <= (main.settings.screenSizeX * 0.03f) + 0.229f * main.settings.screenSizeX &&
                mouseY >= (main.settings.screenSizeY * 0.43f) &&
                mouseY <= (main.settings.screenSizeY * 0.43f) + 0.06f * main.settings.screenSizeY))) {
            if(hoveringOver != 2){
                hoveringOver = 2;
                if(main.settings.buttonSoundFx){
                    main.soundFX.beep.play();
                }
            }
        } else if (((mouseX >= (main.settings.screenSizeX * 0.03f) &&
                mouseX <= (main.settings.screenSizeX * 0.03f) + 0.229f * main.settings.screenSizeX &&
                mouseY >= (main.settings.screenSizeY * 0.49f) &&
                mouseY <= (main.settings.screenSizeY * 0.49f) + 0.06f * main.settings.screenSizeY))) {
            if(hoveringOver != 3){
                hoveringOver = 3;
                if(main.settings.buttonSoundFx){
                    main.soundFX.beep.play();
                }
            }
        }
        else {
            hoveringOver = 4;  //don't play sound
        }
    }
}
*/
