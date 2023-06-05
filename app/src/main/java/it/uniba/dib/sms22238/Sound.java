package it.uniba.dib.sms22238;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class Sound {
    private SharedPreferences prefs;
    private SoundPool soundPool;
    private MediaPlayer mediaPlayer;
    private int soundTravel;
    private int flag;
    private Context context;

    public Sound(Context context, int flag){
        prefs=context.getSharedPreferences("game",context.MODE_PRIVATE); // scrive nel DB del telefono
        this.context=context;
        this.flag=flag;
    }

    public void play(){
        if(!prefs.getBoolean("isMute",false)){
            if(mediaPlayer==null){
                if(flag==0){ //musica museo
                    mediaPlayer=MediaPlayer.create(context,R.raw.museumsound);
                }
                else if(flag==1){ //musica portrait
                    mediaPlayer=MediaPlayer.create(context,R.raw.travelsound);
                }
                else if(flag==2){ //musica landscape
                    mediaPlayer=MediaPlayer.create(context,R.raw.planetsound);
                }
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        stop();
                    }
                });
            }
            mediaPlayer.start();
        }
    }
    public void pause(){
        if(!prefs.getBoolean("isMute",false)) {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        }
    }
    public void stop(){
        if(!prefs.getBoolean("isMute",false)) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }
}
