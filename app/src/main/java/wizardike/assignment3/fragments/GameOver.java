package wizardike.assignment3.fragments;

import java.io.File;
import java.io.IOException;

import wizardike.assignment3.HighScores;

/**
 * Created by Isaac on 14/01/2017.
 */
/*
public class GameOver implements GameLevel{
    private BufferedImage ripImage;
    private HighScores highScores = new HighScores();
    private Font courier;
    private int hoveringOver = 2;
    private boolean stopMusic = false;

    public GameOver(Main main) throws java.io.IOException{
        ripImage = ImageIO.read(new File("data/graphics/tombstone7.png"));
        this.courier = new Font("Courier New Bold", 1, (int) (main.settings.screenSizeX * 0.05));
        this.highScores.addHighScore(main.player.getName(), main.player.getAwesomeness());

        try {
            main.currentMusic = AudioSystem.getClip();
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(
                    "data/music/Demon_Your_Soul_is_mine-BlueMann-1903732045.wav"));
            main.currentMusic.open(audio);
            audio.close();
            main.currentMusic.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if(event.getType() == LineEvent.Type.STOP){
                        main.currentMusic.close();
                        if(stopMusic) {
                            try {

                                try {
                                    main.currentMusic = AudioSystem.getClip();
                                    AudioInputStream audio = AudioSystem.getAudioInputStream(new File(
                                            "data/music/Heroic_Demise_New_.wav"));
                                    audio.close();
                                    main.currentMusic.setLoopPoints(44100 * 2, -1);
                                    main.currentMusic.setFramePosition(44100 * 2);
                                    main.currentMusic.loop(Clip.LOOP_CONTINUOUSLY);
                                    main.currentMusic.open(audio);
                                } catch (LineUnavailableException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            catch (UnsupportedAudioFileException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            try {
                                main.currentMusic = AudioSystem.getClip();
                                AudioInputStream audio = AudioSystem.getAudioInputStream(new File(
                                        "data/music/Creepy_Laugh-Adam_Webb-235643261.wav"));
                                main.currentMusic.open(audio);
                                audio.close();
                                main.currentMusic.addLineListener((LineEvent event2) -> {
                                    if(event2.getType() == LineEvent.Type.STOP){
                                        main.currentMusic.close();
                                        try {
                                            main.currentMusic = AudioSystem.getClip();
                                            AudioInputStream audio2 = AudioSystem.getAudioInputStream(new File(
                                                    "data/music/Heroic_Demise_New_.wav"));
                                            main.currentMusic.open(audio2);
                                            audio2.close();
                                            main.currentMusic.setLoopPoints(44100 * 2, -1);
                                            main.currentMusic.setFramePosition(44100 * 2);
                                            main.currentMusic.loop(Clip.LOOP_CONTINUOUSLY);
                                        }catch(IOException | LineUnavailableException | UnsupportedAudioFileException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                main.currentMusic.start();
                            }
                            catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            });
            main.currentMusic.start();
        }
        catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(Main main, Graphics2D graphics) {
        graphics.setBackground(Color.black);
        graphics.clearRect(0, 0, (int) (main.settings.screenSizeX), (int) (main.settings.screenSizeY));

        graphics.drawImage(ripImage, (int)(0.09479f * main.settings.screenSizeX), (int)(-0.13889f * main.settings.screenSizeY),
                (int)(0.8333f * main.settings.screenSizeX), (int)(1.11f * main.settings.screenSizeY), null);

        graphics.setColor(Color.red);
        graphics.setFont(courier);
        graphics.drawString("GAME OVER", (int) (main.settings.screenSizeX * 0.39f), (int) (main.settings.screenSizeY * 0.084f));

        graphics.setFont(main.arial);
        graphics.setColor(Color.white);

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

        for(int i = 0; i < 6 && i < this.highScores.playerNames.size(); ++i){
            graphics.drawString(this.highScores.playerNames.get(i) + ' ' + this.highScores.scores.get(i),
                    main.settings.screenSizeX * .3f, main.settings.screenSizeY * (0.6f + 0.05f * i));
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
    public void keyPressed(KeyEvent event, Main main) {
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
                main.window.frame.dispatchEvent(new WindowEvent(main.window.frame, WindowEvent.WINDOW_CLOSING));
            } else if (hoveringOver == 1) {
                try {
                    main.gameLevel = new MainMenu(main);
                } catch(java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent event, Main main) {
        double mouseX = event.getX();
        double mouseY = event.getY();
        if (((mouseX >= (main.settings.screenSizeX * 0.75f) &&
                mouseX <= (main.settings.screenSizeX * 0.75f) + 0.18229f * main.settings.screenSizeX &&
                mouseY >= (main.settings.screenSizeY * 0.9f) &&
                mouseY <= (main.settings.screenSizeY * 0.9f) + 0.0648f * main.settings.screenSizeY))) {
            main.window.frame.dispatchEvent(new WindowEvent(main.window.frame, WindowEvent.WINDOW_CLOSING));
        } else if (((mouseX >= (main.settings.screenSizeX * 0.1f) &&
                mouseX <= (main.settings.screenSizeX * 0.1f) + 0.18229f * main.settings.screenSizeX &&
                mouseY >= (main.settings.screenSizeY * 0.9f) &&
                mouseY <= (main.settings.screenSizeY * 0.9f) + 0.0648f * main.settings.screenSizeY))) {
            stopMusic = true;
            main.currentMusic.stop();
            try {
                main.gameLevel = new MainMenu(main);
            } catch(java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }
}
*/
