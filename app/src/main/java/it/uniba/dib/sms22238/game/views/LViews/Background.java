package it.uniba.dib.sms22238.game.views.LViews;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import it.uniba.dib.sms22238.R;

public class Background {

    public int screenX,screenY;
    public Bitmap background;
    SharedPreferences  prefs;
    public int x= 0;
    public int y=0;


    public Background(Resources res,int screenX, int screenY,short flagPlanet, int x){

        this.x = x;

        if(flagPlanet==0) {
            background = BitmapFactory.decodeResource(res, R.drawable.moon_background_grosso);
            background = Bitmap.createScaledBitmap(background, screenX*5, screenY, false);
        }else{
            background = BitmapFactory.decodeResource(res, R.drawable.background_mars);
            background = Bitmap.createScaledBitmap(background, screenX*5, screenY, false);
        }

    }





}
