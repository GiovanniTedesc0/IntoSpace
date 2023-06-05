package it.uniba.dib.sms22238.game.views.LViews;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import it.uniba.dib.sms22238.R;

public class Floor {

    public Bitmap floor;
    boolean isOnTheFloor;
    int x,y;

    public Floor(Resources res, int screenX, int screenY,short flagPlanet){
        if(flagPlanet==0){
            floor = BitmapFactory.decodeResource(res, R.drawable.ground_luna);
        }
        else if(flagPlanet==1){
            floor = BitmapFactory.decodeResource(res, R.drawable.floor_mars);
        }

        x = 0;
        y = screenY - floor.getHeight();

    }

    Rect collisionShape(){
        return new Rect(x,y,x+floor.getWidth(),y+floor.getHeight());
    }


}
