package it.uniba.dib.sms22238.game.views.PViews;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import it.uniba.dib.sms22238.R;

public class Background {

    public Bitmap background;
    public int x=0,y=0;

    public Background(Resources res){

        background= BitmapFactory.decodeResource(res, R.drawable.travel_background);

    }
}
