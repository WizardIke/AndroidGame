package wizardike.assignment3.fragments;

import java.io.File;
import java.io.IOException;

/**
 * Created by Isaac on 29/01/2017.
 */
/*
public class VictoryLevel implements GameLevel {
    private static final float skeletonPosX = 0.375f;
    private static final float skeletonPosY = 0.3f;
    private static final float skeletonWidth = 0.155f;
    private static final float skeletonHeight = 0.28f;

    private static final double skeletonAnimationMaxTime = 3.0;


    private Clip clapping;
    private Font courierNew;
    private int hoveringOver = 2;
    private Image[] skeletonSpritesheet;
    private double skeletonAnimationTime = skeletonAnimationMaxTime * 0.5;

    public VictoryLevel(Main main){
        try {
            this.courierNew  = new Font ("Courier New Bold", 1, (int) (main.settings.screenSizeX * 0.05));

            this.skeletonSpritesheet = new Image[5];
            BufferedImage temp = ImageIO.read(new File("data/graphics/skeletonspritesheet.png"));
            for(int x = 0; x < 5; ++x){
                this.skeletonSpritesheet[x] = temp.getSubimage(x * 64, 20 * 64, 64, 64);
            }

            clapping = AudioSystem.getClip();
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File("data/music/Crowd_Applause-Thore-672347161.wav"));
            clapping.open(audio);
            clapping.loop(Clip.LOOP_CONTINUOUSLY);
            audio.close();
        }

        catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(Main main, Graphics2D graphics){
        graphics.setBackground(Color.black);
        graphics.clearRect(0, 0, (int) (main.settings.screenSizeX), (int) (main.settings.screenSizeY));
        graphics.setFont(courierNew);
        graphics.setColor(Color.white);
        graphics.drawString("- VICTORY IS YOURS -", (int) (main.settings.screenSizeX * .2f),
                (int) (main.settings.screenSizeY * .3f));
        graphics.setFont(main.arial);

        if(hoveringOver == 0){
            graphics.fillRect((int) (main.settings.screenSizeX * 0.75f), (int) (main.settings.screenSizeY * 0.9f),
                    (int)(0.18229f * main.settings.screenSizeX), (int)(0.0648f * main.settings.screenSizeY));
            graphics.setColor(Color.black);
            graphics.drawString("Quit", (int) (main.settings.screenSizeX * 0.81), (int) (main.settings.screenSizeY * 0.95));
            graphics.setColor(Color.white);
        }
        else{
            graphics.drawString("Quit", (int) (main.settings.screenSizeX * 0.81), (int) (main.settings.screenSizeY * 0.95));
        }
        if(hoveringOver == 1){
            graphics.fillRect((int) (main.settings.screenSizeX * 0.1), (int) (main.settings.screenSizeY * 0.9),
                    (int)(0.18229f * main.settings.screenSizeX), (int)(0.0648f * main.settings.screenSizeY));
            graphics.setColor(Color.black);
            graphics.drawString("Menu", (int) (main.settings.screenSizeX * 0.15), (int) (main.settings.screenSizeY * 0.95));
            graphics.setColor(Color.white);
        }
        else{
            graphics.drawString("Menu", (int) (main.settings.screenSizeX * 0.15), (int) (main.settings.screenSizeY * 0.95));
        }
        int idex = (int)(skeletonAnimationTime / skeletonAnimationMaxTime * 9);
        if(idex > 3){
            idex = 8 - idex;
        }
        graphics.drawImage(skeletonSpritesheet[idex],
                (int)(skeletonPosX * main.settings.screenSizeX), (int)(skeletonPosY * main.settings.screenSizeY),
                (int)(main.settings.screenSizeX * skeletonWidth), (int)(main.settings.screenSizeY * skeletonHeight), null);
    }

    @Override
    public void update(Main main, float dt){
        skeletonAnimationTime += dt;
        if(skeletonAnimationTime >= skeletonAnimationMaxTime){
            skeletonAnimationTime = 0.0;
        }
    }


    @Override
    public void mouseMoved (MouseEvent event, Main main) {
        double mouseX = event.getX();
        double mouseY = event.getY();
        if (((mouseX >= (main.settings.screenSizeX * 0.75f) &&
                mouseX <= (main.settings.screenSizeX * 0.75f) + 0.18229f * main.settings.screenSizeX &&
                mouseY >= (main.settings.screenSizeY * 0.9f) &&
                mouseY <= (main.settings.screenSizeY * 0.9f) + 0.0648f * main.settings.screenSizeY))) {
            if(hoveringOver != 0){
                hoveringOver = 0;
                if(main.settings.buttonSoundFx){
                    main.soundFX.beep.play();
                }
            }
        } else if (((mouseX >= (main.settings.screenSizeX * 0.1f) &&
                mouseX <= (main.settings.screenSizeX * 0.1f) + 0.18229f * main.settings.screenSizeX &&
                mouseY >= (main.settings.screenSizeY * 0.9f) &&
                mouseY <= (main.settings.screenSizeY * 0.9f) + 0.0648f * main.settings.screenSizeY))) {
            if(hoveringOver != 1){
                hoveringOver = 1;
                if(main.settings.buttonSoundFx){
                    main.soundFX.beep.play();
                }
            }
        } else {
            hoveringOver = 3;  //don't play sound
        }
    }

    @Override
    public void keyPressed(KeyEvent event, Main main) throws IOException {
        if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
            if(hoveringOver != 0){
                hoveringOver = 0;
                if(main.settings.buttonSoundFx){
                    main.soundFX.beep.play();
                }
            }
        }
        else if (event.getKeyCode() == KeyEvent.VK_LEFT) {
            if(hoveringOver != 1){
                hoveringOver = 1;
                if(main.settings.buttonSoundFx){
                    main.soundFX.beep.play();
                }
            }
        }

        else if (event.getKeyCode() == KeyEvent.VK_ENTER) {
            if (hoveringOver == 0) {
                clapping.stop();
                clapping.close();
                main.window.frame.dispatchEvent(new WindowEvent(main.window.frame, WindowEvent.WINDOW_CLOSING));
            } else if (hoveringOver == 1) {
                clapping.stop();
                clapping.close();
                main.gameLevel = new MainMenu(main);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent event, Main main) throws IOException {
        double mouseX = event.getX();
        double mouseY = event.getY();
        if (((mouseX >= (main.settings.screenSizeX * 0.75f) &&
                mouseX <= (main.settings.screenSizeX * 0.75f) + 0.18229f * main.settings.screenSizeX &&
                mouseY >= (main.settings.screenSizeY * 0.9f) &&
                mouseY <= (main.settings.screenSizeY * 0.9f) + 0.0648f * main.settings.screenSizeY))) {
            clapping.stop();
            clapping.close();
            main.window.frame.dispatchEvent(new WindowEvent(main.window.frame, WindowEvent.WINDOW_CLOSING));
        } else if (((mouseX >= (main.settings.screenSizeX * 0.1f) &&
                mouseX <= (main.settings.screenSizeX * 0.1f) + 0.18229f * main.settings.screenSizeX &&
                mouseY >= (main.settings.screenSizeY * 0.9f) &&
                mouseY <= (main.settings.screenSizeY * 0.9f) + 0.0648f * main.settings.screenSizeY))) {
            clapping.stop();
            clapping.close();
            main.gameLevel = new MainMenu(main);
        }
    }
}
*/