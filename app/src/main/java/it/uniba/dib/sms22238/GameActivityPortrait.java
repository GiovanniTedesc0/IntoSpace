package it.uniba.dib.sms22238;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import it.uniba.dib.sms22238.game.views.ViewTravel;

public class GameActivityPortrait extends AppCompatActivity {

    private short flag;
    private ViewTravel viewTravel;
    private Sound sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        flag=getIntent().getExtras().getShort("flag");
        //imposta lo schermo in full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //ottiene le dimensioni dello schermo a run time
        Point point=new Point();
        getWindowManager().getDefaultDisplay().getSize(point);

        //e le passa al costruttore della view

        viewTravel = new ViewTravel(this,flag,point.y,point.x);

        sound=new Sound(this,1); //flag 1 per la canzone portrait

        //setta la view del gioco

        setContentView(viewTravel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewTravel.resume();
        sound.play();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewTravel.pause();
        sound.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sound.stop();
    }

    public void callManager(short flag, short flagActivity){
        Bundle bundle=new Bundle(2);
        bundle.putShort("flag", flag); //setta il flag nel boundle uguale a 0 perchè sta chiamando il museo
        bundle.putShort("flagActivity",flagActivity);
        Intent i = new Intent(GameActivityPortrait.this, ManagerActivity.class);
        i.putExtras(bundle);
        this.startActivity(i);
    }

    public void callPlanet(short flag){
        Bundle bundle=new Bundle(1);
        bundle.putShort("flagPlanet", flag); //setta il flag nel boundle uguale a 0 o 1
        Intent i = new Intent(GameActivityPortrait.this, GameActivityLandscape.class);
        i.putExtras(bundle);
        this.startActivity(i);
        this.finish();
    }
}