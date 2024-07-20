//code to play music from: https://www.muradnabizade.com/backgroundmusicjava
//music (copy-right free) from: https://www.youtube.com/watch?v=mRN_T6JkH-c&list=PLwJjxqYuirCLkq42mGw4XKGQlpZSfxsYd

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.*;
public class Music {
    public void playMusic(String musicLoc){
        try {
            File musicPath = new File(musicLoc);
            if(musicPath.exists()){
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}

//to use:
//copy this into your Main mini-game class:

//code to play music from: https://www.muradnabizade.com/backgroundmusicjava
//music (copy-right free) from: https://www.youtube.com/watch?v=mRN_T6JkH-c&list=PLwJjxqYuirCLkq42mGw4XKGQlpZSfxsYd
// String filePath = "src/Audio/RPReplay_Final1714956567.wav";
//Music play = new Music();
//play.playMusic(filePath);
//end of borrowed code